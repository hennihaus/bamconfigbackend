package de.hennihaus.services

import de.hennihaus.bamdatamodel.Statistic
import de.hennihaus.repositories.StatisticRepository
import de.hennihaus.repositories.StatisticRepository.Companion.ZERO_REQUESTS
import de.hennihaus.utils.toUUID
import io.ktor.server.plugins.NotFoundException
import org.koin.core.annotation.Single

@Single
class StatisticService(private val repository: StatisticRepository) {

    suspend fun saveStatistics(bankId: String) = bankId.toUUID { uuid ->
        repository.saveAll(
            bankId = uuid,
        )
    }

    suspend fun deleteStatistics(bankId: String) = bankId.toUUID { uuid ->
        repository.deleteAll(
            bankId = uuid,
        )
    }

    suspend fun recreateStatistics(limit: Long) = repository.recreateAll(
        limit = limit.takeIf { limit > ZERO_REQUESTS } ?: ZERO_REQUESTS,
    )

    suspend fun incrementRequest(statistic: Statistic): Statistic {
        return repository.incrementRequest(entry = statistic)
            ?: throw NotFoundException(message = STATISTIC_NOT_FOUND_MESSAGE)
    }

    companion object {
        const val STATISTIC_NOT_FOUND_MESSAGE = "statistic not found by bankId and teamId"
    }
}
