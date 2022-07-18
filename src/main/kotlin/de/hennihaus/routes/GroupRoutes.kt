package de.hennihaus.routes

import de.hennihaus.models.rest.ExistsResponse
import de.hennihaus.routes.resources.Groups
import de.hennihaus.services.GroupService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.java.KoinJavaComponent.getKoin

fun Route.registerGroupRoutes() {
    getAllGroups()
    getGroupById()
    checkUsername()
    checkPassword()
    checkJmsQueue()
    saveGroup()
    deleteGroupById()
    resetStats()
}

private fun Route.getAllGroups(): Route = get<Groups> {
    val groupService = getKoin().get<GroupService>()
    call.respond(message = groupService.getAllGroups())
}

private fun Route.getGroupById(): Route = get<Groups.Id> { request ->
    val groupService = getKoin().get<GroupService>()
    call.respond(
        message = groupService.getGroupById(
            id = request.id,
        ),
    )
}

private fun Route.checkUsername(): Route = get<Groups.Id.CheckUsername> { request ->
    val groupService = getKoin().get<GroupService>()
    call.respond(
        message = ExistsResponse(
            exists = groupService.checkUsername(
                id = request.parent.id,
                username = request.username,
            ),
        ),
    )
}

private fun Route.checkPassword(): Route = get<Groups.Id.CheckPassword> { request ->
    val groupService = getKoin().get<GroupService>()
    call.respond(
        message = ExistsResponse(
            exists = groupService.checkPassword(
                id = request.parent.id,
                password = request.password,
            ),
        ),
    )
}

private fun Route.checkJmsQueue(): Route = get<Groups.Id.CheckJmsQueue> { request ->
    val groupService = getKoin().get<GroupService>()
    call.respond(
        message = ExistsResponse(
            exists = groupService.checkJmsQueue(
                id = request.parent.id,
                jmsQueue = request.jmsQueue,
            ),
        ),
    )
}

private fun Route.saveGroup(): Route = put<Groups.Id> {
    val groupService = getKoin().get<GroupService>()
    call.respond(
        message = groupService.saveGroup(
            group = call.receive(),
        ),
    )
}

private fun Route.deleteGroupById(): Route = delete<Groups.Id> { request ->
    val groupService = getKoin().get<GroupService>()
    groupService.deleteGroupById(id = request.id)
    call.respond(
        message = "",
        status = HttpStatusCode.NoContent,
    )
}

private fun Route.resetStats(): Route = delete<Groups.Id.ResetStats> { request ->
    val groupService = getKoin().get<GroupService>()
    call.respond(
        message = groupService.resetStats(
            id = request.parent.id,
        ),
    )
}
