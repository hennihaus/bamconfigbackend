package de.hennihaus.models.generated

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteQueueResponse(
    val error: String? = null,
    @SerialName("error_type")
    val errorType: String? = null,
    val request: DeleteQueueRequest,
    val status: Int,
    val timestamp: Long? = null,
)

@Serializable
data class DeleteQueueRequest(
    val arguments: List<String> = emptyList(),
    val mbean: String,
    val operation: String,
    val type: String
)
