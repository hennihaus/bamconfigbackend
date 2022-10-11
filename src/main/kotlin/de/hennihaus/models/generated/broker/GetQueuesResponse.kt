package de.hennihaus.models.generated.broker

import com.fasterxml.jackson.annotation.JsonProperty

data class GetQueuesResponse(
    val error: String? = null,
    @JsonProperty("error_type")
    val errorType: String? = null,
    val request: GetQueuesRequest,
    val status: Int,
    val timestamp: Long? = null,
    val value: List<Queue> = emptyList(),
)

data class GetQueuesRequest(
    val attribute: String,
    val mbean: String,
    val type: String,
)

data class Queue(
    val objectName: String,
)
