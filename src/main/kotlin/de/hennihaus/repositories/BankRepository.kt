package de.hennihaus.repositories

import de.hennihaus.models.generated.Bank
import de.hennihaus.repositories.entities.BankEntity
import de.hennihaus.repositories.mappers.toBank
import de.hennihaus.repositories.tables.BankTable
import de.hennihaus.repositories.tables.CreditConfigurationTable
import de.hennihaus.repositories.tables.StatisticTable
import de.hennihaus.utils.batchUpsert
import de.hennihaus.utils.inTransaction
import de.hennihaus.utils.upsert
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class BankRepository : Repository<Bank, UUID> {

    override suspend fun getById(id: UUID): Bank? = inTransaction {
        BankEntity.findById(id = id)
            ?.toBank()
    }

    override suspend fun getAll(): List<Bank> = inTransaction {
        BankEntity.all().map {
            it.toBank()
        }
    }

    override suspend fun deleteById(id: UUID): Boolean = inTransaction {
        BankEntity.findById(id = id)
            ?.delete()
            ?.let { true }
            ?: false
    }

    /**
     * Only changing rather than adding banks since there is no logic to connect a bank with a related task.
     */
    override suspend fun save(entry: Bank, repetitionAttempts: Int): Bank = inTransaction(
        repetitionAttempts = repetitionAttempts,
    ) {
        val now = Clock.System.now()

        entry.saveBank(now = now)
        entry.saveCreditConfiguration()
        entry.saveStatistics(now = now)

        BankEntity.findById(id = entry.uuid)
            ?.toBank()
            ?: throw IllegalStateException(BANK_NOT_FOUND_MESSAGE)
    }

    private fun Bank.saveBank(now: Instant) = BankTable.update(
        where = {
            BankTable.id eq uuid
        },
    ) { bankTable ->
        bankTable[id] = this@saveBank.uuid
        bankTable[name] = this@saveBank.name
        bankTable[jmsQueue] = this@saveBank.jmsQueue
        bankTable[thumbnailUrl] = "${this@saveBank.thumbnailUrl}"
        bankTable[isAsync] = this@saveBank.isAsync
        bankTable[isActive] = this@saveBank.isActive
        bankTable[lastUpdated] = now
    }

    private fun Bank.saveCreditConfiguration() {
        CreditConfigurationTable.deleteWhere {
            CreditConfigurationTable.bankId eq uuid
        }
        creditConfiguration?.let {
            CreditConfigurationTable.upsert(
                conflictColumns = listOf(CreditConfigurationTable.bankId),
            ) { creditConfigurationTable ->
                creditConfigurationTable[bankId] = uuid
                creditConfigurationTable[minAmountInEuros] = creditConfiguration.minAmountInEuros
                creditConfigurationTable[maxAmountInEuros] = creditConfiguration.maxAmountInEuros
                creditConfigurationTable[minTermInMonths] = creditConfiguration.minTermInMonths
                creditConfigurationTable[maxTermInMonths] = creditConfiguration.maxTermInMonths
                creditConfigurationTable[minSchufaRating] = creditConfiguration.minSchufaRating.value
                creditConfigurationTable[maxSchufaRating] = creditConfiguration.maxSchufaRating.value
            }
        }
    }

    private fun Bank.saveStatistics(now: Instant) {
        StatisticTable.batchUpsert(
            data = teams,
            conflictColumns = listOf(StatisticTable.bankId, StatisticTable.teamId),
            excludedColumnsFromUpdate = listOf(StatisticTable.requestsCount),
        ) { statisticTable, team ->
            statisticTable[bankId] = uuid
            statisticTable[teamId] = team.uuid
            statisticTable[requestsCount] = ZERO_REQUESTS
            statisticTable[lastUpdated] = now
        }
        StatisticTable.deleteWhere {
            StatisticTable.bankId eq uuid and (StatisticTable.teamId notInList teams.map { it.uuid })
        }
    }

    companion object {
        private const val BANK_NOT_FOUND_MESSAGE = "Bank not found in database"
        private const val ZERO_REQUESTS = 0L
    }
}
