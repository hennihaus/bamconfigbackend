package de.hennihaus.routes.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

object TaskPaths {
    const val TASKS_PATH = "/tasks"
    const val ID_PATH = "/{id}"

    const val TITLE_EXISTS_PATH = "/check/title/{title}"
}

@Serializable
@Resource(TaskPaths.TASKS_PATH)
class Tasks {

    @Serializable
    @Resource(TaskPaths.ID_PATH)
    data class Id(val parent: Tasks = Tasks(), val id: String) {

        @Serializable
        @Resource(TaskPaths.TITLE_EXISTS_PATH)
        data class CheckTitle(val parent: Id, val title: String)
    }
}
