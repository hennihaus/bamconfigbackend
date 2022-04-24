package de.hennihaus.routes.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

object TaskPaths {
    const val TASKS_PATH = "/tasks"
    const val ID_PATH = "/{id}"
}

@Serializable
@Resource(TaskPaths.TASKS_PATH)
class Tasks {

    @Serializable
    @Resource(TaskPaths.ID_PATH)
    data class Id(val parent: Tasks = Tasks(), val id: String)
}
