package de.hennihaus.models

import de.hennihaus.bamdatamodel.serializers.UUIDSerializer
import de.hennihaus.models.serializers.ContentTypeSerializer
import de.hennihaus.models.serializers.HttpStatusCodeSerializer
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Response(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    @Serializable(with = HttpStatusCodeSerializer::class)
    val httpStatusCode: HttpStatusCode,
    @Serializable(with = ContentTypeSerializer::class)
    val contentType: ContentType,
    val description: String,
    val example: String,
)
