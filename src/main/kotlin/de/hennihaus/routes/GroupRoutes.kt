package de.hennihaus.routes

import de.hennihaus.routes.resources.Groups
import de.hennihaus.services.GroupService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.java.KoinJavaComponent.getKoin

fun Route.registerGroupRoutes() {
    getAllGroups()
    getGroupById()
    checkUsername()
    checkPassword()
    checkJmsTopic()
    createGroup()
    updateGroup()
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
            id = request.id
        )
    )
}

private fun Route.checkUsername(): Route = get<Groups.Id.CheckUsername> { request ->
    val groupService = getKoin().get<GroupService>()
    call.respond(
        message = groupService.checkUsername(
            id = request.parent.id,
            username = request.username
        )
    )
}

private fun Route.checkPassword(): Route = get<Groups.Id.CheckPassword> { request ->
    val groupService = getKoin().get<GroupService>()
    call.respond(
        message = groupService.checkPassword(
            id = request.parent.id,
            password = request.password
        )
    )
}

private fun Route.checkJmsTopic(): Route = get<Groups.Id.CheckJmsTopic> { request ->
    val groupService = getKoin().get<GroupService>()
    call.respond(
        message = groupService.checkJmsTopic(
            id = request.parent.id,
            jmsTopic = request.jmsTopic
        )
    )
}

private fun Route.createGroup(): Route = post<Groups> {
    val groupService = getKoin().get<GroupService>()
    call.respond(
        message = groupService.createGroup(
            group = call.receive()
        ),
        status = HttpStatusCode.Created
    )
}

private fun Route.updateGroup(): Route = put<Groups.Id> {
    val groupService = getKoin().get<GroupService>()
    call.respond(
        message = groupService.updateGroup(
            group = call.receive()
        )
    )
}

private fun Route.deleteGroupById(): Route = delete<Groups.Id> { request ->
    val groupService = getKoin().get<GroupService>()
    groupService.deleteGroupById(id = request.id)
    call.respond(
        message = "",
        status = HttpStatusCode.NoContent
    )
}

private fun Route.resetStats(): Route = delete<Groups.Id.ResetStats> { request ->
    val groupService = getKoin().get<GroupService>()
    call.respond(
        message = groupService.resetStats(
            id = request.parent.id
        )
    )
}
