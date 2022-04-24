package de.hennihaus.testutils

import de.hennihaus.services.BankService
import de.hennihaus.services.GroupService
import de.hennihaus.services.TaskService
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.util.KtorDsl
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import de.hennihaus.module as ktorModule
import io.ktor.server.testing.testApplication as ktorTestApplication
import org.koin.dsl.module as koinModule

object KtorTestBuilder {
    @KtorDsl
    fun testApplication(mockModule: Module, block: suspend ApplicationTestBuilder.() -> Unit) {
        ktorTestApplication {
            environment {
                config = ApplicationConfig(configPath = "application-test.conf")
            }
            application {
                ktorModule(
                    koinModules = listOf(
                        koinModule {
                            single { mockk<GroupService>() }
                            single { mockk<BankService>() }
                            single { mockk<TaskService>() }
                        },
                        mockModule
                    )
                )
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
