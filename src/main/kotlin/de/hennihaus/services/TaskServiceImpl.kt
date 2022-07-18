package de.hennihaus.services

import de.hennihaus.models.Bank
import de.hennihaus.models.Task
import de.hennihaus.repositories.TaskRepository
import de.hennihaus.utils.toObjectId
import io.ktor.server.plugins.NotFoundException
import org.koin.core.annotation.Single

@Single
class TaskServiceImpl(
    private val repository: TaskRepository,
    private val stats: StatsService,
    private val github: GithubService,
) : TaskService {

    override suspend fun getAllTasks(): List<Task> = repository.getAll()
        .sortedBy { it.step }
        .map { it.copy(banks = it.banks.map { bank -> updateBank(bank = bank) }) }

    override suspend fun getTaskById(id: String): Task {
        val task = id.toObjectId {
            repository.getById(id = it) ?: throw NotFoundException(message = TASK_NOT_FOUND_MESSAGE)
        }
        return task.copy(banks = task.banks.map { updateBank(bank = it) })
    }

    override suspend fun patchTask(id: String, task: Task): Task {
        return id.toObjectId { objectId ->
            repository.getById(id = objectId)
                ?.copy(
                    title = task.title,
                    description = task.description,
                    isOpenApiVerbose = task.isOpenApiVerbose,
                    contact = task.contact,
                    parameters = task.parameters,
                    responses = task.responses,
                )
                ?.also { task -> github.updateOpenApi(task = task) }
                ?.let { task -> repository.save(entry = task) }
                ?.let { task -> task.copy(banks = task.banks.map { bank -> updateBank(bank = bank) }) }
                ?: throw NotFoundException(message = TASK_NOT_FOUND_MESSAGE)
        }
    }

    private suspend fun updateBank(bank: Bank): Bank = bank.copy(
        groups = bank.groups.map { stats.setHasPassed(group = it) }
    )

    companion object {
        internal const val TASK_NOT_FOUND_MESSAGE = "[task not found by id]"
    }
}
