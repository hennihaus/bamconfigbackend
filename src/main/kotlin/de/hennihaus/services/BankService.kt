package de.hennihaus.services

import de.hennihaus.bamdatamodel.Bank
import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.repositories.BankRepository
import de.hennihaus.utils.toUUID
import io.ktor.server.plugins.NotFoundException
import org.koin.core.annotation.Single

@Single
class BankService(private val repository: BankRepository) {

    suspend fun getAllBanks(): List<Bank> = repository.getAll().sortedBy { it.isAsync }.sortedWith { first, second ->
        when {
            first.creditConfiguration == null -> -1
            second.creditConfiguration == null -> 1
            else -> 0
        }
    }

    suspend fun getBankById(id: String): Bank = id.toUUID {
        repository.getById(id = it)
            ?: throw NotFoundException(message = BANK_NOT_FOUND_MESSAGE)
    }

    suspend fun patchBank(id: String, bank: Bank): Bank = id.toUUID { uuid ->
        repository.getById(id = uuid)
            ?.patchBank(new = bank)
            ?.let {
                repository.save(
                    entry = it,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
            ?: throw NotFoundException(message = BANK_NOT_FOUND_MESSAGE)
    }

    suspend fun hasName(name: String): Boolean {
        return repository.getBankIdByName(name = name)
            ?.let { true }
            ?: false
    }

    private fun Bank.patchBank(new: Bank) = copy(
        thumbnailUrl = new.thumbnailUrl,
        isActive = new.isActive,
        creditConfiguration = new.creditConfiguration,
        teams = new.teams,
    )

    companion object {
        const val BANK_NOT_FOUND_MESSAGE = "bank not found by uuid"
    }
}
