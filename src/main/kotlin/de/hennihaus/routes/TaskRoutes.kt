package de.hennihaus.routes

import de.hennihaus.models.generated.rest.TaskDTO
import de.hennihaus.routes.TaskRoutes.ID_PATH_PARAMETER
import de.hennihaus.routes.TaskRoutes.TASKS_PATH
import de.hennihaus.routes.TaskRoutes.TITLE_PATH_PARAMETER
import de.hennihaus.routes.TaskRoutes.UNIQUE_PATH
import de.hennihaus.routes.mappers.toTask
import de.hennihaus.routes.mappers.toTaskDTO
import de.hennihaus.routes.mappers.toUniqueDTO
import de.hennihaus.services.TaskService
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import org.koin.java.KoinJavaComponent.getKoin

object TaskRoutes {
    const val TASKS_PATH = "tasks"
    const val UNIQUE_PATH = "unique"

    const val ID_PATH_PARAMETER = "id"
    const val TITLE_PATH_PARAMETER = "title"
}

fun Route.registerTaskRoutes() = route(path = "/$TASKS_PATH") {
    getAllTask()
    getTaskById()
    isTitleUnique()
    patchTask()
}

private fun Route.getAllTask() = get {
    val taskService = getKoin().get<TaskService>()

    with(receiver = call) {
        val tasks = taskService.getAllTasks()

        respond(message = tasks.map { it.toTaskDTO() })
    }
}

private fun Route.getTaskById() = get(path = "/{$ID_PATH_PARAMETER}") {
    val taskService = getKoin().get<TaskService>()

    with(receiver = call) {
        val id = parameters.getOrFail(name = ID_PATH_PARAMETER)

        val task = taskService.getTaskById(id = id)

        respond(message = task.toTaskDTO())
    }
}

private fun Route.isTitleUnique() = get(
    path = "/{$ID_PATH_PARAMETER}/$UNIQUE_PATH/$TITLE_PATH_PARAMETER/{$TITLE_PATH_PARAMETER}",
) {
    val taskService = getKoin().get<TaskService>()

    with(receiver = call) {
        val id = parameters.getOrFail(name = ID_PATH_PARAMETER)
        val title = parameters.getOrFail(name = TITLE_PATH_PARAMETER)

        val isUnique = taskService.isTitleUnique(
            id = id,
            title = title,
        )

        respond(message = isUnique.toUniqueDTO())
    }
}

private fun Route.patchTask() = patch(path = "/{$ID_PATH_PARAMETER}") {
    val taskService = getKoin().get<TaskService>()

    with(receiver = call) {
        val id = parameters.getOrFail(name = ID_PATH_PARAMETER)
        val dto = receive<TaskDTO>()

        val task = taskService.patchTask(
            id = id,
            task = dto.toTask(),
        )

        respond(message = task.toTaskDTO())
    }
}
