package de.hennihaus.routes

import de.hennihaus.models.generated.rest.TeamDTO
import de.hennihaus.routes.TeamRoutes.ID_PATH_PARAMETER
import de.hennihaus.routes.TeamRoutes.JMS_QUEUE_PATH_PARAMETER
import de.hennihaus.routes.TeamRoutes.PASSWORD_PATH_PARAMETER
import de.hennihaus.routes.TeamRoutes.STATISTICS_PATH
import de.hennihaus.routes.TeamRoutes.TEAMS_PATH
import de.hennihaus.routes.TeamRoutes.UNIQUE_PATH
import de.hennihaus.routes.TeamRoutes.USERNAME_PATH_PARAMETER
import de.hennihaus.routes.mappers.toTeam
import de.hennihaus.routes.mappers.toTeamDTO
import de.hennihaus.routes.mappers.toUniqueDTO
import de.hennihaus.services.TeamService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import org.koin.java.KoinJavaComponent.getKoin

object TeamRoutes {
    const val TEAMS_PATH = "teams"
    const val UNIQUE_PATH = "unique"
    const val STATISTICS_PATH = "statistics"

    const val ID_PATH_PARAMETER = "id"
    const val USERNAME_PATH_PARAMETER = "username"
    const val PASSWORD_PATH_PARAMETER = "password"
    const val JMS_QUEUE_PATH_PARAMETER = "jmsQueue"
}

fun Route.registerTeamRoutes() = route(path = "/$TEAMS_PATH") {
    getAllTeams()
    saveTeam()
    getTeamById()
    deleteTeamById()
    isUsernameUnique()
    isPasswordUnique()
    isJmsQueueUnique()
    resetStatistics()
}

private fun Route.getAllTeams() = get {
    val teamService = getKoin().get<TeamService>()

    with(receiver = call) {
        val teams = teamService.getAllTeams()

        respond(message = teams.map { it.toTeamDTO() })
    }
}

private fun Route.getTeamById() = get(path = "/{$ID_PATH_PARAMETER}") {
    val teamService = getKoin().get<TeamService>()

    with(receiver = call) {
        val id = parameters.getOrFail(name = ID_PATH_PARAMETER)

        val team = teamService.getTeamById(id = id)

        respond(message = team.toTeamDTO())
    }
}

private fun Route.isUsernameUnique() = get(
    path = "/{$ID_PATH_PARAMETER}/$UNIQUE_PATH/$USERNAME_PATH_PARAMETER/{$USERNAME_PATH_PARAMETER}",
) {
    val teamService = getKoin().get<TeamService>()

    with(receiver = call) {
        val id = parameters.getOrFail(name = ID_PATH_PARAMETER)
        val username = parameters.getOrFail(name = USERNAME_PATH_PARAMETER)

        val isUnique = teamService.isUsernameUnique(
            id = id,
            username = username,
        )

        respond(message = isUnique.toUniqueDTO())
    }
}

private fun Route.isPasswordUnique() = get(
    path = "/{$ID_PATH_PARAMETER}/$UNIQUE_PATH/$PASSWORD_PATH_PARAMETER/{$PASSWORD_PATH_PARAMETER}",
) {
    val teamService = getKoin().get<TeamService>()

    with(receiver = call) {
        val id = parameters.getOrFail(name = ID_PATH_PARAMETER)
        val password = parameters.getOrFail(name = PASSWORD_PATH_PARAMETER)

        val isUnique = teamService.isPasswordUnique(
            id = id,
            password = password,
        )

        respond(message = isUnique.toUniqueDTO())
    }
}

private fun Route.isJmsQueueUnique() = get(
    path = "/{$ID_PATH_PARAMETER}/$UNIQUE_PATH/$JMS_QUEUE_PATH_PARAMETER/{$JMS_QUEUE_PATH_PARAMETER}",
) {
    val teamService = getKoin().get<TeamService>()

    with(receiver = call) {
        val id = parameters.getOrFail(name = ID_PATH_PARAMETER)
        val jmsQueue = parameters.getOrFail(name = JMS_QUEUE_PATH_PARAMETER)

        val isUnique = teamService.isJmsQueueUnique(
            id = id,
            jmsQueue = jmsQueue,
        )

        respond(message = isUnique.toUniqueDTO())
    }
}

private fun Route.saveTeam() = put(path = "/{$ID_PATH_PARAMETER}") {
    val teamService = getKoin().get<TeamService>()

    with(receiver = call) {
        val dto = receive<TeamDTO>()

        val team = teamService.saveTeam(team = dto.toTeam())

        respond(message = team.toTeamDTO())
    }
}

private fun Route.deleteTeamById() = delete(path = "/{$ID_PATH_PARAMETER}") {
    val teamService = getKoin().get<TeamService>()

    with(receiver = call) {
        val id = parameters.getOrFail(name = ID_PATH_PARAMETER)

        val message = teamService.deleteTeamById(id = id).let { "" }

        respond(
            message = message,
            status = HttpStatusCode.NoContent,
        )
    }
}

private fun Route.resetStatistics() = delete(path = "/{$ID_PATH_PARAMETER}/$STATISTICS_PATH") {
    val teamService = getKoin().get<TeamService>()

    with(receiver = call) {
        val id = parameters.getOrFail(name = ID_PATH_PARAMETER)

        val team = teamService.resetStatistics(id = id)

        respond(message = team.toTeamDTO())
    }
}
