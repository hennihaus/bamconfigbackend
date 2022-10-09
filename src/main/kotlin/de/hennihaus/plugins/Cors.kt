package de.hennihaus.plugins

import de.hennihaus.configurations.Configuration.ALLOWED_FRONTEND_HOST
import de.hennihaus.configurations.Configuration.ALLOWED_FRONTEND_PROTOCOL
import de.hennihaus.configurations.Configuration.ALLOWED_SWAGGER_HOST
import de.hennihaus.configurations.Configuration.ALLOWED_SWAGGER_PROTOCOL
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCors() {
    val allowedFrontendProtocol = environment.config.property(path = ALLOWED_FRONTEND_PROTOCOL).getString()
    val allowedFrontendHost = environment.config.property(path = ALLOWED_FRONTEND_HOST).getString()
    val allowedSwaggerProtocol = environment.config.property(path = ALLOWED_SWAGGER_PROTOCOL).getString()
    val allowedSwaggerHost = environment.config.property(path = ALLOWED_SWAGGER_HOST).getString()

    install(plugin = CORS) {
        allowCredentials = true

        allowHost(host = allowedFrontendHost, schemes = listOf(allowedFrontendProtocol))
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
