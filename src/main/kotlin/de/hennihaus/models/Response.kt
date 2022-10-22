package de.hennihaus.models

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import java.util.UUID

data class Response(
    val uuid: UUID,
    val httpStatusCode: HttpStatusCode,
    val contentType: ContentType,
    val description: String,
    val example: String,
)
