package de.hennihaus.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCors() {
    val frontendAllowedHost = getProperty<String>(key = "ktor.deployment.cors.frontend.allowedHost")
    val frontendAllowedProtocol = getProperty<String>(key = "ktor.deployment.cors.frontend.allowedProtocol")
    val swaggerAllowedHost = getProperty<String>(key = "ktor.deployment.cors.swagger.allowedHost")
    val swaggerAllowedProtocol = getProperty<String>(key = "ktor.deployment.cors.swagger.allowedProtocol")

    install(CORS) {
        allowCredentials = true

        allowHost(host = frontendAllowedHost, schemes = listOf(frontendAllowedProtocol))
        allowHost(host = swaggerAllowedHost, schemes = listOf(swaggerAllowedProtocol))

        allowHeader(header = HttpHeaders.ContentType)
        allowHeader(header = HttpHeaders.Authorization)

        allowMethod(method = HttpMethod.Options)
        allowMethod(method = HttpMethod.Get)
        allowMethod(method = HttpMethod.Put)
        allowMethod(method = HttpMethod.Patch)
        allowMethod(method = HttpMethod.Delete)
    }
}
