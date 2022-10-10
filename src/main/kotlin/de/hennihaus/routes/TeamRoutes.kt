package de.hennihaus.routes

import de.hennihaus.models.generated.rest.TeamDTO
import de.hennihaus.models.generated.rest.UniqueDTO
import de.hennihaus.routes.mappers.toTeam
import de.hennihaus.routes.mappers.toTeamDTO
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
    isUsernameUnique()
    isPasswordUnique()
    isJmsQueueUnique()
    saveTeam()
    deleteTeamById()
    resetStatistics()
}

private fun Route.getAllTeams() = get<Teams> {
    val teamService = getKoin().get<TeamService>()
    call.respond(
        message = teamService.getAllTeams().map {
            it.toTeamDTO()
        },
    )
}

private fun Route.getTeamById() = get<Teams.Id> { request ->
    val teamService = getKoin().get<TeamService>()
    call.respond(
        message = teamService.getTeamById(id = request.id).toTeamDTO(),
    )
}

private fun Route.isUsernameUnique() = get<Teams.Id.UniqueUsername> { request ->
    val teamService = getKoin().get<TeamService>()
    call.respond(
        message = UniqueDTO(
            isUnique = teamService.isUsernameUnique(
                id = request.parent.id,
                username = request.username,
            ),
        ),
    )
}

private fun Route.isPasswordUnique() = get<Teams.Id.UniquePassword> { request ->
    val teamService = getKoin().get<TeamService>()
    call.respond(
        message = UniqueDTO(
            isUnique = teamService.isPasswordUnique(
                id = request.parent.id,
                password = request.password,
            ),
        ),
    )
}

private fun Route.isJmsQueueUnique() = get<Teams.Id.UniqueJmsQueue> { request ->
    val teamService = getKoin().get<TeamService>()
    call.respond(
        message = UniqueDTO(
            isUnique = teamService.isJmsQueueUnique(
                id = request.parent.id,
                jmsQueue = request.jmsQueue,
            ),
        ),
    )
}

private fun Route.saveTeam() = put<Teams.Id> {
    val teamService = getKoin().get<TeamService>()
    val team = teamService.saveTeam(
        team = call.receive<TeamDTO>().toTeam(),
    )
    call.respond(
        message = team.toTeamDTO(),
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
        message = teamService.resetStatistics(id = request.parent.id).toTeamDTO(),
    )
}
