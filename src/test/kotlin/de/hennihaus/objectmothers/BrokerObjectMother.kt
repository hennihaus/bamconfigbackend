package de.hennihaus.objectmothers

import de.hennihaus.models.generated.broker.DeleteJobsResponse
import de.hennihaus.models.generated.broker.GetQueuesResponse
import de.hennihaus.models.generated.broker.GetTopicsResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

object BrokerObjectMother {

    const val DEAD_LETTER_QUEUE = "ActiveMQ.DLQ"
    const val JMS_BANK_A_QUEUE = "jmsBankA"
    const val FIRST_TEAM_QUEUE = "ResponseLoanBrokerTeam01"

    /**
     * https://activemq.apache.org/advisory-message
     */
    const val CONNECTION_QUEUES_INFO = "ActiveMQ.Advisory.Connection"
    const val PRODUCED_QUEUES_INFO = "ActiveMQ.Advisory.Producer.Queue"
    const val CONSUMED_QUEUES_INFO = "ActiveMQ.Advisory.Consumer.Queue"
    const val MESSAGE_TO_DLQ_INFO = "ActiveMQ.Advisory.MessageDLQd.Queue"
    const val QUEUE_CREATION_DELETION_INFO = "ActiveMQ.Advisory.Queue"
    const val TOPIC_CREATION_DELETION_INFO = "ActiveMQ.Advisory.Topic"
    const val IS_MASTER_BROKER_INFO = "ActiveMQ.Advisory.MasterBroker"

    fun getQueuesResponse(): GetQueuesResponse = Json.decodeFromString(
        string = File("./src/test/resources/broker/getQueuesResponse.json").readText(),
    )

    fun getTopicsResponse(): GetTopicsResponse = Json.decodeFromString(
        string = File("./src/test/resources/broker/getTopicsResponse.json").readText(),
    )

    fun getQueuesErrorResponse(): GetQueuesResponse = Json.decodeFromString(
        string = File("./src/test/resources/broker/getQueuesErrorResponse.json").readText(),
    )

    fun getTopicsErrorResponse(): GetTopicsResponse = Json.decodeFromString(
        string = File("./src/test/resources/broker/getTopicsErrorResponse.json").readText(),
    )

    fun getDeleteJobsErrorResponse(): DeleteJobsResponse = Json.decodeFromString(
        string = File("./src/test/resources/broker/deleteJobsErrorResponse.json").readText(),
    )
}
