package de.hennihaus.services

import de.hennihaus.services.callservices.BrokerCallService
import de.hennihaus.utils.logger
import org.koin.core.annotation.Single

@Single
class BrokerService(private val brokerCall: BrokerCallService) {

    private val log by logger()

    suspend fun deleteQueueByName(name: String) {
        brokerCall.deleteQueueByName(name = name)
    }

    suspend fun resetBroker() {
        resetQueues()
        resetTopics()
        brokerCall.deleteAllJobs()
    }

    private suspend fun resetQueues() {
        brokerCall.getAllQueues().value.map {
            it.objectName.substringAfter(
                delimiter = DESTINATION_NAME_DELIMITER,
                missingDelimiterValue = EMPTY_STRING
            ).substringBefore(
                delimiter = DESTINATION_TYPE_DELIMITER,
                missingDelimiterValue = EMPTY_STRING
            ).also { queue ->
                if (queue == EMPTY_STRING) log.warning(
                    "Queue has no destinationName or destinationType: ${it.objectName}"
                )
            }
        }.filter { queue ->
            queue != EMPTY_STRING
        }.forEach { queue ->
            brokerCall.deleteQueueByName(name = queue)
        }
    }

    private suspend fun resetTopics() {
        brokerCall.getAllTopics().value.map {
            it.objectName.substringAfter(
                delimiter = DESTINATION_NAME_DELIMITER,
                missingDelimiterValue = EMPTY_STRING
            ).substringBefore(
                delimiter = DESTINATION_TYPE_DELIMITER,
                missingDelimiterValue = EMPTY_STRING
            ).also { queue ->
                if (queue == EMPTY_STRING) log.warning(
                    "Topic has no destinationName or destinationType: ${it.objectName}!"
                )
            }
        }.filter { queue ->
            queue != EMPTY_STRING
        }.forEach { queue ->
            brokerCall.deleteTopicByName(name = queue)
        }
    }

    companion object {
        private const val EMPTY_STRING = ""
        const val DESTINATION_NAME_DELIMITER = ",destinationName="
        const val DESTINATION_TYPE_DELIMITER = ",destinationType="
    }
}
