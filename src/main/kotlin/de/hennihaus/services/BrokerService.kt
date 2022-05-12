package de.hennihaus.services

interface BrokerService {
    suspend fun deleteQueueByName(name: String)

    suspend fun resetBroker()
}
