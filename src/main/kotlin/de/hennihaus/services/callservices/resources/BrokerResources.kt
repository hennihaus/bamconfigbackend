package de.hennihaus.services.callservices.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

object BrokerPaths {
    // Base path
    const val ACTIVE_MQ_PATH = "/api/jolokia"

    // Operation type
    const val READ_PATH = "/read"
    const val EXEC_PATH = "/exec"

    // Beans
    const val M_BEAN_PATH = "/org.apache.activemq:type=Broker,brokerName=localhost"
    const val JMS_M_BEAN_PATH = "/org.apache.activemq:brokerName=localhost,name=JMS,service=JobScheduler,type=Broker"

    // Operations
    const val QUEUES_PATH = "/Queues"
    const val TOPICS_PATH = "/Topics"
    const val REMOVE_QUEUE_PATH = "/removeQueue(java.lang.String)/{name}"
    const val REMOVE_TOPIC_PATH = "/removeTopic(java.lang.String)/{name}"
    const val REMOVE_JOBS_PATH = "/removeAllJobs()"
}

@Serializable
@Resource(BrokerPaths.ACTIVE_MQ_PATH)
class Broker {

    @Serializable
    @Resource(BrokerPaths.READ_PATH)
    data class Read(val parent: Broker = Broker()) {

        @Serializable
        @Resource(BrokerPaths.M_BEAN_PATH)
        data class MBean(val parent: Read = Read()) {

            @Serializable
            @Resource(BrokerPaths.QUEUES_PATH)
            data class Queues(val parent: MBean = MBean())

            @Serializable
            @Resource(BrokerPaths.TOPICS_PATH)
            data class Topics(val parent: MBean = MBean())
        }
    }

    @Serializable
    @Resource(BrokerPaths.EXEC_PATH)
    data class Exec(val parent: Broker = Broker()) {

        @Serializable
        @Resource(BrokerPaths.M_BEAN_PATH)
        data class MBean(val parent: Exec = Exec()) {

            @Serializable
            @Resource(BrokerPaths.REMOVE_QUEUE_PATH)
            data class RemoveQueue(val parent: MBean = MBean(), val name: String)

            @Serializable
            @Resource(BrokerPaths.REMOVE_TOPIC_PATH)
            data class RemoveTopic(val parent: MBean = MBean(), val name: String)
        }

        @Serializable
        @Resource(BrokerPaths.JMS_M_BEAN_PATH)
        data class JobMBean(val parent: Exec = Exec()) {

            @Serializable
            @Resource(BrokerPaths.REMOVE_JOBS_PATH)
            data class RemoveAllJobs(val parent: JobMBean = JobMBean())
        }
    }
}
