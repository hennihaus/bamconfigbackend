package de.hennihaus.services.callservices

import de.hennihaus.configurations.BrokerConfiguration.Companion.ACTIVE_MQ_HEADER_AUTHORIZATION
import de.hennihaus.configurations.BrokerConfiguration.Companion.ACTIVE_MQ_HOST
import de.hennihaus.configurations.BrokerConfiguration.Companion.ACTIVE_MQ_PORT
import de.hennihaus.models.generated.broker.GetQueuesResponse
import de.hennihaus.models.generated.broker.GetTopicsResponse
import de.hennihaus.models.generated.broker.Queue
import de.hennihaus.models.generated.broker.Topic
import de.hennihaus.objectmothers.BrokerContainerObjectMother.OBJECT_NAME_DEFAULT_PREFIX
import de.hennihaus.objectmothers.BrokerContainerObjectMother.QUEUE_OBJECT_NAME_SUFFIX
import de.hennihaus.objectmothers.BrokerContainerObjectMother.TOPIC_OBJECT_NAME_SUFFIX
import de.hennihaus.objectmothers.BrokerContainerObjectMother.getTestJobs
import de.hennihaus.objectmothers.BrokerContainerObjectMother.getTestQueues
import de.hennihaus.objectmothers.BrokerContainerObjectMother.getTestTopics
import de.hennihaus.objectmothers.BrokerObjectMother.JMS_BANK_A_QUEUE
import de.hennihaus.plugins.BrokerException
import de.hennihaus.plugins.ErrorMessage.BROKER_EXCEPTION_MESSAGE
import de.hennihaus.plugins.initKoin
import de.hennihaus.services.BrokerService.Companion.DESTINATION_NAME_DELIMITER
import de.hennihaus.services.BrokerService.Companion.DESTINATION_TYPE_DELIMITER
import de.hennihaus.testutils.containers.BrokerContainer
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.maps.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.beInstanceOf
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BrokerCallServiceIntegrationTest : KoinTest {

    private val brokerContainer = BrokerContainer.INSTANCE
    private val classUnderTest: BrokerCallService by inject()

    @JvmField
    @RegisterExtension
    @Suppress("unused")
    val koinTestInstance = KoinTestExtension.create {
        initKoin(
            properties = mapOf(
                ACTIVE_MQ_HOST to brokerContainer.host,
                ACTIVE_MQ_PORT to brokerContainer.firstMappedPort.toString(),
                ACTIVE_MQ_HEADER_AUTHORIZATION to BrokerContainer.ACTIVE_MQ_AUTHORIZATION_HEADER,
            )
        )
    }

    @BeforeEach
    fun init() = BrokerContainer.resetState()

    @AfterAll
    fun cleanUp() = stopKoin()

    @Nested
    inner class GetAllQueues {
        @Test
        fun `should return 200 and a queue list containing correct objectName`() = runBlocking<Unit> {
            BrokerContainer.addTestData(queues = getTestQueues())

            val result: GetQueuesResponse = classUnderTest.getAllQueues()

            result.shouldNotBeNull()
            result.status shouldBe HttpStatusCode.OK.value
            result.value shouldHaveSize getTestQueues().size
            result.value shouldContainExactlyInAnyOrder getTestQueues().map {
                Queue(
                    objectName = """
                        $OBJECT_NAME_DEFAULT_PREFIX
                        $DESTINATION_NAME_DELIMITER
                        $it
                        $DESTINATION_TYPE_DELIMITER
                        $QUEUE_OBJECT_NAME_SUFFIX
                    """.trimIndent().replace("\n", "")
                )
            }
        }

        @Test
        fun `should return 200 and an empty list when no queues available`() = runBlocking<Unit> {
            BrokerContainer.addTestData(queues = emptyList())

            val result: GetQueuesResponse = classUnderTest.getAllQueues()

            result.shouldNotBeNull()
            result.status shouldBe HttpStatusCode.OK.value
            result.value.shouldBeEmpty()
        }
    }

    @Nested
    inner class GetAllTopics {
        @Test
        fun `should return 200 and a topic list containing correct objectName`() = runBlocking<Unit> {
            BrokerContainer.addTestData(topics = getTestTopics())

            val result: GetTopicsResponse = classUnderTest.getAllTopics()

            result.shouldNotBeNull()
            result.status shouldBe HttpStatusCode.OK.value
            result.value shouldHaveSize getTestTopics().size
            result.value shouldContainExactlyInAnyOrder getTestTopics().map {
                Topic(
                    objectName = """
                        $OBJECT_NAME_DEFAULT_PREFIX
                        $DESTINATION_NAME_DELIMITER
                        $it
                        $DESTINATION_TYPE_DELIMITER
                        $TOPIC_OBJECT_NAME_SUFFIX
                    """.trimIndent().replace("\n", "")
                )
            }
        }

        @Test
        fun `should return 200 and an empty list when no topics available`() = runBlocking<Unit> {
            BrokerContainer.addTestData(topics = emptyList())

            val result: GetTopicsResponse = classUnderTest.getAllTopics()

            result.shouldNotBeNull()
            result.status shouldBe HttpStatusCode.OK.value
            result.value.shouldBeEmpty()
        }
    }

    @Nested
    inner class DeleteAllJobs {
        @Test
        fun `should return 200 and delete all open jobs in broker`() = runBlocking {
            BrokerContainer.addTestData(jobs = getTestJobs())
            BrokerContainer.getTestJobs().value.shouldNotBeEmpty()

            val response: HttpResponse = classUnderTest.deleteAllJobs()

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldContain HTTP_STATUS_OK
            BrokerContainer.getTestJobs().value shouldBe emptyMap()
        }

        @Test
        fun `should return 200 and not throw an exception when no jobs is in broker`() = runBlocking {
            BrokerContainer.addTestData(jobs = emptyMap())

            val response: HttpResponse = classUnderTest.deleteAllJobs()

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldContain HTTP_STATUS_OK
            BrokerContainer.getTestJobs().value shouldBe emptyMap()
        }
    }

    @Nested
    inner class DeleteQueueByName {
        @Test
        fun `should delete a queue by name`() = runBlocking<Unit> {
            BrokerContainer.addTestData(queues = listOf(JMS_BANK_A_QUEUE))
            BrokerContainer.getTestQueues().value.shouldNotBeEmpty()

            val response: HttpResponse = classUnderTest.deleteQueueByName(name = JMS_BANK_A_QUEUE)

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldContain HTTP_STATUS_OK
            BrokerContainer.getTestQueues().value.shouldBeEmpty()
        }

        @Test
        fun `should return 200 and not throw an exception when queue was not found`() = runBlocking<Unit> {
            val name = "unknownQueue"

            val response: HttpResponse = classUnderTest.deleteQueueByName(name = name)

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldContain HTTP_STATUS_OK
            BrokerContainer.getTestQueues().value.shouldBeEmpty()
        }

        @Test
        fun `should throw an exception when queue in request is empty`() = runBlocking {
            val name = ""

            val response = shouldThrow<BrokerException> { classUnderTest.deleteQueueByName(name = name) }

            response should beInstanceOf<BrokerException>()
            response.message shouldNotBe BROKER_EXCEPTION_MESSAGE
        }
    }

    @Nested
    inner class DeleteTopicByName {
        @Test
        fun `should delete a topic by name`() = runBlocking<Unit> {
            BrokerContainer.addTestData(topics = listOf(JMS_BANK_A_QUEUE))
            BrokerContainer.getTestTopics().value.shouldNotBeEmpty()

            val response: HttpResponse = classUnderTest.deleteTopicByName(name = JMS_BANK_A_QUEUE)

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldContain HTTP_STATUS_OK
            BrokerContainer.getTestTopics().value shouldNotContain Topic(
                objectName = """
                    $OBJECT_NAME_DEFAULT_PREFIX
                    $DESTINATION_NAME_DELIMITER
                    $JMS_BANK_A_QUEUE
                    $DESTINATION_TYPE_DELIMITER
                    $TOPIC_OBJECT_NAME_SUFFIX
                """.trimIndent().replace("\n", "")
            )
        }

        @Test
        fun `should return 200 and not throw an exception when topic was not found`() = runBlocking<Unit> {
            val name = "unknownTopic"

            val response: HttpResponse = classUnderTest.deleteTopicByName(name = name)

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldContain HTTP_STATUS_OK
            BrokerContainer.getTestTopics().value.shouldBeEmpty()
        }

        @Test
        fun `should throw an exception when topic in request is empty`() = runBlocking {
            val name = ""

            val response = shouldThrow<BrokerException> { classUnderTest.deleteTopicByName(name = name) }

            response should beInstanceOf<BrokerException>()
            response.message shouldNotBe BROKER_EXCEPTION_MESSAGE
        }
    }

    companion object {
        const val HTTP_STATUS_OK = """"status":200"""
    }
}
