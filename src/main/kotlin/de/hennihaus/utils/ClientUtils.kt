package de.hennihaus.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.jackson.jackson

fun HttpClientConfig<*>.configureMonitoring() = install(plugin = Logging) {
    logger = Logger.DEFAULT
    level = LogLevel.INFO
}

fun HttpClientConfig<*>.configureRetryBehavior(maxRetries: Int) = install(plugin = HttpRequestRetry) {
    retryOnServerErrors(maxRetries = maxRetries)
    exponentialDelay()
}

fun HttpClientConfig<*>.configureSerialization() = install(plugin = ContentNegotiation) {
    jackson {
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
}
