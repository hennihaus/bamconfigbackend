package de.hennihaus.services

import de.hennihaus.models.generated.Statistic
import de.hennihaus.models.generated.Team
import de.hennihaus.repositories.BankRepository
import de.hennihaus.repositories.StatisticRepository
import io.ktor.server.plugins.NotFoundException
import org.koin.core.annotation.Single

@Single
class StatisticService(
    private val statisticRepository: StatisticRepository,
    private val bankRepository: BankRepository,
) {

    suspend fun incrementRequest(statistic: Statistic): Statistic {
        return statisticRepository.incrementRequest(entry = statistic)
            ?: throw NotFoundException(message = STATISTIC_NOT_FOUND_MESSAGE)
    }

    suspend fun setHasPassed(team: Team): Team = team.copy(
        hasPassed = bankRepository.getAll()
            .filter { bank -> bank.name in team.statistics }
            .filter { bank -> if (bank.isAsync) bank.isActive else true }
            .none { bank -> team.statistics[bank.name] == ZERO_REQUESTS }
    )

    companion object {
        const val ZERO_REQUESTS = 0L

        const val STATISTIC_NOT_FOUND_MESSAGE = "[statistic not found by bankId and teamId]"
    }
}
