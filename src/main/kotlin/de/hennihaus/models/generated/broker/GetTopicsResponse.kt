package de.hennihaus.models.generated.broker

import com.fasterxml.jackson.annotation.JsonProperty

data class GetTopicsResponse(
    val error: String? = null,
    @JsonProperty("error_type")
    val errorType: String? = null,
    val request: GetTopicsRequest,
    val status: Int,
    val timestamp: Long? = null,
    val value: List<Topic> = emptyList(),
)

data class GetTopicsRequest(
    val attribute: String,
    val mbean: String,
    val type: String,
)

data class Topic(
    val objectName: String,
)
