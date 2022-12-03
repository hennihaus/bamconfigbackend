package de.hennihaus.repositories

import de.hennihaus.bamdatamodel.Bank
import de.hennihaus.repositories.entities.BankEntity
import de.hennihaus.repositories.mappers.toBank
import de.hennihaus.repositories.tables.BankTable
import de.hennihaus.repositories.tables.CreditConfigurationTable
import de.hennihaus.utils.inTransaction
import de.hennihaus.utils.upsert
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import java.time.Instant
import java.time.ZonedDateTime
import java.util.UUID

@Single
class BankRepository {

    // TODO: Add timestamps in examples
    suspend fun getById(id: UUID): Bank? = inTransaction {
        BankEntity.findById(id = id)
            ?.load(relations = getBankRelations())
            ?.toBank()
    }

    suspend fun getAll(): List<Bank> = inTransaction {
        BankEntity.all()
            .with(relations = getBankRelations())
            .map {
                it.toBank()
            }
    }

    suspend fun deleteById(id: UUID): Boolean = inTransaction {
        BankEntity.findById(id = id)
            ?.delete()
            ?.let { true }
            ?: false
    }

    /**
     * Only changing rather than adding banks since there is no logic to connect a bank with a related task.
     */
    suspend fun save(entry: Bank, repetitionAttempts: Int): Bank = inTransaction(
        repetitionAttempts = repetitionAttempts,
    ) {
        val now = ZonedDateTime.now().toInstant()

        entry.saveBank(now = now)
        entry.saveCreditConfiguration()

        BankEntity.findById(id = entry.uuid)
            ?.load(relations = getBankRelations())
            ?.toBank()
            ?: throw IllegalStateException(BANK_NOT_FOUND_MESSAGE)
    }

    suspend fun getBankIdByName(name: String): UUID? = inTransaction {
        BankTable.slice(column = BankTable.id)
            .select { BankTable.name eq name }
            .singleOrNull()
            ?.let { it[BankTable.id].value }
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
                creditConfigurationTable[minAmountInEuros] = it.minAmountInEuros
                creditConfigurationTable[maxAmountInEuros] = it.maxAmountInEuros
                creditConfigurationTable[minTermInMonths] = it.minTermInMonths
                creditConfigurationTable[maxTermInMonths] = it.maxTermInMonths
                creditConfigurationTable[minSchufaRating] = it.minSchufaRating.name
                creditConfigurationTable[maxSchufaRating] = it.maxSchufaRating.name
            }
        }
    }

    private fun getBankRelations() = arrayOf(
        BankEntity::creditConfiguration,
    )

    companion object {
        private const val BANK_NOT_FOUND_MESSAGE = "Bank not found in database"
    }
}
