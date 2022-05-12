package de.hennihaus.objectmothers

object BrokerObjectMother {

    const val DEAD_LETTER_QUEUE = "ActiveMQ.DLQ"
    const val JMS_BANK_A_QUEUE = "jmsBankA"
    const val FIRST_GROUP_QUEUE = "ResponseLoanBrokerGruppe01"

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
}
