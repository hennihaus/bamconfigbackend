package de.hennihaus.repositories

import de.hennihaus.bamdatamodel.Team
import de.hennihaus.repositories.entities.StatisticEntity
import de.hennihaus.repositories.entities.TeamEntity
import de.hennihaus.repositories.mappers.toTeam
import de.hennihaus.repositories.tables.BankTable
import de.hennihaus.repositories.tables.StatisticTable
import de.hennihaus.repositories.tables.StudentTable
import de.hennihaus.repositories.tables.TeamTable
import de.hennihaus.utils.batchUpsert
import de.hennihaus.utils.inTransaction
import de.hennihaus.utils.upsert
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.koin.core.annotation.Single
import java.time.Instant
import java.time.ZonedDateTime
import java.util.UUID

@Single
class TeamRepository : Repository<Team, UUID> {

    override suspend fun getById(id: UUID): Team? = inTransaction {
        TeamEntity.findById(id = id)
            ?.load(relations = getTeamRelations())
            ?.toTeam()
    }

    override suspend fun getAll(): List<Team> = inTransaction {
        TeamEntity.all()
            .with(relations = getTeamRelations())
            .map {
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
        val now = ZonedDateTime.now().toInstant()

        entry.saveTeam(now = now)
        entry.saveStudents(now = now)
        entry.saveStatistics(now = now)

        TeamEntity.findById(id = entry.uuid)
            ?.load(relations = getTeamRelations())
            ?.toTeam()
            ?: throw IllegalStateException(TEAM_NOT_FOUND_MESSAGE)
    }

    suspend fun getTeamIdByUsername(username: String): UUID? = inTransaction {
        TeamTable.slice(column = TeamTable.id)
            .select { TeamTable.username eq username }
            .singleOrNull()
            ?.let { it[TeamTable.id].value }
    }

    suspend fun getTeamIdByPassword(password: String): UUID? = inTransaction {
        TeamTable.slice(columns = listOf(TeamTable.id, TeamTable.password))
            .selectAll()
            .find { it[TeamTable.password] == password }
            ?.let { it[TeamTable.id].value }
    }

    suspend fun getTeamIdByJmsQueue(jmsQueue: String): UUID? = inTransaction {
        TeamTable.slice(column = TeamTable.id)
            .select { TeamTable.jmsQueue eq jmsQueue }
            .singleOrNull()
            ?.let { it[TeamTable.id].value }
    }

    suspend fun getJmsQueueById(id: UUID): String? = inTransaction {
        TeamTable.slice(column = TeamTable.jmsQueue)
            .select { TeamTable.id eq id }
            .singleOrNull()
            ?.let { it[TeamTable.jmsQueue] }
    }

    suspend fun getAllTeamIds(): List<UUID> = inTransaction {
        TeamTable.slice(column = TeamTable.id)
            .selectAll()
            .map {
                it[TeamTable.id].value
            }
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
        val bankIds = BankTable.select { BankTable.name inList statistics.keys }.associate {
            it[BankTable.name] to it[BankTable.id].value
        }

        StatisticTable.batchUpsert(
            data = statistics.toList(),
            conflictColumns = listOf(StatisticTable.bankId, StatisticTable.teamId),
        ) { statisticTable, (bankName, requests) ->
            statisticTable[teamId] = uuid
            statisticTable[bankId] = bankIds[bankName] ?: throw IllegalArgumentException(BANK_NAME_NOT_FOUND_MESSAGE)
            statisticTable[requestsCount] = requests
            statisticTable[lastUpdated] = now
        }
        StatisticTable.deleteWhere {
            (StatisticTable.teamId eq uuid).and(
                op = StatisticTable.bankId notInList bankIds.values,
            )
        }
    }

    private fun getTeamRelations() = arrayOf(
        TeamEntity::students,
        TeamEntity::statistics,
        StatisticEntity::bank,
    )

    companion object {
        private const val TEAM_NOT_FOUND_MESSAGE = "Team not found in database"
        private const val BANK_NAME_NOT_FOUND_MESSAGE = "Bank name not found in database"
    }
}
