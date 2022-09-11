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

    companion object {
        private const val ONE_REQUEST = 1L
    }
}
