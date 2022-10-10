package de.hennihaus.routes

import de.hennihaus.models.generated.rest.TaskDTO
import de.hennihaus.models.generated.rest.UniqueDTO
import de.hennihaus.routes.mappers.toTask
import de.hennihaus.routes.mappers.toTaskDTO
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
    isTitleUnique()
    patchTask()
}

private fun Route.getAllTask() = get<Tasks> {
    val taskService = getKoin().get<TaskService>()
    call.respond(
        message = taskService.getAllTasks().map {
            it.toTaskDTO()
        },
    )
}

private fun Route.getTaskById() = get<Tasks.Id> { request ->
    val taskService = getKoin().get<TaskService>()
    call.respond(
        message = taskService.getTaskById(id = request.id).toTaskDTO(),
    )
}

private fun Route.isTitleUnique() = get<Tasks.Id.UniqueTitle> { request ->
    val taskService = getKoin().get<TaskService>()
    call.respond(
        message = UniqueDTO(
            isUnique = taskService.isTitleUnique(
                id = request.parent.id,
                title = request.title,
            ),
        ),
    )
}

private fun Route.patchTask() = patch<Tasks.Id> { request ->
    val taskService = getKoin().get<TaskService>()
    val task = taskService.patchTask(
        id = request.id,
        task = call.receive<TaskDTO>().toTask(),
    )
    call.respond(
        message = task.toTaskDTO(),
    )
}
