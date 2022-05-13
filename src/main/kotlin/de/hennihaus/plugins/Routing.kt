package de.hennihaus.plugins

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
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule

fun Application.configureRouting() {
    install(Resources)
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                serializersModule = IdKotlinXSerializationModule
            }
        )
    }
    install(IgnoreTrailingSlash)
    install(Routing) {
        registerGroupRoutes()
        registerBankRoutes()
        registerTaskRoutes()
        registerBrokerRoutes()
    }
}
