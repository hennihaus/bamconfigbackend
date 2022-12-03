package de.hennihaus.repositories

import de.hennihaus.bamdatamodel.Statistic
import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.repositories.entities.StatisticEntity
import de.hennihaus.repositories.mappers.toStatistic
import de.hennihaus.repositories.tables.BankTable
import de.hennihaus.repositories.tables.StatisticTable
import de.hennihaus.repositories.tables.TeamTable
import de.hennihaus.repositories.types.uuidParam
import de.hennihaus.utils.inTransaction
import de.hennihaus.utils.upsert
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.timestampParam
import org.jetbrains.exposed.sql.longParam
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.stringParam
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import java.time.Instant
import java.time.ZonedDateTime
import java.util.UUID

@Single
class StatisticRepository {

    suspend fun saveAll(bankId: UUID): Unit = inTransaction {
        val now = ZonedDateTime.now().toInstant()

        StatisticTable.upsert(
            conflictColumns = listOf(
                StatisticTable.bankId,
                StatisticTable.teamId,
            ),
            excludedColumnsFromUpdate = listOf(
                StatisticTable.requestsCount,
            ),
            selectQuery = TeamTable
                .slice(
                    columns = getStatisticColumns(
                        bankId = bankId,
                        now = now,
                    ),
                )
                .selectAll(),
        )
    }

    suspend fun deleteAll(bankId: UUID): Unit = inTransaction {
        StatisticTable.deleteWhere {
            StatisticTable.bankId eq bankId
        }
    }

    suspend fun recreateAll(limit: Long): Unit = inTransaction {
        val now = ZonedDateTime.now().toInstant()

        deleteStatistics()
        insertExampleStatistics(now = now)
        insertRegularStatistics(limit = limit, now = now)
    }

    suspend fun incrementRequest(entry: Statistic): Statistic? = inTransaction {
        StatisticTable.update(
            where = {
                StatisticTable.bankId eq entry.bankId and (StatisticTable.teamId eq entry.teamId)
            },
        ) { statisticTable ->
            with(SqlExpressionBuilder) {
                statisticTable[requestsCount] = requestsCount + ONE_REQUEST
            }
        }

        StatisticEntity.find { StatisticTable.bankId eq entry.bankId and (StatisticTable.teamId eq entry.teamId) }
            .singleOrNull()
            ?.toStatistic()
    }

    suspend fun resetRequests(teamId: UUID, repetitionAttempts: Int): List<Statistic> = inTransaction(
        repetitionAttempts = repetitionAttempts,
    ) {
        StatisticTable.update(
            where = {
                StatisticTable.teamId eq teamId
            },
        ) { statisticTable ->
            with(SqlExpressionBuilder) {
                statisticTable[requestsCount] = ZERO_REQUESTS
            }
        }

        StatisticEntity.find { StatisticTable.teamId eq teamId }.map {
            it.toStatistic()
        }
    }

    private fun deleteStatistics() = StatisticTable.deleteWhere {
        StatisticTable.bankId inSubQuery (BankTable.slice(column = BankTable.id)
            .select { BankTable.isAsync eq true })
    }

    private fun insertExampleStatistics(now: Instant) {
        val selectQuery = TeamTable
            .join(
                joinType = JoinType.INNER,
                otherTable = BankTable,
                onColumn = TeamTable.type,
                otherColumn = stringParam(
                    value = TeamType.EXAMPLE.name,
                ),
            )
            .slice(
                columns = getStatisticColumns(
                    now = now,
                ),
            )
            .select {
                BankTable.isAsync eq true and (BankTable.isActive eq true)
            }

        StatisticTable.insert(selectQuery = selectQuery)
    }

    private fun insertRegularStatistics(limit: Long, now: Instant) {
        val teamQuery = TeamTable
            .slice(
                columns = getStatisticColumns(
                    now = now,
                ),
            )
            .selectAll()
        val bankQuery = BankTable
            .slice(
                column = BankTable.id,
            )
            .select {
                BankTable.isAsync eq true and (BankTable.isActive eq true)
            }

        val query = buildString {
            append("INSERT INTO statistic")
            append(" ")
            append("(bank_uuid, team_uuid, statistic_requests_count, statistic_updated_timestamp_with_time_zone)")
            append(" ")
            append(teamQuery.prepareSQL(builder = QueryBuilder(prepared = false)))
            append(" ")
            append("INNER JOIN LATERAL")
            append(" ")
            append("( ")
            append(bankQuery.prepareSQL(builder = QueryBuilder(prepared = false)))
            append(" AND team.team_uuid = team.team_uuid ORDER BY random() LIMIT ")
            append(limit)
            append(")")
            append(" ")
            append("as bank ON true WHERE team_type = '${TeamType.REGULAR.name}'")
        }

        query.executeInsert()
    }

    private fun getStatisticColumns(now: Instant) = listOf(
        BankTable.id,
        TeamTable.id,
        longParam(value = ZERO_REQUESTS),
        timestampParam(value = now),
    )

    private fun getStatisticColumns(bankId: UUID, now: Instant) = listOf(
        uuidParam(value = bankId),
        TeamTable.id,
        longParam(value = ZERO_REQUESTS),
        timestampParam(value = now),
    )

    private fun String.executeInsert() = TransactionManager.current().exec(
        stmt = this,
    )

    companion object {
        const val ZERO_REQUESTS = 0L
        const val ONE_REQUEST = 1L
    }
}
