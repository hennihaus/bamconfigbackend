package de.hennihaus.testutils

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object MockClientBuilder {

    fun getMockEngine(
        content: String = "",
        status: HttpStatusCode = HttpStatusCode.OK,
        headers: Headers = Headers.Empty
    ) = MockEngine {
        respond(
            content = content,
            status = status,
            headers = mergeHeaders(
                headersOf(
                    HttpHeaders.ContentType to listOf("${ContentType.Application.Json}")
                ),
                headers
            )
        )
    }

    fun getMockClient(
        engine: MockEngine = getMockEngine(),
        urlString: String = "http://localhost:8080",
        contentType: ContentType = ContentType.Any,
        json: Json = Json { ignoreUnknownKeys = true }
    ) = HttpClient(engine = engine) {
        install(plugin = ContentNegotiation) {
            json(
                contentType = contentType,
                json = json
            )
        }
        install(plugin = Resources)
        install(plugin = DefaultRequest) {
            url(urlString = urlString)
        }
    }

    private fun mergeHeaders(vararg headers: Headers): Headers = headersOf(
        pairs = headers.toList().flatMap { it.entries() }.map { it.key to it.value }.toTypedArray()
    )
}
