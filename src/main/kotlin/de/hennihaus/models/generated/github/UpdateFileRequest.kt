package de.hennihaus.models.generated.github

import kotlinx.serialization.Serializable

@Serializable
data class UpdateFileRequest(
    val message: String,
    val committer: UpdateFileCommitter,
    val content: String,
    val sha: String,
    val branch: String,
)

@Serializable
data class UpdateFileCommitter(
    val name: String,
    val email: String,
)
