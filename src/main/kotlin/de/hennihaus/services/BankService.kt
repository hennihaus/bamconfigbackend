package de.hennihaus.services

import de.hennihaus.bamdatamodel.Bank
import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.repositories.BankRepository
import de.hennihaus.utils.toUUID
import io.ktor.server.plugins.NotFoundException
import org.koin.core.annotation.Single

@Single
class BankService(private val repository: BankRepository) {

    suspend fun getAllBanks(): List<Bank> = repository.getAll()

    suspend fun getBankById(id: String): Bank = id.toUUID {
        repository.getById(id = it)
            ?: throw NotFoundException(message = BANK_NOT_FOUND_MESSAGE)
    }

    suspend fun saveBank(bank: Bank): Bank = repository.save(
        entry = bank,
        repetitionAttempts = ONE_REPETITION_ATTEMPT,
    )

    companion object {
        const val BANK_NOT_FOUND_MESSAGE = "[bank not found by uuid]"
    }
}
