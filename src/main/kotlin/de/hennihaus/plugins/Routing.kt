package de.hennihaus.plugins

import de.hennihaus.configurations.Configuration.API_VERSION
import de.hennihaus.routes.registerBankRoutes
import de.hennihaus.routes.registerBrokerRoutes
import de.hennihaus.routes.registerGroupRoutes
import de.hennihaus.routes.registerTaskRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.server.routing.IgnoreTrailingSlash
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule

fun Application.configureRouting() {
    val apiVersion = getProperty<String>(key = API_VERSION)

    install(Resources)
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                encodeDefaults = true
                serializersModule = IdKotlinXSerializationModule
            }
        )
    }
    install(IgnoreTrailingSlash)
    install(Routing) {
        route(path = apiVersion) {
            registerGroupRoutes()
            registerBankRoutes()
            registerTaskRoutes()
            registerBrokerRoutes()
        }
    }
}
