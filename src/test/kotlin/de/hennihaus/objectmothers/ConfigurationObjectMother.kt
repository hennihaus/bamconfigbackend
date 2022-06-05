package de.hennihaus.objectmothers

import de.hennihaus.configurations.BrokerConfiguration

object ConfigurationObjectMother {

    fun getBrokerConfiguration(
        protocol: String = "http",
        host: String = "0.0.0.0",
        port: Int = 8080,
        maxRetries: Int = 2,
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
