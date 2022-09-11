package de.hennihaus.testutils

import de.hennihaus.models.serializer.ContentTypeSerializer
import de.hennihaus.models.serializer.HttpStatusCodeSerializer
import de.hennihaus.models.serializer.URISerializer
import de.hennihaus.models.serializer.UUIDSerializer
import de.hennihaus.module
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.koin.core.module.Module
import io.ktor.server.testing.testApplication as ktorTestApplication

object KtorTestUtils {
    fun testApplicationWith(vararg mockModules: Module, block: suspend ApplicationTestBuilder.() -> Unit) {
        ktorTestApplication {
            application {
                module(koinModules = mockModules)
            }
            block()
        }
    }
}

val ApplicationTestBuilder.testClient
    get() = createClient {
        install(ContentNegotiation) {
            json(
                Json {
                    serializersModule = SerializersModule {
                        contextual(serializer = ContentTypeSerializer)
                        contextual(serializer = HttpStatusCodeSerializer)
                        contextual(serializer = URISerializer)
                        contextual(serializer = UUIDSerializer)
                    }
                }
            )
        }
    }
