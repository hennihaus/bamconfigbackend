package de.hennihaus.services.callservices

import de.hennihaus.configurations.BrokerConfiguration
import de.hennihaus.models.generated.broker.DeleteQueueResponse
import de.hennihaus.models.generated.broker.DeleteTopicResponse
import de.hennihaus.models.generated.broker.GetQueuesResponse
import de.hennihaus.models.generated.broker.GetTopicsResponse
import de.hennihaus.plugins.BrokerException
import de.hennihaus.services.callservices.paths.BrokerPaths.ACTIVE_MQ_PATH
import de.hennihaus.services.callservices.paths.BrokerPaths.EXEC_PATH
import de.hennihaus.services.callservices.paths.BrokerPaths.JMS_M_BEAN_PATH
import de.hennihaus.services.callservices.paths.BrokerPaths.M_BEAN_PATH
import de.hennihaus.services.callservices.paths.BrokerPaths.QUEUES_PATH
import de.hennihaus.services.callservices.paths.BrokerPaths.READ_PATH
import de.hennihaus.services.callservices.paths.BrokerPaths.REMOVE_JOBS_PATH
import de.hennihaus.services.callservices.paths.BrokerPaths.REMOVE_QUEUE_PATH
import de.hennihaus.services.callservices.paths.BrokerPaths.REMOVE_TOPIC_PATH
import de.hennihaus.services.callservices.paths.BrokerPaths.TOPICS_PATH
import de.hennihaus.utils.configureMonitoring
import de.hennihaus.utils.configureRetryBehavior
import de.hennihaus.utils.configureSerialization
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.http.isSuccess
import io.ktor.util.appendIfNameAbsent
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
        val response = client.get {
            url {
                appendPathSegments(segments = buildReadMBeanPath() + QUEUES_PATH)
            }
        }
        return response.body<GetQueuesResponse>().also {
            validateResponse(
                status = it.status,
                error = it.error,
            )
        }
    }

    suspend fun getAllTopics(): GetTopicsResponse {
        val response = client.get {
            url {
                appendPathSegments(segments = buildReadMBeanPath() + TOPICS_PATH)
            }
        }
        return response.body<GetTopicsResponse>().also {
            validateResponse(
                status = it.status,
                error = it.error,
            )
        }
    }

    suspend fun deleteAllJobs(): HttpResponse {
        val response = client.get {
            url {
                appendPathSegments(
                    segments = listOf(
                        ACTIVE_MQ_PATH,
                        EXEC_PATH,
                        JMS_M_BEAN_PATH,
                        REMOVE_JOBS_PATH,
                    ),
                )
            }
        }
        return response.also {
            val body = it.body<DeleteQueueResponse>()

            validateResponse(
                status = body.status,
                error = body.error,
            )
        }
    }

    suspend fun deleteQueueByName(name: String): HttpResponse {
        val response = client.get {
            url {
                appendPathSegments(segments = buildExecuteMBeanPath() + REMOVE_QUEUE_PATH + name)
            }
        }
        return response.also {
            val body = it.body<DeleteQueueResponse>()

            validateResponse(
                status = body.status,
                error = body.error,
            )
        }
    }

    suspend fun deleteTopicByName(name: String): HttpResponse {
        val response = client.get {
            url {
                appendPathSegments(segments = buildExecuteMBeanPath() + REMOVE_TOPIC_PATH + name)
            }
        }
        return response.also {
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

    private fun buildReadMBeanPath() = listOf(
        ACTIVE_MQ_PATH,
        READ_PATH,
        M_BEAN_PATH,
    )

    private fun buildExecuteMBeanPath() = listOf(
        ACTIVE_MQ_PATH,
        EXEC_PATH,
        M_BEAN_PATH,
    )
}
