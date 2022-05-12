package de.hennihaus.models.generated

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeleteJobsResponse(
    val error: String? = null,
    @SerialName("error_type")
    val errorType: String? = null,
    val request: DeleteJobsRequest,
    val status: Int,
    val timestamp: Long? = null,
)

@Serializable
data class DeleteJobsRequest(
    val mbean: String,
    val operation: String,
    val type: String
)
