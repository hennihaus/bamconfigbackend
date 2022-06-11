package de.hennihaus.services

import de.hennihaus.models.Bank
import de.hennihaus.models.Task
import de.hennihaus.repositories.TaskRepository
import de.hennihaus.utils.toObjectId
import io.ktor.server.plugins.NotFoundException
import org.koin.core.annotation.Single

@Single
class TaskServiceImpl(private val repository: TaskRepository, private val stats: StatsService) : TaskService {

    override suspend fun getAllTasks(): List<Task> = repository.getAll()
        .sortedBy { it.step }
        .map { it.copy(banks = it.banks.map { bank -> updateBank(bank = bank) }) }

    override suspend fun getTaskById(id: String): Task {
        val task = id.toObjectId {
            repository.getById(id = it) ?: throw NotFoundException(message = ID_MESSAGE)
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
                ?.let { task -> repository.save(entry = task) }
                ?.let { task -> task.copy(banks = task.banks.map { bank -> updateBank(bank = bank) }) }
                ?: throw NotFoundException(message = ID_MESSAGE)
        }
    }

    private suspend fun updateBank(bank: Bank): Bank = bank.copy(
        groups = bank.groups.map { stats.setHasPassed(group = it) }
    )

    companion object {
        internal const val ID_MESSAGE = "No Task for given ID found!"
    }
}
