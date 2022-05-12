package de.hennihaus.models.generated

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetQueuesResponse(
    val error: String? = null,
    @SerialName("error_type")
    val errorType: String? = null,
    val request: GetQueuesRequest,
    val status: Int,
    val timestamp: Long? = null,
    val value: List<Queue> = emptyList()
)

@Serializable
data class GetQueuesRequest(
    val attribute: String,
    val mbean: String,
    val type: String
)

@Serializable
data class Queue(
    val objectName: String
)
