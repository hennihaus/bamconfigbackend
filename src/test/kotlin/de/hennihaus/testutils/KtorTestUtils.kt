package de.hennihaus.testutils

import de.hennihaus.module
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.ApplicationTestBuilder
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
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
                    serializersModule = IdKotlinXSerializationModule
                }
            )
        }
    }
