package de.hennihaus.models.generated

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetTopicsResponse(
    val error: String? = null,
    @SerialName("error_type")
    val errorType: String? = null,
    val request: GetTopicsRequest,
    val status: Int,
    val timestamp: Long? = null,
    val value: List<Topic> = emptyList()
)

@Serializable
data class GetTopicsRequest(
    val attribute: String,
    val mbean: String,
    val type: String
)

@Serializable
data class Topic(
    val objectName: String
)
