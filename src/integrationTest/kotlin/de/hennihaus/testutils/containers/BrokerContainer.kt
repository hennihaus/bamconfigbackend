package de.hennihaus.testutils.containers

import de.hennihaus.models.generated.broker.GetQueuesResponse
import de.hennihaus.models.generated.broker.GetTopicsResponse
import de.hennihaus.services.BrokerServiceImpl.Companion.DESTINATION_NAME_DELIMITER
import de.hennihaus.services.BrokerServiceImpl.Companion.DESTINATION_TYPE_DELIMITER
import de.hennihaus.services.callservices.resources.Broker
import de.hennihaus.testutils.model.Job
import de.hennihaus.testutils.model.generated.GetJobsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait

object BrokerContainer {

    /**
     * Container configurations
     */
    private const val IMAGE_NAME = "hennihaus/bambroker"
    private const val IMAGE_VERSION = "latest"
    private const val ACTIVE_MQ_PORT = 8161
    private const val ACTIVE_MQ_USERNAME_ENV_VARIABLE = "ACTIVE_MQ_USERNAME"
    private const val ACTIVE_MQ_USERNAME_ENV = "test"
    private const val ACTIVE_MQ_PASSWORD_ENV_VARIABLE = "ACTIVE_MQ_PASSWORD"
    private const val ACTIVE_MQ_PASSWORD_ENV = "test"

    /**
     * ActiveMQ mock configurations
     */
    private const val BASE_MESSAGE_PATH = "/api/message"
    private const val BASE_CONFIG_PATH = "/api/jolokia"
    private const val EXEC_PATH = "exec"
    private const val READ_PATH = "read"
    private const val BASE_M_BEAN_PATH = "org.apache.activemq:type=Broker,brokerName=localhost"
    private const val BASE_JMS_M_BEAN_PATH =
        "org.apache.activemq:brokerName=localhost,name=JMS,service=JobScheduler,type=Broker"
    private const val ADD_QUEUE_OPERATION = "addQueue(java.lang.String)"
    private const val ADD_TOPIC_OPERATION = "addTopic(java.lang.String)"
    private const val GET_JOBS_OPERATION = "AllJobs"
    private const val GET_QUEUES_OPERATION = "Queues"
    private const val GET_TOPICS_OPERATION = "Topics"
    private const val ACTIVE_MQ_DELAY_PROPERTY = "AMQ_SCHEDULED_DELAY"
    private const val ACTIVE_MQ_BODY_PROPERTY = "body"
    private const val ACTIVE_MQ_MESSAGE_TYPE_PARAMETER = "type"
    private const val ACTIVE_MQ_MESSAGE_QUEUE = "queue"

    /**
     * ActiveMQ client configurations
     */
    private const val ACTIVE_MQ_ORIGIN_HEADER = "http://localhost"
    private val ACTIVE_MQ_PROTOCOL = URLProtocol.HTTP
    const val ACTIVE_MQ_AUTHORIZATION_HEADER = "Basic dGVzdDp0ZXN0"

    /**
     * Helpers
     */
    val INSTANCE by lazy { startBrokerContainer() }
    val TEST_CLIENT by lazy { startTestClient() }

    suspend fun addTestData(
        queues: List<String> = emptyList(),
        topics: List<String> = emptyList(),
        jobs: Map<String, Job> = emptyMap()
    ) {
        queues.forEach {
            TEST_CLIENT.get(urlString = "$BASE_CONFIG_PATH/$EXEC_PATH/$BASE_M_BEAN_PATH/$ADD_QUEUE_OPERATION/$it")
        }
        topics.forEach {
            TEST_CLIENT.get(urlString = "$BASE_CONFIG_PATH/$EXEC_PATH/$BASE_M_BEAN_PATH/$ADD_TOPIC_OPERATION/$it")
        }
        jobs.toList().forEach { (queue, job) ->
            sendMessage(
                message = job.message,
                queue = queue,
                delayInMilliseconds = job.delayInMilliseconds
            )
        }
    }

    fun resetState() = runBlocking<Unit> {
        // remove queues
        TEST_CLIENT.get(resource = Broker.Read.MBean.Queues()).body<GetQueuesResponse>().value
            .map {
                it.objectName.substringAfter(delimiter = DESTINATION_NAME_DELIMITER)
                    .substringBefore(delimiter = DESTINATION_TYPE_DELIMITER)
            }
            .forEach { TEST_CLIENT.get(resource = Broker.Exec.MBean.RemoveQueue(name = it)) }
        // remove topics
        TEST_CLIENT.get(resource = Broker.Read.MBean.Topics()).body<GetTopicsResponse>().value
            .map {
                it.objectName.substringAfter(delimiter = DESTINATION_NAME_DELIMITER)
                    .substringBefore(delimiter = DESTINATION_TYPE_DELIMITER)
            }
            .forEach { TEST_CLIENT.get(resource = Broker.Exec.MBean.RemoveTopic(name = it)) }
        // remove jobs
        TEST_CLIENT.get(resource = Broker.Exec.JobMBean.RemoveAllJobs())
    }

    suspend fun getTestQueues(): GetQueuesResponse =
        TEST_CLIENT.get(urlString = "$BASE_CONFIG_PATH/$READ_PATH/$BASE_M_BEAN_PATH/$GET_QUEUES_OPERATION").body()

    suspend fun getTestTopics(): GetTopicsResponse =
        TEST_CLIENT.get(urlString = "$BASE_CONFIG_PATH/$READ_PATH/$BASE_M_BEAN_PATH/$GET_TOPICS_OPERATION").body()

    suspend fun getTestJobs(): GetJobsResponse =
        TEST_CLIENT.get(urlString = "$BASE_CONFIG_PATH/$READ_PATH/$BASE_JMS_M_BEAN_PATH/$GET_JOBS_OPERATION").body()

    private fun startBrokerContainer() = GenericContainer<Nothing>("$IMAGE_NAME:$IMAGE_VERSION").apply {
        env = listOf(
            "$ACTIVE_MQ_USERNAME_ENV_VARIABLE=$ACTIVE_MQ_USERNAME_ENV",
            "$ACTIVE_MQ_PASSWORD_ENV_VARIABLE=$ACTIVE_MQ_PASSWORD_ENV"
        )
        exposedPorts = listOf(
            ACTIVE_MQ_PORT
        )
        setWaitStrategy(Wait.forListeningPort())
        start()
    }

    private fun startTestClient() = HttpClient(CIO) {
        install(plugin = DefaultRequest) {
            url(urlString = "${ACTIVE_MQ_PROTOCOL.name}://${INSTANCE.host}:${INSTANCE.firstMappedPort}")
            header(key = HttpHeaders.Authorization, value = ACTIVE_MQ_AUTHORIZATION_HEADER)
            header(key = HttpHeaders.Origin, value = ACTIVE_MQ_ORIGIN_HEADER)
        }
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

    private suspend fun sendMessage(message: String, queue: String, delayInMilliseconds: Long) = TEST_CLIENT.submitForm(
        url = "$BASE_MESSAGE_PATH/$queue",
        block = {
            parameter(key = ACTIVE_MQ_MESSAGE_TYPE_PARAMETER, ACTIVE_MQ_MESSAGE_QUEUE)
        },
        formParameters = Parameters.build {
            append(name = ACTIVE_MQ_BODY_PROPERTY, message)
            append(name = ACTIVE_MQ_DELAY_PROPERTY, "$delayInMilliseconds")
        }
    )
}
