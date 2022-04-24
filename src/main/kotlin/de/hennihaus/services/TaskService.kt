package de.hennihaus.services

import de.hennihaus.models.Task

interface TaskService {
    suspend fun getAllTasks(): List<Task>
    suspend fun getTaskById(id: String): Task
    suspend fun patchTask(id: String, task: Task): Task
}
