package de.hennihaus.objectmothers

import de.hennihaus.objectmothers.BrokerObjectMother.CONNECTION_QUEUES_INFO
import de.hennihaus.objectmothers.BrokerObjectMother.CONSUMED_QUEUES_INFO
import de.hennihaus.objectmothers.BrokerObjectMother.DEAD_LETTER_QUEUE
import de.hennihaus.objectmothers.BrokerObjectMother.FIRST_TEAM_QUEUE
import de.hennihaus.objectmothers.BrokerObjectMother.IS_MASTER_BROKER_INFO
import de.hennihaus.objectmothers.BrokerObjectMother.JMS_BANK_A_QUEUE
import de.hennihaus.objectmothers.BrokerObjectMother.MESSAGE_TO_DLQ_INFO
import de.hennihaus.objectmothers.BrokerObjectMother.PRODUCED_QUEUES_INFO
import de.hennihaus.objectmothers.BrokerObjectMother.QUEUE_CREATION_DELETION_INFO
import de.hennihaus.objectmothers.BrokerObjectMother.TOPIC_CREATION_DELETION_INFO
import de.hennihaus.testutils.model.Job

object BrokerContainerObjectMother {

    private const val DEFAULT_TEST_MESSAGE = "Test"
    const val DEFAULT_TEST_DELAY = 600_000L
    const val OBJECT_NAME_DEFAULT_PREFIX = "org.apache.activemq:brokerName=localhost"
    const val QUEUE_OBJECT_NAME_SUFFIX = "Queue,type=Broker"
    const val TOPIC_OBJECT_NAME_SUFFIX = "Topic,type=Broker"

    fun getTestQueues(): List<String> = listOf(
        DEAD_LETTER_QUEUE,
        JMS_BANK_A_QUEUE,
        FIRST_TEAM_QUEUE,
    )

    fun getTestTopics(): List<String> = listOf(
        CONNECTION_QUEUES_INFO,
        "$CONSUMED_QUEUES_INFO.$DEAD_LETTER_QUEUE",
        "$CONSUMED_QUEUES_INFO.$JMS_BANK_A_QUEUE",
        IS_MASTER_BROKER_INFO,
        "$MESSAGE_TO_DLQ_INFO.$JMS_BANK_A_QUEUE",
        "$MESSAGE_TO_DLQ_INFO.$FIRST_TEAM_QUEUE",
        "$PRODUCED_QUEUES_INFO.$JMS_BANK_A_QUEUE",
        "$PRODUCED_QUEUES_INFO.$FIRST_TEAM_QUEUE",
        QUEUE_CREATION_DELETION_INFO,
        TOPIC_CREATION_DELETION_INFO,
        JMS_BANK_A_QUEUE,
    )

    fun getTestJobs(): Map<String, Job> = mapOf(
        JMS_BANK_A_QUEUE to Job(message = DEFAULT_TEST_MESSAGE),
        FIRST_TEAM_QUEUE to Job(message = DEFAULT_TEST_MESSAGE),
    )
}
