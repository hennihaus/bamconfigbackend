package de.hennihaus.services.callservices

import de.hennihaus.models.generated.DeleteJobsResponse
import de.hennihaus.models.generated.DeleteQueueResponse
import de.hennihaus.models.generated.DeleteTopicResponse
import de.hennihaus.models.generated.GetQueuesResponse
import de.hennihaus.models.generated.GetTopicsResponse
import de.hennihaus.plugins.BrokerException
import de.hennihaus.services.callservices.resources.Broker
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import org.koin.core.annotation.Single

@Single
class BrokerCallServiceImpl(private val client: HttpClient) : BrokerCallService {

    override suspend fun getAllQueues(): GetQueuesResponse {
        return client.get(resource = Broker.Read.MBean.Queues()).body<GetQueuesResponse>().also {
            validateResponse(
                status = it.status,
                error = it.error
            )
        }
    }

    override suspend fun getAllTopics(): GetTopicsResponse {
        return client.get(resource = Broker.Read.MBean.Topics()).body<GetTopicsResponse>().also {
            validateResponse(
                status = it.status,
                error = it.error
            )
        }
    }

    override suspend fun deleteAllJobs(): HttpResponse {
        return client.get(resource = Broker.Exec.JobMBean.RemoveAllJobs()).also {
            val body = it.body<DeleteJobsResponse>()
            validateResponse(
                status = body.status,
                error = body.error
            )
        }
    }

    override suspend fun deleteQueueByName(name: String): HttpResponse {
        return client.get(resource = Broker.Exec.MBean.RemoveQueue(name = name)).also {
            val body = it.body<DeleteQueueResponse>()
            validateResponse(
                status = body.status,
                error = body.error
            )
        }
    }

    override suspend fun deleteTopicByName(name: String): HttpResponse {
        return client.get(resource = Broker.Exec.MBean.RemoveTopic(name = name)).also {
            val body = it.body<DeleteTopicResponse>()
            validateResponse(
                status = body.status,
                error = body.error
            )
        }
    }

    private fun validateResponse(status: Int, error: String?) {
        val valid = status.takeIf {
            HttpStatusCode.fromValue(value = it).isSuccess()
        }
        valid ?: throw BrokerException(message = error)
    }
}
