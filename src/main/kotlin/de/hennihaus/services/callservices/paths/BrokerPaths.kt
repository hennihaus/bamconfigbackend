package de.hennihaus.services.callservices.paths

object BrokerPaths {
    // Base path
    const val ACTIVE_MQ_PATH = "api/jolokia"

    // Operation type
    const val READ_PATH = "read"
    const val EXEC_PATH = "exec"

    // Beans
    const val M_BEAN_PATH = "org.apache.activemq:type=Broker,brokerName=localhost"
    const val JMS_M_BEAN_PATH = "org.apache.activemq:brokerName=localhost,name=JMS,service=JobScheduler,type=Broker"

    // Operations
    const val QUEUES_PATH = "Queues"
    const val TOPICS_PATH = "Topics"
    const val REMOVE_QUEUE_PATH = "removeQueue(java.lang.String)"
    const val REMOVE_TOPIC_PATH = "removeTopic(java.lang.String)"
    const val REMOVE_JOBS_PATH = "removeAllJobs()"
}
