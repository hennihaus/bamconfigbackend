package de.hennihaus.services

import de.hennihaus.models.Bank
import de.hennihaus.repositories.BankRepository
import io.ktor.server.plugins.NotFoundException
import org.koin.core.annotation.Single

@Single
class BankServiceImpl(private val repository: BankRepository, private val stats: StatsService) : BankService {

    override suspend fun getAllBanks(): List<Bank> = repository.getAll().map {
        it.copy(groups = it.groups.map { group -> stats.setHasPassed(group = group) })
    }

    override suspend fun getBankByJmsQueue(jmsQueue: String): Bank {
        val bank = repository.getById(jmsQueue) ?: throw NotFoundException(message = BANK_NOT_FOUND_MESSAGE)
        return bank.copy(groups = bank.groups.map { group -> stats.setHasPassed(group = group) })
    }

    override suspend fun saveBank(bank: Bank): Bank = repository.save(bank)

    override suspend fun saveAllBanks(banks: List<Bank>): List<Bank> = banks.map { saveBank(it) }

    companion object {
        internal const val BANK_NOT_FOUND_MESSAGE = "[bank not found by jmsQueue]"
    }
}
