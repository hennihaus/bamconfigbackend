package de.hennihaus.repositories

import de.hennihaus.bamdatamodel.Team
import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.configurations.Configuration.PASSWORD_LENGTH
import de.hennihaus.models.cursors.TeamCursor
import de.hennihaus.repositories.StatisticRepository.Companion.ZERO_REQUESTS
import de.hennihaus.repositories.entities.StatisticEntity
import de.hennihaus.repositories.entities.TeamEntity
import de.hennihaus.repositories.mappers.toTeam
import de.hennihaus.repositories.queries.TeamQueryBuilder
import de.hennihaus.repositories.tables.BankTable
import de.hennihaus.repositories.tables.StatisticTable
import de.hennihaus.repositories.tables.StudentTable
import de.hennihaus.repositories.tables.TeamTable
import de.hennihaus.utils.batchUpsert
import de.hennihaus.utils.inTransaction
import de.hennihaus.utils.upsert
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.CustomStringFunction
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.intParam
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Property
import org.koin.core.annotation.Single
import java.time.Instant
import java.time.OffsetDateTime
import java.util.UUID

@Single
class TeamRepository(
    private val queryBuilder: TeamQueryBuilder,
    @Property(PASSWORD_LENGTH) private val passwordLength: String,
) {

    suspend fun getById(id: UUID): Team? = inTransaction {
        TeamEntity.findById(id = id)
            ?.load(relations = getTeamRelations())
            ?.toTeam()
    }

    suspend fun getAll(cursor: TeamCursor): List<Team> = inTransaction {
        val rows = queryBuilder.buildTeamPaginationQuery(
            cursor = cursor,
        )

        TeamEntity.wrapRows(rows = rows)
            .with(relations = getTeamRelations())
            .map {
                it.toTeam()
            }
    }

    suspend fun deleteById(id: UUID): Boolean = inTransaction {
        TeamEntity.findById(id = id)
            ?.delete()
            ?.let { true }
            ?: false
    }

    suspend fun save(entry: Team, repetitionAttempts: Int): Team = inTransaction(
        repetitionAttempts = repetitionAttempts,
    ) {
        val now = OffsetDateTime.now().toInstant()

        entry.saveTeam(now = now)
        entry.saveStudents(now = now)
        entry.saveStatistics(now = now)

        TeamEntity.findById(id = entry.uuid)
            ?.load(relations = getTeamRelations())
            ?.toTeam()
            ?: throw IllegalStateException(TEAM_NOT_FOUND_MESSAGE)
    }

    suspend fun getTeamIdByType(type: TeamType): UUID? = inTransaction {
        TeamTable.slice(column = TeamTable.id)
            .select { TeamTable.type eq type.name }
            .firstOrNull()
            ?.let { it[TeamTable.id].value }
    }

    suspend fun getTeamIdByUsername(username: String): UUID? = inTransaction {
        TeamTable.slice(column = TeamTable.id)
            .select { TeamTable.username eq username }
            .singleOrNull()
            ?.let { it[TeamTable.id].value }
    }

    suspend fun getTeamIdByPassword(password: String): UUID? = inTransaction {
        TeamTable.slice(column = TeamTable.id)
            .select { TeamTable.password eq password }
            .singleOrNull()
            ?.let { it[TeamTable.id].value }
    }

    suspend fun getTeamIdByJmsQueue(jmsQueue: String): UUID? = inTransaction {
        TeamTable.slice(column = TeamTable.id)
            .select { TeamTable.jmsQueue eq jmsQueue }
            .singleOrNull()
            ?.let { it[TeamTable.id].value }
    }

    suspend fun resetAllTeams(repetitionAttempts: Int): List<UUID> = inTransaction(
        repetitionAttempts = repetitionAttempts,
    ) {
        TeamTable.update(where = { TeamTable.type neq TeamType.EXAMPLE.name }) {
            with(SqlExpressionBuilder) {
                it.update(
                    column = password,
                    value = CustomStringFunction(
                        functionName = "RANDOM_STRING",
                        params = arrayOf(
                            intParam(
                                value = passwordLength.toInt(),
                            ),
                        ),
                    ),
                )
            }
        }
        StatisticTable.update { statisticTable ->
            with(SqlExpressionBuilder) {
                statisticTable[requestsCount] = ZERO_REQUESTS
            }
        }

        TeamTable.slice(column = TeamTable.id)
            .selectAll()
            .map {
                it[TeamTable.id].value
            }
    }

    private fun Team.saveTeam(now: Instant) = TeamTable.upsert(conflictColumns = listOf(TeamTable.id)) { teamTable ->
        teamTable[id] = this@saveTeam.uuid
        teamTable[type] = this@saveTeam.type.name
        teamTable[username] = this@saveTeam.username
        teamTable[password] = this@saveTeam.password
        teamTable[jmsQueue] = this@saveTeam.jmsQueue
        teamTable[updated] = now
    }

    private fun Team.saveStudents(now: Instant) {
        StudentTable.batchUpsert(data = students, conflictColumns = listOf(StudentTable.id)) { studentTable, student ->
            studentTable[id] = student.uuid
            studentTable[teamId] = uuid
            studentTable[firstname] = student.firstname
            studentTable[lastname] = student.lastname
            studentTable[updated] = now
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
            statisticTable[updated] = now
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
