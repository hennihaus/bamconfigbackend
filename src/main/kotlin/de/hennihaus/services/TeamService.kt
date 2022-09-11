package de.hennihaus.services

import de.hennihaus.configurations.Configuration.PASSWORD_LENGTH
import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.models.generated.Team
import de.hennihaus.repositories.TeamRepository
import de.hennihaus.utils.toUUID
import io.ktor.server.plugins.NotFoundException
import org.koin.core.annotation.Property
import org.koin.core.annotation.Single
import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator

@Single
class TeamService(
    private val repository: TeamRepository,
    private val statistic: StatisticService,
    @Property(PASSWORD_LENGTH) private val passwordLength: String,
) {

    suspend fun getAllTeams(): List<Team> {
        return repository.getAll()
            .sortedBy { it.username }
            .map { statistic.setHasPassed(team = it) }
    }

    suspend fun getTeamById(id: String): Team = id.toUUID { uuid ->
        repository.getById(id = uuid)
            ?.let { statistic.setHasPassed(team = it) }
            ?: throw NotFoundException(message = TEAM_NOT_FOUND_MESSAGE)
    }

    suspend fun checkUsername(id: String, username: String): Boolean = id.toUUID { uuid ->
        repository.getTeamByUsername(username = username)
            ?.let { it.uuid != uuid }
            ?: false
    }

    suspend fun checkPassword(id: String, password: String): Boolean = id.toUUID { uuid ->
        return repository.getTeamByPassword(password = password)
            ?.let { it.uuid != uuid }
            ?: false
    }

    suspend fun checkJmsQueue(id: String, jmsQueue: String): Boolean = id.toUUID { uuid ->
        return repository.getTeamByJmsQueue(jmsQueue = jmsQueue)
            ?.let { it.uuid != uuid }
            ?: false
    }

    suspend fun saveTeam(team: Team): Team = statistic.setHasPassed(team = team).let {
        repository.save(
            entry = it,
            repetitionAttempts = ONE_REPETITION_ATTEMPT,
        )
    }

    suspend fun deleteTeamById(id: String): Boolean = id.toUUID { uuid ->
        repository.deleteById(id = uuid)
    }

    suspend fun resetAllTeams(): List<Team> {
        return repository.getAll()
            .map {
                resetTeam(team = it)
            }
            .map {
                repository.save(
                    entry = it,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
    }

    suspend fun resetStatistics(id: String): Team = id.toUUID { uuid ->
        repository.getById(id = uuid)
            ?.let {
                it.copy(statistics = it.statistics.mapValues { ZERO_REQUESTS })
            }
            ?.let {
                repository.save(
                    entry = it,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
            ?.let {
                statistic.setHasPassed(team = it)
            }
            ?: throw NotFoundException(message = TEAM_NOT_FOUND_MESSAGE)
    }

    private fun resetTeam(team: Team): Team = team.let {
        it.copy(
            statistics = it.statistics.mapValues { ZERO_REQUESTS },
            hasPassed = false,
            password = PasswordGenerator().generatePassword(
                passwordLength.toInt(),
                CharacterRule(EnglishCharacterData.Alphabetical),
            ),
        )
    }

    companion object {
        internal const val TEAM_NOT_FOUND_MESSAGE = "[team not found by uuid]"
        private const val ZERO_REQUESTS = 0L
    }
}
