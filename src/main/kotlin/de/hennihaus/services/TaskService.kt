package de.hennihaus.services

import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.models.Parameter
import de.hennihaus.models.Response
import de.hennihaus.models.Task
import de.hennihaus.repositories.TaskRepository
import de.hennihaus.utils.toUUID
import io.ktor.server.plugins.NotFoundException
import org.koin.core.annotation.Single
import java.util.UUID

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
            ?.patchTask(new = task)
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

    suspend fun getAllParametersById(id: String): List<UUID> = id.toUUID { uuid ->
        repository.getAllParametersById(id = uuid)
    }

    suspend fun getAllResponsesById(id: String): List<UUID> = id.toUUID { uuid ->
        repository.getAllResponsesById(id = uuid)
    }

    suspend fun checkTitle(id: String, title: String): Boolean = id.toUUID { uuid ->
        repository.getTaskIdByTitle(title = title)
            ?.let { it != uuid }
            ?: false
    }

    private fun Task.patchTask(new: Task) = copy(
        title = new.title,
        description = new.description,
        isOpenApiVerbose = new.isOpenApiVerbose,
        contact = new.contact,
        parameters = parameters.map { old ->
            old.patchParameter(
                new = new.parameters.find { parameter -> parameter.uuid == old.uuid } ?: old,
            )
        },
        responses = responses.map { old ->
            old.patchResponse(
                new = new.responses.find { parameter -> parameter.uuid == old.uuid } ?: old,
            )
        },
    )

    private fun Parameter.patchParameter(new: Parameter) = copy(
        description = new.description,
        example = new.example,
    )

    private fun Response.patchResponse(new: Response) = copy(
        description = new.description,
        example = new.example,
    )

    companion object {
        internal const val TASK_NOT_FOUND_MESSAGE = "task not found by uuid"
    }
}
