package de.hennihaus.services.callservices

import de.hennihaus.configurations.BrokerConfiguration
import de.hennihaus.models.generated.broker.DeleteJobsResponse
import de.hennihaus.models.generated.broker.DeleteQueueResponse
import de.hennihaus.models.generated.broker.DeleteTopicResponse
import de.hennihaus.models.generated.broker.GetQueuesResponse
import de.hennihaus.models.generated.broker.GetTopicsResponse
import de.hennihaus.plugins.BrokerException
import de.hennihaus.services.callservices.resources.Broker
import de.hennihaus.utils.configureMonitoring
import de.hennihaus.utils.configureRetryBehavior
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class BrokerCallService(private val engine: HttpClientEngine, private val config: BrokerConfiguration) {

    private val client = HttpClient(engine = engine) {
        expectSuccess = true
        configureMonitoring()
        configureSerialization()
        configureRetryBehavior(maxRetries = config.maxRetries)
        configureDefaultRequests()
    }

    suspend fun getAllQueues(): GetQueuesResponse {
        return client.get(resource = Broker.Read.MBean.Queues()).body<GetQueuesResponse>().also {
            validateResponse(
                status = it.status,
                error = it.error,
            )
        }
    }

    suspend fun getAllTopics(): GetTopicsResponse {
        return client.get(resource = Broker.Read.MBean.Topics()).body<GetTopicsResponse>().also {
            validateResponse(
                status = it.status,
                error = it.error,
            )
        }
    }

    suspend fun deleteAllJobs(): HttpResponse {
        return client.get(resource = Broker.Exec.JobMBean.RemoveAllJobs()).also {
            val body = it.body<DeleteJobsResponse>()
            validateResponse(
                status = body.status,
                error = body.error,
            )
        }
    }

    suspend fun deleteQueueByName(name: String): HttpResponse {
        return client.get(resource = Broker.Exec.MBean.RemoveQueue(name = name)).also {
            val body = it.body<DeleteQueueResponse>()
            validateResponse(
                status = body.status,
                error = body.error,
            )
        }
    }

    suspend fun deleteTopicByName(name: String): HttpResponse {
        return client.get(resource = Broker.Exec.MBean.RemoveTopic(name = name)).also {
            val body = it.body<DeleteTopicResponse>()
            validateResponse(
                status = body.status,
                error = body.error,
            )
        }
    }

    private fun validateResponse(status: Int, error: String?) {
        val valid = status.takeIf {
            HttpStatusCode.fromValue(value = it).isSuccess()
        }
        valid ?: throw BrokerException(message = error)
    }

    private fun HttpClientConfig<*>.configureSerialization() {
        install(plugin = ContentNegotiation) {
            json(
                contentType = ContentType.Any,
                json = Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        install(plugin = Resources)
    }

    private fun HttpClientConfig<*>.configureDefaultRequests() = install(plugin = DefaultRequest) {
        val (protocol, host, port, _, authorizationHeader, originHeader) = config
        url {
            this.protocol = URLProtocol.createOrDefault(name = protocol)
            this.host = host
            this.port = port
        }
        headers {
            appendIfNameAbsent(name = HttpHeaders.Authorization, value = authorizationHeader)
            appendIfNameAbsent(name = HttpHeaders.Origin, value = originHeader)
        }
    }
}
