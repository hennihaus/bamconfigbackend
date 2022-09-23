package de.hennihaus.services

import de.hennihaus.bamdatamodel.Team
import de.hennihaus.configurations.Configuration.PASSWORD_LENGTH
import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.repositories.StatisticRepository
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
    private val teamRepository: TeamRepository,
    private val statisticRepository: StatisticRepository,
    @Property(PASSWORD_LENGTH) private val passwordLength: String,
) {

    suspend fun getAllTeams(): List<Team> = teamRepository.getAll().sortedBy { it.username }

    suspend fun getTeamById(id: String): Team = id.toUUID { uuid ->
        teamRepository.getById(id = uuid)
            ?: throw NotFoundException(message = TEAM_NOT_FOUND_MESSAGE)
    }

    suspend fun checkUsername(id: String, username: String): Boolean = id.toUUID { uuid ->
        teamRepository.getTeamByUsername(username = username)
            ?.let { it.uuid != uuid }
            ?: false
    }

    suspend fun checkPassword(id: String, password: String): Boolean = id.toUUID { uuid ->
        teamRepository.getTeamByPassword(password = password)
            ?.let { it.uuid != uuid }
            ?: false
    }

    suspend fun checkJmsQueue(id: String, jmsQueue: String): Boolean = id.toUUID { uuid ->
        teamRepository.getTeamByJmsQueue(jmsQueue = jmsQueue)
            ?.let { it.uuid != uuid }
            ?: false
    }

    suspend fun saveTeam(team: Team): Team = teamRepository.save(
        entry = team,
        repetitionAttempts = ONE_REPETITION_ATTEMPT,
    )

    suspend fun deleteTeamById(id: String): Boolean = id.toUUID { uuid ->
        teamRepository.deleteById(id = uuid)
    }

    suspend fun resetAllTeams(): List<Team> {
        return teamRepository.getAll()
            .map {
                resetTeam(team = it)
            }
            .map {
                teamRepository.save(
                    entry = it,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
    }

    suspend fun resetStatistics(id: String): Team = id.toUUID { uuid ->
        statisticRepository.resetRequests(
            teamId = uuid,
            repetitionAttempts = ONE_REPETITION_ATTEMPT,
        )
        teamRepository.getById(id = uuid)
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
        const val TEAM_NOT_FOUND_MESSAGE = "[team not found by uuid]"
        const val ZERO_REQUESTS = 0L
    }
}
