package de.hennihaus.services

import de.hennihaus.models.Task

interface GithubService {
    suspend fun updateOpenApi(task: Task)
}
