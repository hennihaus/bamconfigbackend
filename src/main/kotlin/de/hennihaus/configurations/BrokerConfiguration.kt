package de.hennihaus.configurations

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.serialization.json.Json
import org.koin.dsl.module

object BrokerConfiguration {

    const val ACTIVE_MQ_PROTOCOL = "ktor.activemq.protocol"
    const val ACTIVE_MQ_HOST = "ktor.activemq.host"
    const val ACTIVE_MQ_PORT = "ktor.activemq.port"
    const val ACTIVE_MQ_RETRIES = "ktor.activemq.retries"
    const val ACTIVE_MQ_HEADER_AUTHORIZATION = "ktor.activemq.headers.authorization"
    const val ACTIVE_MQ_HEADER_ORIGIN = "ktor.activemq.headers.origin"

    val brokerModule = module {

        single {
            val protocol = getProperty<String>(key = ACTIVE_MQ_PROTOCOL)
            val host = getProperty<String>(key = ACTIVE_MQ_HOST)
            val port = getProperty<String>(key = ACTIVE_MQ_PORT)
            val retries = getProperty<String>(key = ACTIVE_MQ_RETRIES)
            val authorizationHeader = getProperty<String>(key = ACTIVE_MQ_HEADER_AUTHORIZATION)
            val originHeader = getProperty<String>(key = ACTIVE_MQ_HEADER_ORIGIN)

            HttpClient(CIO) {
                // configurations
                expectSuccess = true

                // plugins
                configureMonitoring()
                configureSerialization()
                configureRetryBehavior(
                    maxRetries = retries.toInt()
                )
                configureDefaultRequests(
                    protocol = URLProtocol.createOrDefault(name = protocol),
                    host = host,
                    port = port.toInt(),
                    authorizationHeader = authorizationHeader,
                    originHeader = originHeader
                )
            }
        }
    }

    private fun HttpClientConfig<CIOEngineConfig>.configureMonitoring() = install(plugin = Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.INFO
    }

    private fun HttpClientConfig<CIOEngineConfig>.configureSerialization() {
        install(plugin = ContentNegotiation) {
            json(
                contentType = ContentType.Any,
                json = Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        install(plugin = Resources)
    }

    private fun HttpClientConfig<CIOEngineConfig>.configureRetryBehavior(maxRetries: Int) {
        install(plugin = HttpRequestRetry) {
            retryOnServerErrors(maxRetries = maxRetries)
            exponentialDelay()
        }
    }

    private fun HttpClientConfig<CIOEngineConfig>.configureDefaultRequests(
        protocol: URLProtocol,
        host: String,
        port: Int,
        authorizationHeader: String,
        originHeader: String
    ) {
        install(plugin = DefaultRequest) {
            url {
                this.protocol = protocol
                this.host = host
                this.port = port
            }
            headers {
                appendIfNameAbsent(name = HttpHeaders.Authorization, value = authorizationHeader)
                appendIfNameAbsent(name = HttpHeaders.Origin, value = originHeader)
            }
        }
    }
}
