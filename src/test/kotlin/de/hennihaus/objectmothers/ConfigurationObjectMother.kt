package de.hennihaus.objectmothers

import de.hennihaus.configurations.BrokerConfiguration

object ConfigurationObjectMother {

    const val DEFAULT_PROTOCOL = "http"
    const val DEFAULT_HOST = "0.0.0.0"
    const val DEFAULT_PORT = 8080
    const val DEFAULT_MAX_RETRIES = 2

    fun getBrokerConfiguration(
        protocol: String = DEFAULT_PROTOCOL,
        host: String = DEFAULT_HOST,
        port: Int = DEFAULT_PORT,
        maxRetries: Int = DEFAULT_MAX_RETRIES,
        authorizationHeader: String = "Basic dGVzdDp0ZXN0",
        originHeader: String = "http://localhost",
    ) = BrokerConfiguration(
        protocol = protocol,
        host = host,
        port = port,
        maxRetries = maxRetries,
        authorizationHeader = authorizationHeader,
        originHeader = originHeader,
    )
}
