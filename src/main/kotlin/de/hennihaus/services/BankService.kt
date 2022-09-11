package de.hennihaus.services

import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.models.generated.Bank
import de.hennihaus.repositories.BankRepository
import de.hennihaus.utils.toUUID
import io.ktor.server.plugins.NotFoundException
import org.koin.core.annotation.Single

@Single
class BankService(private val repository: BankRepository, private val statistic: StatisticService) {

    suspend fun getAllBanks(): List<Bank> = repository.getAll().map { bank ->
        bank.copy(teams = bank.teams.map { team -> statistic.setHasPassed(team = team) })
    }

    suspend fun getBankById(id: String): Bank = id.toUUID {
        return repository.getById(id = it)
            ?.let { bank ->
                bank.copy(teams = bank.teams.map { team -> statistic.setHasPassed(team = team) })
            }
            ?: throw NotFoundException(message = BANK_NOT_FOUND_MESSAGE)
    }

    suspend fun saveBank(bank: Bank): Bank = repository.save(
        entry = bank,
        repetitionAttempts = ONE_REPETITION_ATTEMPT,
    ).let {
        bank.copy(teams = bank.teams.map { team -> statistic.setHasPassed(team = team) })
    }

    companion object {
        internal const val BANK_NOT_FOUND_MESSAGE = "[bank not found by uuid]"
    }
}
