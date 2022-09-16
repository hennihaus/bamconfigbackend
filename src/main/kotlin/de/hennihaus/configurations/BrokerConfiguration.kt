package de.hennihaus.configurations

import de.hennihaus.configurations.BrokerConfiguration.Companion.ACTIVE_MQ_HEADER_AUTHORIZATION
import de.hennihaus.configurations.BrokerConfiguration.Companion.ACTIVE_MQ_HEADER_ORIGIN
import de.hennihaus.configurations.BrokerConfiguration.Companion.ACTIVE_MQ_HOST
import de.hennihaus.configurations.BrokerConfiguration.Companion.ACTIVE_MQ_PORT
import de.hennihaus.configurations.BrokerConfiguration.Companion.ACTIVE_MQ_PROTOCOL
import de.hennihaus.configurations.BrokerConfiguration.Companion.ACTIVE_MQ_RETRIES
import org.koin.dsl.module

val brokerModule = module {
    single {
        val protocol = getProperty<String>(key = ACTIVE_MQ_PROTOCOL)
        val host = getProperty<String>(key = ACTIVE_MQ_HOST)
        val port = getProperty<String>(key = ACTIVE_MQ_PORT)
        val maxRetries = getProperty<String>(key = ACTIVE_MQ_RETRIES)
        val authorizationHeader = getProperty<String>(key = ACTIVE_MQ_HEADER_AUTHORIZATION)
        val originHeader = getProperty<String>(key = ACTIVE_MQ_HEADER_ORIGIN)

        BrokerConfiguration(
            protocol = protocol,
            host = host,
            port = port.toInt(),
            maxRetries = maxRetries.toInt(),
            authorizationHeader = authorizationHeader,
            originHeader = originHeader,
        )
    }
}

data class BrokerConfiguration(
    val protocol: String,
    val host: String,
    val port: Int,
    val maxRetries: Int,
    val authorizationHeader: String,
    val originHeader: String,
) {
    companion object {
        const val ACTIVE_MQ_PROTOCOL = "activemq.protocol"
        const val ACTIVE_MQ_HOST = "activemq.host"
        const val ACTIVE_MQ_PORT = "activemq.port"
        const val ACTIVE_MQ_RETRIES = "activemq.retries"
        const val ACTIVE_MQ_HEADER_AUTHORIZATION = "activemq.headers.authorization"
        const val ACTIVE_MQ_HEADER_ORIGIN = "activemq.headers.origin"
    }
}
