package de.hennihaus.models.generated

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class DeleteTopicResponse(
    val error: String? = null,
    @SerialName("error_type")
    val errorType: String? = null,
    val request: DeleteTopicRequest,
    val status: Int,
    val timestamp: Long? = null,
)

@Serializable
data class DeleteTopicRequest(
    val arguments: List<String> = emptyList(),
    val mbean: String,
    val operation: String,
    val type: String
)
