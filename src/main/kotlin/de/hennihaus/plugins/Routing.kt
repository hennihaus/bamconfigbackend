package de.hennihaus.plugins

import de.hennihaus.configurations.Configuration.API_VERSION
import de.hennihaus.models.serializer.ContentTypeSerializer
import de.hennihaus.models.serializer.HttpStatusCodeSerializer
import de.hennihaus.models.serializer.URISerializer
import de.hennihaus.models.serializer.UUIDSerializer
import de.hennihaus.routes.registerBankRoutes
import de.hennihaus.routes.registerBrokerRoutes
import de.hennihaus.routes.registerStatisticRoutes
import de.hennihaus.routes.registerTaskRoutes
import de.hennihaus.routes.registerTeamRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.server.routing.IgnoreTrailingSlash
import io.ktor.server.routing.Routing
import io.ktor.server.routing.route
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

fun Application.configureRouting() {
    val apiVersion = environment.config.property(path = API_VERSION).getString()

    install(Resources)
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
                encodeDefaults = true
                serializersModule = SerializersModule {
                    contextual(serializer = ContentTypeSerializer)
                    contextual(serializer = HttpStatusCodeSerializer)
                    contextual(serializer = URISerializer)
                    contextual(serializer = UUIDSerializer)
                }
            }
        )
    }
    install(IgnoreTrailingSlash)
    install(Routing) {
        route(path = "/$apiVersion") {
            registerTeamRoutes()
            registerBankRoutes()
            registerTaskRoutes()
            registerBrokerRoutes()
            registerStatisticRoutes()
        }
    }
}
