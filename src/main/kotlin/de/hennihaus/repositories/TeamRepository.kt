package de.hennihaus.repositories

import de.hennihaus.models.generated.Team
import de.hennihaus.repositories.entities.BankEntity
import de.hennihaus.repositories.entities.TeamEntity
import de.hennihaus.repositories.mappers.toTeam
import de.hennihaus.repositories.tables.BankTable
import de.hennihaus.repositories.tables.StatisticTable
import de.hennihaus.repositories.tables.StudentTable
import de.hennihaus.repositories.tables.TeamTable
import de.hennihaus.utils.batchUpsert
import de.hennihaus.utils.inTransaction
import de.hennihaus.utils.upsert
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class TeamRepository : Repository<Team, UUID> {

    override suspend fun getById(id: UUID): Team? = inTransaction {
        TeamEntity.findById(id = id)
            ?.toTeam()
    }

    override suspend fun getAll(): List<Team> = inTransaction {
        TeamEntity.all().map {
            it.toTeam()
        }
    }

    override suspend fun deleteById(id: UUID): Boolean = inTransaction {
        TeamEntity.findById(id = id)
            ?.delete()
            ?.let { true }
            ?: false
    }

    override suspend fun save(entry: Team, repetitionAttempts: Int): Team = inTransaction(
        repetitionAttempts = repetitionAttempts,
    ) {
        val now = Clock.System.now()

        entry.saveTeam(now = now)
        entry.saveStudents(now = now)
        entry.saveStatistics(now = now)

        TeamEntity.findById(id = entry.uuid)
            ?.toTeam()
            ?: throw IllegalStateException(TEAM_NOT_FOUND_MESSAGE)
    }

    suspend fun getTeamByUsername(username: String): Team? = inTransaction {
        TeamEntity.find { TeamTable.username eq username }
            .singleOrNull()
            ?.toTeam()
    }

    suspend fun getTeamByPassword(password: String): Team? = inTransaction {
        TeamEntity.all().find { it.password == password }
            ?.toTeam()
    }

    suspend fun getTeamByJmsQueue(jmsQueue: String): Team? = inTransaction {
        TeamEntity.find { TeamTable.jmsQueue eq jmsQueue }
            .singleOrNull()
            ?.toTeam()
    }

    private fun Team.saveTeam(now: Instant) = TeamTable.upsert(conflictColumns = listOf(TeamTable.id)) { teamTable ->
        teamTable[id] = this@saveTeam.uuid
        teamTable[username] = this@saveTeam.username
        teamTable[password] = this@saveTeam.password
        teamTable[jmsQueue] = this@saveTeam.jmsQueue
        teamTable[lastUpdated] = now
    }

    private fun Team.saveStudents(now: Instant) {
        StudentTable.batchUpsert(data = students, conflictColumns = listOf(StudentTable.id)) { studentTable, student ->
            studentTable[id] = student.uuid
            studentTable[teamId] = uuid
            studentTable[firstname] = student.firstname
            studentTable[lastname] = student.lastname
            studentTable[lastUpdated] = now
        }
        StudentTable.deleteWhere {
            StudentTable.teamId eq uuid and (StudentTable.id notInList students.map { it.uuid })
        }
    }

    private fun Team.saveStatistics(now: Instant) {
        val banks = BankEntity.find { BankTable.name inList statistics.keys }

        StatisticTable.batchUpsert(
            data = statistics.toList(),
            conflictColumns = listOf(StatisticTable.bankId, StatisticTable.teamId),
        ) { statisticTable, (bankName, requests) ->
            statisticTable[teamId] = uuid
            statisticTable[bankId] = banks.findId(bankName = bankName)
            statisticTable[requestsCount] = requests
            statisticTable[lastUpdated] = now
        }
        StatisticTable.deleteWhere {
            (StatisticTable.teamId eq uuid).and(
                op = StatisticTable.bankId notInList statistics.map { (bankName, _) ->
                    banks.findId(bankName = bankName)
                },
            )
        }
    }

    private fun SizedIterable<BankEntity>.findId(bankName: String): UUID {
        return find { bank -> bank.name == bankName }
            ?.id
            ?.value
            ?: throw IllegalArgumentException(BANK_NAME_NOT_FOUND_MESSAGE)
    }

    companion object {
        private const val TEAM_NOT_FOUND_MESSAGE = "Team not found in database"
        private const val BANK_NAME_NOT_FOUND_MESSAGE = "Bank name not found in database"
    }
}
