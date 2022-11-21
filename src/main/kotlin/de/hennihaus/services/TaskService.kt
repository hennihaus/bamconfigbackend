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
import kotlin.math.roundToInt

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

    suspend fun patchParameters(username: String, password: String) {
        val usernameParameter = repository.updateParameter(
            name = USERNAME_PARAMETER,
            example = username,
            repetitionAttempts = ONE_REPETITION_ATTEMPT,
        )
        val passwordParameter = repository.updateParameter(
            name = PASSWORD_PARAMETER,
            example = password,
            repetitionAttempts = ONE_REPETITION_ATTEMPT,
        )
        usernameParameter ?: throw NotFoundException(
            message = "$PARAMETER_NOT_FOUND_MESSAGE by $USERNAME_PARAMETER",
        )
        passwordParameter ?: throw NotFoundException(
            message = "$PARAMETER_NOT_FOUND_MESSAGE by $PASSWORD_PARAMETER",
        )
    }

    suspend fun patchParameters(
        minAmountInEuros: Int,
        maxAmountInEuros: Int,
        minTermInMonths: Int,
        maxTermInMonths: Int,
    ) {
        val amountInEurosParameter = repository.updateParameter(
            name = AMOUNT_IN_EUROS_PARAMETER,
            example = "${arrayOf(minAmountInEuros, maxAmountInEuros).average().roundToInt()}",
            repetitionAttempts = ONE_REPETITION_ATTEMPT,
        )
        val termInMonthsParameter = repository.updateParameter(
            name = TERM_IN_MONTHS_PARAMETER,
            example = "${arrayOf(minTermInMonths, maxTermInMonths).average().roundToInt()}",
            repetitionAttempts = ONE_REPETITION_ATTEMPT,
        )
        amountInEurosParameter ?: throw NotFoundException(
            message = "$PARAMETER_NOT_FOUND_MESSAGE by $AMOUNT_IN_EUROS_PARAMETER",
        )
        termInMonthsParameter ?: throw NotFoundException(
            message = "$PARAMETER_NOT_FOUND_MESSAGE by $TERM_IN_MONTHS_PARAMETER",
        )
    }

    suspend fun getAllParametersById(id: String): List<UUID> = id.toUUID { uuid ->
        repository.getAllParametersById(id = uuid)
    }

    suspend fun getAllResponsesById(id: String): List<UUID> = id.toUUID { uuid ->
        repository.getAllResponsesById(id = uuid)
    }

    suspend fun isTitleUnique(id: String, title: String): Boolean = id.toUUID { uuid ->
        repository.getTaskIdByTitle(title = title)
            ?.let { it == uuid }
            ?: true
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
                new = new.responses.find { response -> response.uuid == old.uuid } ?: old,
            )
        },
    )

    private fun Parameter.patchParameter(new: Parameter) = when (name) {
        USERNAME_PARAMETER, PASSWORD_PARAMETER, AMOUNT_IN_EUROS_PARAMETER, TERM_IN_MONTHS_PARAMETER -> copy(
            description = new.description,
        )
        else -> copy(
            description = new.description,
            example = new.example,
        )
    }

    private fun Response.patchResponse(new: Response) = copy(
        description = new.description,
        example = new.example,
    )

    companion object {
        const val USERNAME_PARAMETER = "username"
        const val PASSWORD_PARAMETER = "password"
        const val AMOUNT_IN_EUROS_PARAMETER = "amountInEuros"
        const val TERM_IN_MONTHS_PARAMETER = "termInMonths"

        const val TASK_NOT_FOUND_MESSAGE = "task not found by uuid"
        const val PARAMETER_NOT_FOUND_MESSAGE = "parameter not found"
    }
}
