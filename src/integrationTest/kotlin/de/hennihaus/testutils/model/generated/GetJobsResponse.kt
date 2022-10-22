package de.hennihaus.testutils.model.generated

data class GetJobsResponse(
    val request: GetJobsRequest,
    val status: Int,
    val timestamp: Long,
    val value: Map<String, Job>,
)

data class GetJobsRequest(
    val attribute: String,
    val mbean: String,
    val type: String,
)

data class Job(
    val cronEntry: String,
    val delay: Long,
    val jobId: String,
    val next: String,
    val period: Int,
    val repeat: Int,
    val start: String,
)
