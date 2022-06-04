package de.hennihaus.services

import de.hennihaus.models.Group
import de.hennihaus.repositories.BankRepository
import org.koin.core.annotation.Single

@Single
class StatsServiceImpl(private val repository: BankRepository) : StatsService {

    override suspend fun setHasPassed(group: Group): Group = group.copy(
        hasPassed = repository.getAll()
            .filter { bank -> if (bank.isAsync) bank.groups.any { it.id == group.id } else true }
            .filter { bank -> if (bank.isAsync) bank.isActive else true }
            .none { bank -> group.stats[bank.jmsQueue] == ZERO_REQUESTS }
    )

    companion object {
        private const val ZERO_REQUESTS = 0
    }
}
