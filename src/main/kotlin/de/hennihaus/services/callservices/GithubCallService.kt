package de.hennihaus.services.callservices

import de.hennihaus.configurations.GithubFileConfiguration
import de.hennihaus.models.generated.github.GetFileResponse
import de.hennihaus.models.generated.github.UpdateFileRequest
import de.hennihaus.models.generated.github.UpdateFileResponse

interface GithubCallService {
    suspend fun getFile(fileConfig: GithubFileConfiguration): GetFileResponse

    suspend fun updateFile(fileConfig: GithubFileConfiguration, file: UpdateFileRequest): UpdateFileResponse
}
