package de.hennihaus.services

import de.hennihaus.models.Bank
import de.hennihaus.plugins.NotFoundException
import de.hennihaus.repositories.BankRepository
import org.koin.core.annotation.Single

@Single
class BankServiceImpl(private val repository: BankRepository) : BankService {

    override suspend fun getAllBanks(): List<Bank> = repository.getAll()

    override suspend fun getBankByJmsTopic(jmsTopic: String): Bank =
        repository.getById(jmsTopic) ?: throw NotFoundException(ID_MESSAGE)

    override suspend fun updateBank(bank: Bank): Bank = repository.save(bank)

    override suspend fun updateAllBanks(banks: List<Bank>): List<Bank> = banks.map { updateBank(it) }

    companion object {
        internal const val ID_MESSAGE = "No Bank for given ID found!"
    }
}
