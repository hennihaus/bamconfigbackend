package de.hennihaus.services

import de.hennihaus.bamdatamodel.Statistic
import de.hennihaus.repositories.StatisticRepository
import io.ktor.server.plugins.NotFoundException
import org.koin.core.annotation.Single

@Single
class StatisticService(private val repository: StatisticRepository) {

    suspend fun incrementRequest(statistic: Statistic): Statistic {
        return repository.incrementRequest(entry = statistic)
            ?: throw NotFoundException(message = STATISTIC_NOT_FOUND_MESSAGE)
    }

    companion object {
        const val ZERO_REQUESTS = 0L

        const val STATISTIC_NOT_FOUND_MESSAGE = "[statistic not found by bankId and teamId]"
    }
}
