package de.hennihaus.testutils

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.addDeserializer
import com.fasterxml.jackson.module.kotlin.addSerializer
import de.hennihaus.models.IntegrationStep
import de.hennihaus.module
import io.kotest.extensions.time.withConstantNow
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import org.koin.core.module.Module
import java.time.LocalDateTime
import io.ktor.server.testing.testApplication as ktorTestApplication

object KtorTestUtils {

    private const val TEST_CONFIG_FILE = "application-test.conf"

    fun testApplicationWith(vararg mockModules: Module, block: suspend ApplicationTestBuilder.() -> Unit) {
        ktorTestApplication {
            environment {
                config = ApplicationConfig(configPath = TEST_CONFIG_FILE)
            }
            application {
                module(
                    configFilePath = TEST_CONFIG_FILE,
                    koinModules = mockModules,
                )
            }
            withConstantNow(now = LocalDateTime.now()) {
                block()
            }
        }
    }
}

val ApplicationTestBuilder.testClient
    get() = createClient {
        install(ContentNegotiation) {
            jackson {
                registerModule(JavaTimeModule())
                registerModule(
                    SimpleModule()
                        .addSerializer(kClass = HttpStatusCode::class, serializer = HttpStatusCodeSerializer)
                        .addDeserializer(kClass = HttpStatusCode::class, deserializer = HttpStatusCodeDeserializer)
                        .addSerializer(kClass = ContentType::class, serializer = ContentTypeSerializer)
                        .addDeserializer(kClass = ContentType::class, deserializer = ContentTypeDeserializer)
                        .addSerializer(kClass = IntegrationStep::class, serializer = IntegrationStepSerializer)
                        .addDeserializer(kClass = IntegrationStep::class, deserializer = IntegrationStepDeserializer)
                )
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            }
        }
    }
