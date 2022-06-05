package de.hennihaus.configurations

import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module

val brokerModule = module {
    single {
        CIO.create()
    }
    single {
        val protocol = getProperty<String>(key = BrokerConfiguration.ACTIVE_MQ_PROTOCOL)
        val host = getProperty<String>(key = BrokerConfiguration.ACTIVE_MQ_HOST)
        val port = getProperty<String>(key = BrokerConfiguration.ACTIVE_MQ_PORT)
        val maxRetries = getProperty<String>(key = BrokerConfiguration.ACTIVE_MQ_RETRIES)
        val authorizationHeader = getProperty<String>(key = BrokerConfiguration.ACTIVE_MQ_HEADER_AUTHORIZATION)
        val originHeader = getProperty<String>(key = BrokerConfiguration.ACTIVE_MQ_HEADER_ORIGIN)

        BrokerConfiguration(
            protocol = protocol,
            host = host,
            port = port.toInt(),
            maxRetries = maxRetries.toInt(),
            authorizationHeader = authorizationHeader,
            originHeader = originHeader
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
        const val ACTIVE_MQ_PROTOCOL = "ktor.activemq.protocol"
        const val ACTIVE_MQ_HOST = "ktor.activemq.host"
        const val ACTIVE_MQ_PORT = "ktor.activemq.port"
        const val ACTIVE_MQ_RETRIES = "ktor.activemq.retries"
        const val ACTIVE_MQ_HEADER_AUTHORIZATION = "ktor.activemq.headers.authorization"
        const val ACTIVE_MQ_HEADER_ORIGIN = "ktor.activemq.headers.origin"
    }
}
