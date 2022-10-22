package de.hennihaus.models.generated.github

import com.fasterxml.jackson.annotation.JsonProperty

data class GetFileResponse(
    val name: String,
    val path: String,
    val sha: String,
    val size: Int,
    val url: String,
    @JsonProperty("html_url")
    val htmlUrl: String?,
    @JsonProperty("git_url")
    val gitUrl: String?,
    @JsonProperty("download_url")
    val downloadUrl: String?,
    val type: String,
    val content: String,
    val encoding: String,
    @JsonProperty("_links")
    val links: GetFileLinks,
)

data class GetFileLinks(
    val self: String,
    val git: String?,
    val html: String?,
)
