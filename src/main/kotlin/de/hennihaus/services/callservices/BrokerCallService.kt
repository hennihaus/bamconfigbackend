package de.hennihaus.services.callservices

import de.hennihaus.models.generated.broker.GetQueuesResponse
import de.hennihaus.models.generated.broker.GetTopicsResponse
import io.ktor.client.statement.HttpResponse

interface BrokerCallService {
    suspend fun getAllQueues(): GetQueuesResponse

    suspend fun getAllTopics(): GetTopicsResponse

    suspend fun deleteAllJobs(): HttpResponse

    suspend fun deleteQueueByName(name: String): HttpResponse

    suspend fun deleteTopicByName(name: String): HttpResponse
}
