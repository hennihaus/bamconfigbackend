package de.hennihaus.models.generated.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetFileResponse(
    val name: String,
    val path: String,
    val sha: String,
    val size: Int,
    val url: String,
    @SerialName("html_url")
    val htmlUrl: String?,
    @SerialName("git_url")
    val gitUrl: String?,
    @SerialName("download_url")
    val downloadUrl: String?,
    val type: String,
    val content: String,
    val encoding: String,
    @SerialName("_links")
    val links: GetFileLinks,
)

@Serializable
data class GetFileLinks(
    val self: String,
    val git: String?,
    val html: String?,
)
