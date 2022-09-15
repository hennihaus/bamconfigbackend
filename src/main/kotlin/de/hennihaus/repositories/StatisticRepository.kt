package de.hennihaus.repositories

import de.hennihaus.models.generated.Statistic
import de.hennihaus.repositories.entities.StatisticEntity
import de.hennihaus.repositories.mappers.toStatistic
import de.hennihaus.repositories.tables.StatisticTable
import de.hennihaus.utils.inTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class StatisticRepository {

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

    companion object {
        const val ZERO_REQUESTS = 0L
        const val ONE_REQUEST = 1L
    }
}
