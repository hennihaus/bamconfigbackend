package de.hennihaus.services

import de.hennihaus.objectmothers.BankObjectMother.getJmsBank
import de.hennihaus.objectmothers.BrokerObjectMother.CONNECTION_QUEUES_INFO
import de.hennihaus.objectmothers.BrokerObjectMother.CONSUMED_QUEUES_INFO
import de.hennihaus.objectmothers.BrokerObjectMother.DEAD_LETTER_QUEUE
import de.hennihaus.objectmothers.BrokerObjectMother.FIRST_GROUP_QUEUE
import de.hennihaus.objectmothers.BrokerObjectMother.IS_MASTER_BROKER_INFO
import de.hennihaus.objectmothers.BrokerObjectMother.JMS_BANK_A_QUEUE
import de.hennihaus.objectmothers.BrokerObjectMother.MESSAGE_TO_DLQ_INFO
import de.hennihaus.objectmothers.BrokerObjectMother.PRODUCED_QUEUES_INFO
import de.hennihaus.objectmothers.BrokerObjectMother.QUEUE_CREATION_DELETION_INFO
import de.hennihaus.objectmothers.BrokerObjectMother.TOPIC_CREATION_DELETION_INFO
import de.hennihaus.objectmothers.BrokerObjectMother.getQueuesResponse
import de.hennihaus.objectmothers.BrokerObjectMother.getTopicsResponse
import de.hennihaus.services.callservices.BrokerCallServiceImpl
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.types.beInstanceOf
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BrokerServiceTest {

    private val brokerCall = mockk<BrokerCallServiceImpl>()

    private val classUnderTest = BrokerServiceImpl(
        brokerCall = brokerCall
    )

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class DeleteQueueByName {
        @Test
        fun `should delete a queue by name`() = runBlocking {
            val name = getJmsBank().name
            coEvery { brokerCall.deleteQueueByName(name = any()) } returns mockk()

            classUnderTest.deleteQueueByName(name = name)

            coVerify(exactly = 1) { brokerCall.deleteQueueByName(name = name) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val name = getJmsBank().name
            coEvery { brokerCall.deleteQueueByName(name = name) } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.deleteQueueByName(name = name) }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { brokerCall.deleteQueueByName(name = name) }
        }
    }

    @Nested
    inner class ResetBroker {
        @Test
        fun `should reset broker`() = runBlocking {
            coEvery { brokerCall.getAllQueues() } returns getQueuesResponse()
            coEvery { brokerCall.getAllTopics() } returns getTopicsResponse()
            coEvery { brokerCall.deleteQueueByName(name = any()) } returns mockk()
            coEvery { brokerCall.deleteTopicByName(name = any()) } returns mockk()
            coEvery { brokerCall.deleteAllJobs() } returns mockk()

            classUnderTest.resetBroker()

            coVerifySequence {
                brokerCall.getAllQueues()
                brokerCall.deleteQueueByName(name = JMS_BANK_A_QUEUE)
                brokerCall.deleteQueueByName(name = FIRST_GROUP_QUEUE)
                brokerCall.deleteQueueByName(name = DEAD_LETTER_QUEUE)

                brokerCall.getAllTopics()
                brokerCall.deleteTopicByName(name = CONNECTION_QUEUES_INFO)
                brokerCall.deleteTopicByName(name = "$CONSUMED_QUEUES_INFO.$DEAD_LETTER_QUEUE")
                brokerCall.deleteTopicByName(name = "$CONSUMED_QUEUES_INFO.$JMS_BANK_A_QUEUE")
                brokerCall.deleteTopicByName(name = IS_MASTER_BROKER_INFO)
                brokerCall.deleteTopicByName(name = "$MESSAGE_TO_DLQ_INFO.$JMS_BANK_A_QUEUE")
                brokerCall.deleteTopicByName(name = "$MESSAGE_TO_DLQ_INFO.$FIRST_GROUP_QUEUE")
                brokerCall.deleteTopicByName(name = "$PRODUCED_QUEUES_INFO.$JMS_BANK_A_QUEUE")
                brokerCall.deleteTopicByName(name = "$PRODUCED_QUEUES_INFO.$FIRST_GROUP_QUEUE")
                brokerCall.deleteTopicByName(name = QUEUE_CREATION_DELETION_INFO)
                brokerCall.deleteTopicByName(name = TOPIC_CREATION_DELETION_INFO)
                brokerCall.deleteTopicByName(name = JMS_BANK_A_QUEUE)

                brokerCall.deleteAllJobs()
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { brokerCall.getAllQueues() } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.resetBroker() }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { brokerCall.getAllQueues() }
            coVerify(exactly = 0) { brokerCall.deleteQueueByName(name = any()) }
            coVerify(exactly = 0) { brokerCall.getAllTopics() }
            coVerify(exactly = 0) { brokerCall.deleteTopicByName(name = any()) }
        }
    }
}
