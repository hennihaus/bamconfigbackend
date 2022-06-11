package de.hennihaus.routes

import de.hennihaus.routes.resources.Tasks
import de.hennihaus.services.TaskService
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.patch
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.java.KoinJavaComponent.getKoin

fun Route.registerTaskRoutes() {
    getAllTask()
    getTaskById()
    patchTask()
}

private fun Route.getAllTask(): Route = get<Tasks> {
    val taskService = getKoin().get<TaskService>()
    call.respond(message = taskService.getAllTasks())
}

private fun Route.getTaskById(): Route = get<Tasks.Id> { request ->
    val taskService = getKoin().get<TaskService>()
    call.respond(
        message = taskService.getTaskById(
            id = request.id,
        )
    )
}

private fun Route.patchTask(): Route = patch<Tasks.Id> { request ->
    val taskService = getKoin().get<TaskService>()
    call.respond(
        message = taskService.patchTask(
            id = request.id,
            task = call.receive(),
        )
    )
}
