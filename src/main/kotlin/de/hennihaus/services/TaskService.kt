package de.hennihaus.services

import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.models.Task
import de.hennihaus.repositories.TaskRepository
import de.hennihaus.utils.toUUID
import io.ktor.server.plugins.NotFoundException
import org.koin.core.annotation.Single

@Single
class TaskService(
    private val repository: TaskRepository,
    private val github: GithubService,
) {

    suspend fun getAllTasks(): List<Task> = repository.getAll().sortedBy { it.integrationStep }

    suspend fun getTaskById(id: String): Task = id.toUUID { uuid ->
        repository.getById(id = uuid)
            ?: throw NotFoundException(message = TASK_NOT_FOUND_MESSAGE)
    }

    suspend fun patchTask(id: String, task: Task): Task = id.toUUID { uuid ->
        repository.getById(id = uuid)
            ?.copy(
                title = task.title,
                description = task.description,
                isOpenApiVerbose = task.isOpenApiVerbose,
                contact = task.contact,
                parameters = task.parameters,
                responses = task.responses,
            )
            ?.also {
                github.updateOpenApi(task = it)
            }
            ?.let {
                repository.save(
                    entry = it,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
            ?: throw NotFoundException(message = TASK_NOT_FOUND_MESSAGE)
    }

    suspend fun checkTitle(id: String, title: String): Boolean = id.toUUID { uuid ->
        repository.getTaskByTitle(title = title)
            ?.let { it.uuid != uuid }
            ?: false
    }

    companion object {
        internal const val TASK_NOT_FOUND_MESSAGE = "[task not found by uuid]"
    }
}
