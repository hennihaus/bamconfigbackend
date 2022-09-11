package de.hennihaus.routes

import de.hennihaus.models.generated.ExistsResponse
import de.hennihaus.routes.resources.Teams
import de.hennihaus.services.TeamService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.java.KoinJavaComponent.getKoin

fun Route.registerTeamRoutes() {
    getAllTeams()
    getTeamById()
    checkUsername()
    checkPassword()
    checkJmsQueue()
    saveTeam()
    deleteTeamById()
    resetStatistics()
}

private fun Route.getAllTeams() = get<Teams> {
    val teamService = getKoin().get<TeamService>()
    call.respond(message = teamService.getAllTeams())
}

private fun Route.getTeamById() = get<Teams.Id> { request ->
    val teamService = getKoin().get<TeamService>()
    call.respond(
        message = teamService.getTeamById(
            id = request.id,
        ),
    )
}

private fun Route.checkUsername() = get<Teams.Id.CheckUsername> { request ->
    val teamService = getKoin().get<TeamService>()
    call.respond(
        message = ExistsResponse(
            exists = teamService.checkUsername(
                id = request.parent.id,
                username = request.username,
            ),
        ),
    )
}

private fun Route.checkPassword() = get<Teams.Id.CheckPassword> { request ->
    val teamService = getKoin().get<TeamService>()
    call.respond(
        message = ExistsResponse(
            exists = teamService.checkPassword(
                id = request.parent.id,
                password = request.password,
            ),
        ),
    )
}

private fun Route.checkJmsQueue() = get<Teams.Id.CheckJmsQueue> { request ->
    val teamService = getKoin().get<TeamService>()
    call.respond(
        message = ExistsResponse(
            exists = teamService.checkJmsQueue(
                id = request.parent.id,
                jmsQueue = request.jmsQueue,
            ),
        ),
    )
}

private fun Route.saveTeam() = put<Teams.Id> {
    val teamService = getKoin().get<TeamService>()
    call.respond(
        message = teamService.saveTeam(
            team = call.receive(),
        ),
    )
}

private fun Route.deleteTeamById() = delete<Teams.Id> { request ->
    val teamService = getKoin().get<TeamService>()
    teamService.deleteTeamById(id = request.id)
    call.respond(
        message = "",
        status = HttpStatusCode.NoContent,
    )
}

private fun Route.resetStatistics() = delete<Teams.Id.ResetStatistics> { request ->
    val teamService = getKoin().get<TeamService>()
    call.respond(
        message = teamService.resetStatistics(
            id = request.parent.id,
        ),
    )
}
