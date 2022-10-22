package de.hennihaus.models.generated.broker

import com.fasterxml.jackson.annotation.JsonProperty

data class DeleteJobsResponse(
    val error: String? = null,
    @JsonProperty("error_type")
    val errorType: String? = null,
    val request: DeleteJobsRequest,
    val status: Int,
    val timestamp: Long? = null,
)

data class DeleteJobsRequest(
    val mbean: String,
    val arguments: List<String> = emptyList(),
    val operation: String,
    val type: String,
)
