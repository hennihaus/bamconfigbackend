package de.hennihaus.models.generated.github

import com.fasterxml.jackson.annotation.JsonProperty

data class UpdateFileResponse(
    val content: UpdateFileContent?,
    val commit: UpdateFileCommit,
)

data class UpdateFileContent(
    val name: String,
    val path: String,
    val sha: String,
    val size: Int,
    val url: String,
    @JsonProperty("html_url")
    val htmlUrl: String,
    @JsonProperty("git_url")
    val gitUrl: String,
    @JsonProperty("download_url")
    val downloadUrl: String,
    val type: String,
    @JsonProperty("_links")
    val links: UpdateFileLinks,
)

data class UpdateFileCommit(
    val sha: String,
    @JsonProperty("node_id")
    val nodeId: String,
    val url: String,
    @JsonProperty("html_url")
    val htmlUrl: String,
    val author: UpdateFileAuthor,
    val committer: UpdateFileCommitter,
    val message: String,
    val tree: UpdateFileTree,
    val parents: List<UpdateFileParent>,
    val verification: UpdateFileVerification,
)

data class UpdateFileLinks(
    val self: String,
    val git: String,
    val html: String,
)

data class UpdateFileAuthor(
    val date: String,
    val name: String,
    val email: String,
)

data class UpdateFileTree(
    val url: String,
    val sha: String,
)

data class UpdateFileParent(
    val url: String,
    @JsonProperty("html_url")
    val htmlUrl: String,
    val sha: String,
)

data class UpdateFileVerification(
    val verified: Boolean,
    val reason: String,
    val signature: String?,
    val payload: String?,
)
