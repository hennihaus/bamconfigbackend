package de.hennihaus.plugins

import de.hennihaus.configurations.Configuration.ALLOWED_FIRST_FRONTEND_HOST
import de.hennihaus.configurations.Configuration.ALLOWED_FIRST_FRONTEND_PROTOCOL
import de.hennihaus.configurations.Configuration.ALLOWED_SECOND_FRONTEND_HOST
import de.hennihaus.configurations.Configuration.ALLOWED_SECOND_FRONTEND_PROTOCOL
import de.hennihaus.configurations.Configuration.ALLOWED_SWAGGER_HOST
import de.hennihaus.configurations.Configuration.ALLOWED_SWAGGER_PROTOCOL
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCors() {
    val allowedFirstFrontendProtocol = environment.config.property(path = ALLOWED_FIRST_FRONTEND_PROTOCOL).getString()
    val allowedFirstFrontendHost = environment.config.property(path = ALLOWED_FIRST_FRONTEND_HOST).getString()
    val allowedSecondFrontendProtocol = environment.config.property(path = ALLOWED_SECOND_FRONTEND_PROTOCOL).getString()
    val allowedSecondFrontendHost = environment.config.property(path = ALLOWED_SECOND_FRONTEND_HOST).getString()
    val allowedSwaggerProtocol = environment.config.property(path = ALLOWED_SWAGGER_PROTOCOL).getString()
    val allowedSwaggerHost = environment.config.property(path = ALLOWED_SWAGGER_HOST).getString()

    install(plugin = CORS) {
        allowCredentials = true

        allowHost(host = allowedFirstFrontendHost, schemes = listOf(allowedFirstFrontendProtocol))
        allowHost(host = allowedSecondFrontendHost, schemes = listOf(allowedSecondFrontendProtocol))
        allowHost(host = allowedSwaggerHost, schemes = listOf(allowedSwaggerProtocol))

        allowHeader(header = HttpHeaders.ContentType)
        allowHeader(header = HttpHeaders.Authorization)

        allowMethod(method = HttpMethod.Options)
        allowMethod(method = HttpMethod.Get)
        allowMethod(method = HttpMethod.Put)
        allowMethod(method = HttpMethod.Patch)
        allowMethod(method = HttpMethod.Delete)
    }
}
