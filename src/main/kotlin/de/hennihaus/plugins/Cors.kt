package de.hennihaus.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCors() {
    val allowedHost = environment.config.property(path = "ktor.deployment.cors.allowedHost").getString()
    val allowedProtocol = environment.config.property(path = "ktor.deployment.cors.allowedProtocol").getString()
    install(CORS) {
        allowCredentials = true

        allowHost(host = allowedHost, schemes = listOf(allowedProtocol))

        allowHeader(header = HttpHeaders.ContentType)
        allowHeader(header = HttpHeaders.Authorization)

        allowMethod(method = HttpMethod.Options)
        allowMethod(method = HttpMethod.Get)
        allowMethod(method = HttpMethod.Post)
        allowMethod(method = HttpMethod.Put)
        allowMethod(method = HttpMethod.Patch)
        allowMethod(method = HttpMethod.Delete)
    }
}
