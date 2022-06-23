package de.hennihaus.models.generated.github

import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName

@Serializable
data class UpdateFileResponse(
    val content: UpdateFileContent?,
    val commit: UpdateFileCommit,
)

@Serializable
data class UpdateFileContent(
    val name: String,
    val path: String,
    val sha: String,
    val size: Int,
    val url: String,
    @SerialName("html_url")
    val htmlUrl: String,
    @SerialName("git_url")
    val gitUrl: String,
    @SerialName("download_url")
    val downloadUrl: String,
    val type: String,
    @SerialName("_links")
    val links: UpdateFileLinks,
)

@Serializable
data class UpdateFileCommit(
    val sha: String,
    @SerialName("node_id")
    val nodeId: String,
    val url: String,
    @SerialName("html_url")
    val htmlUrl: String,
    val author: UpdateFileAuthor,
    val committer: UpdateFileCommitter,
    val message: String,
    val tree: UpdateFileTree,
    val parents: List<UpdateFileParent>,
    val verification: UpdateFileVerification,
)

@Serializable
data class UpdateFileLinks(
    val self: String,
    val git: String,
    val html: String,
)

@Serializable
data class UpdateFileAuthor(
    val date: String,
    val name: String,
    val email: String,
)

@Serializable
data class UpdateFileTree(
    val url: String,
    val sha: String,
)

@Serializable
data class UpdateFileParent(
    val url: String,
    @SerialName("html_url")
    val htmlUrl: String,
    val sha: String,
)

@Serializable
data class UpdateFileVerification(
    val verified: Boolean,
    val reason: String,
    val signature: String?,
    val payload: String?,
)
