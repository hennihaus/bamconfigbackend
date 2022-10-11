package de.hennihaus.models.generated.broker

import com.fasterxml.jackson.annotation.JsonProperty

data class DeleteQueueResponse(
    val error: String? = null,
    @JsonProperty("error_type")
    val errorType: String? = null,
    val request: DeleteQueueRequest,
    val status: Int,
    val timestamp: Long? = null,
)

data class DeleteQueueRequest(
    val arguments: List<String> = emptyList(),
    val mbean: String,
    val operation: String,
    val type: String,
)
