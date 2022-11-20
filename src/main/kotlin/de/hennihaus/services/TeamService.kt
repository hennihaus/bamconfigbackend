package de.hennihaus.services

import de.hennihaus.bamdatamodel.Team
import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.models.cursors.TeamCursor
import de.hennihaus.models.cursors.TeamPagination
import de.hennihaus.repositories.StatisticRepository
import de.hennihaus.repositories.StatisticRepository.Companion.ZERO_REQUESTS
import de.hennihaus.repositories.TeamRepository
import de.hennihaus.utils.toUUID
import io.ktor.server.plugins.NotFoundException
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class TeamService(
    private val teamRepository: TeamRepository,
    private val statisticRepository: StatisticRepository,
    private val taskService: TaskService,
    private val githubService: GithubService,
    private val cursorService: CursorService,
) {

    suspend fun getAllTeams(cursor: TeamCursor): TeamPagination {
        val teams = teamRepository.getAll(cursor = cursor).sortedBy { it.username }

        return cursorService.buildPagination(
            cursor = cursor,
            positionSupplier = { it.username },
            positionFallback = USERNAME_POSITION_FALLBACK,
            items = teams,
            limit = cursor.query.limit,
        )
    }

    suspend fun getTeamById(id: String): Team = id.toUUID { uuid ->
        teamRepository.getById(id = uuid)
            ?: throw NotFoundException(message = TEAM_NOT_FOUND_MESSAGE)
    }

    suspend fun isTypeUnique(id: String, type: String): Boolean = id.toUUID { uuid ->
        teamRepository.getTeamIdByType(type = type)
            ?.let { it == uuid }
            ?: true
    }

    suspend fun isUsernameUnique(id: String, username: String): Boolean = id.toUUID { uuid ->
        teamRepository.getTeamIdByUsername(username = username)
            ?.let { it == uuid }
            ?: true
    }

    suspend fun isPasswordUnique(id: String, password: String): Boolean = id.toUUID { uuid ->
        teamRepository.getTeamIdByPassword(password = password)
            ?.let { it == uuid }
            ?: true
    }

    suspend fun isJmsQueueUnique(id: String, jmsQueue: String): Boolean = id.toUUID { uuid ->
        teamRepository.getTeamIdByJmsQueue(jmsQueue = jmsQueue)
            ?.let { it == uuid }
            ?: true
    }

    suspend fun saveTeam(team: Team): Team {
        return teamRepository
            .save(
                entry = team.copy(
                    statistics = team.statistics.mapValues { (_, requests) ->
                        if (requests < ZERO_REQUESTS) ZERO_REQUESTS else requests
                    },
                ),
                repetitionAttempts = ONE_REPETITION_ATTEMPT,
            )
            .also {
                if (it.type == TeamType.EXAMPLE) taskService.patchParameters(
                    username = it.username,
                    password = it.password,
                )
            }
            .also {
                if (it.type == TeamType.EXAMPLE) githubService.updateOpenApi(
                    team = it,
                )
            }
    }

    suspend fun getJmsQueueById(id: String): String? = id.toUUID { uuid ->
        teamRepository.getJmsQueueById(id = uuid)
    }

    suspend fun deleteTeamById(id: String): Boolean = id.toUUID { uuid ->
        teamRepository.deleteById(id = uuid)
    }

    suspend fun resetAllTeams(): List<UUID> = teamRepository.resetAllTeams(
        repetitionAttempts = ONE_REPETITION_ATTEMPT,
    )

    suspend fun resetStatistics(id: String): Team = id.toUUID { uuid ->
        statisticRepository.resetRequests(
            teamId = uuid,
            repetitionAttempts = ONE_REPETITION_ATTEMPT,
        )
        teamRepository.getById(id = uuid)
            ?: throw NotFoundException(message = TEAM_NOT_FOUND_MESSAGE)
    }

    companion object {
        const val TEAM_NOT_FOUND_MESSAGE = "team not found by uuid"
        const val USERNAME_POSITION_FALLBACK = ""
    }
}
