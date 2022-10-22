package de.hennihaus.models.generated.github

data class UpdateFileRequest(
    val message: String,
    val committer: UpdateFileCommitter,
    val content: String,
    val sha: String,
    val branch: String,
)

data class UpdateFileCommitter(
    val name: String,
    val email: String,
)
