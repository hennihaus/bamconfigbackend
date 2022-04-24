package de.hennihaus.services

import de.hennihaus.models.Task
import de.hennihaus.plugins.NotFoundException
import de.hennihaus.repositories.TaskRepository
import de.hennihaus.utils.toObjectId
import org.koin.core.annotation.Single

@Single
class TaskServiceImpl(private val repository: TaskRepository) : TaskService {

    override suspend fun getAllTasks(): List<Task> = repository.getAll().sortedBy { it.step }

    override suspend fun getTaskById(id: String): Task {
        return id.toObjectId {
            repository.getById(id = it) ?: throw NotFoundException(message = ID_MESSAGE)
        }
    }

    override suspend fun patchTask(id: String, task: Task): Task {
        return id.toObjectId {
            repository.getById(id = it)
                ?.copy(
                    title = task.title,
                    description = task.description,
                    parameters = task.parameters
                )
                ?.let { task -> repository.save(entry = task) }
                ?: throw NotFoundException(message = ID_MESSAGE)
        }
    }

    companion object {
        internal const val ID_MESSAGE = "No Task for given ID found!"
    }
}
