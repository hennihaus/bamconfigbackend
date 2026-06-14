package de.hennihaus.testutils.containers

import com.fasterxml.jackson.databind.DeserializationFeature
import de.hennihaus.models.generated.broker.GetQueuesResponse
import de.hennihaus.models.generated.broker.GetTopicsResponse
import de.hennihaus.services.BrokerService.Companion.DESTINATION_NAME_DELIMITER
import de.hennihaus.services.BrokerService.Companion.DESTINATION_TYPE_DELIMITER
import de.hennihaus.testutils.model.Job
import de.hennihaus.testutils.model.generated.GetJobsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.URLProtocol
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.runBlocking
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
    private const val MESSAGE_PATH = "api/message"
    private const val CONFIG_PATH = "api/jolokia"
    private const val EXEC_PATH = "exec"
    private const val READ_PATH = "read"
    private const val M_BEAN_PATH = "org.apache.activemq:type=Broker,brokerName=localhost"
    private const val JOB_M_BEAN_PATH =
        "org.apache.activemq:brokerName=localhost,name=JMS,service=JobScheduler,type=Broker"
    private const val ADD_QUEUE_OPERATION = "addQueue(java.lang.String)"
    private const val ADD_TOPIC_OPERATION = "addTopic(java.lang.String)"
    private const val REMOVE_QUEUE_OPERATION = "removeQueue(java.lang.String)"
    private const val REMOVE_TOPIC_OPERATION = "removeTopic(java.lang.String)"
    private const val REMOVE_JOBS_OPERATION = "removeAllJobs()"
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
        queues.forEach { TEST_CLIENT.get(urlString = addQueueUrl(queue = it)) }
        topics.forEach { TEST_CLIENT.get(urlString = addTopicUrl(topic = it)) }
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
        TEST_CLIENT.get(urlString = getQueuesUrl()).body<GetQueuesResponse>().value
            .map { it.objectName }
            .map { it.substringAfter(delimiter = DESTINATION_NAME_DELIMITER) }
            .map { it.substringBefore(delimiter = DESTINATION_TYPE_DELIMITER) }
            .forEach { TEST_CLIENT.get(urlString = removeQueueUrl(queue = it)) }
        // remove topics
        TEST_CLIENT.get(urlString = getTopicsUrl()).body<GetTopicsResponse>().value
            .map { it.objectName }
            .map { it.substringAfter(delimiter = DESTINATION_NAME_DELIMITER) }
            .map { it.substringBefore(delimiter = DESTINATION_TYPE_DELIMITER) }
            .forEach { TEST_CLIENT.get(urlString = removeTopicUrl(topic = it)) }
        // remove jobs
        TEST_CLIENT.get(urlString = removeJobsUrl())
    }

    suspend fun getTestQueues(): GetQueuesResponse = TEST_CLIENT.get(urlString = getQueuesUrl()).body()

    suspend fun getTestTopics(): GetTopicsResponse = TEST_CLIENT.get(urlString = getTopicsUrl()).body()

    suspend fun getTestJobs(): GetJobsResponse = TEST_CLIENT.get(urlString = getJobsUrl()).body()

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
            jackson(contentType = ContentType.Any) {
                disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            }
        }
    }

    private suspend fun sendMessage(message: String, queue: String, delayInMilliseconds: Long) = TEST_CLIENT.submitForm(
        url = "/$MESSAGE_PATH/$queue",
        block = {
            parameter(key = ACTIVE_MQ_MESSAGE_TYPE_PARAMETER, ACTIVE_MQ_MESSAGE_QUEUE)
        },
        formParameters = Parameters.build {
            append(name = ACTIVE_MQ_BODY_PROPERTY, message)
            append(name = ACTIVE_MQ_DELAY_PROPERTY, "$delayInMilliseconds")
        }
    )

    private fun getQueuesUrl() = buildString {
        append("/")
        append(CONFIG_PATH)
        append("/")
        append(READ_PATH)
        append("/")
        append(M_BEAN_PATH)
        append("/")
        append(GET_QUEUES_OPERATION)
    }

    private fun getTopicsUrl() = buildString {
        append("/")
        append(CONFIG_PATH)
        append("/")
        append(READ_PATH)
        append("/")
        append(M_BEAN_PATH)
        append("/")
        append(GET_TOPICS_OPERATION)
    }

    private fun getJobsUrl() = buildString {
        append("/")
        append(CONFIG_PATH)
        append("/")
        append(READ_PATH)
        append("/")
        append(JOB_M_BEAN_PATH)
        append("/")
        append(GET_JOBS_OPERATION)
    }

    private fun addQueueUrl(queue: String) = buildString {
        append("/")
        append(CONFIG_PATH)
        append("/")
        append(EXEC_PATH)
        append("/")
        append(M_BEAN_PATH)
        append("/")
        append(ADD_QUEUE_OPERATION)
        append("/")
        append(queue)
    }

    private fun addTopicUrl(topic: String) = buildString {
        append("/")
        append(CONFIG_PATH)
        append("/")
        append(EXEC_PATH)
        append("/")
        append(M_BEAN_PATH)
        append("/")
        append(ADD_TOPIC_OPERATION)
        append("/")
        append(topic)
    }

    private fun removeQueueUrl(queue: String) = buildString {
        append("/")
        append(CONFIG_PATH)
        append("/")
        append(EXEC_PATH)
        append("/")
        append(M_BEAN_PATH)
        append("/")
        append(REMOVE_QUEUE_OPERATION)
        append("/")
        append(queue)
    }

    private fun removeTopicUrl(topic: String) = buildString {
        append("/")
        append(CONFIG_PATH)
        append("/")
        append(EXEC_PATH)
        append("/")
        append(M_BEAN_PATH)
        append("/")
        append(REMOVE_TOPIC_OPERATION)
        append("/")
        append(topic)
    }

    private fun removeJobsUrl() = buildString {
        append("/")
        append(CONFIG_PATH)
        append("/")
        append(EXEC_PATH)
        append("/")
        append(JOB_M_BEAN_PATH)
        append("/")
        append(REMOVE_JOBS_OPERATION)
    }
}
