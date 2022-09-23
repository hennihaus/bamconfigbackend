package de.hennihaus.models.serializers

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object HttpStatusCodeSerializer : KSerializer<HttpStatusCode> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "HttpStatusCode",
        kind = PrimitiveKind.INT,
    )

    override fun deserialize(decoder: Decoder): HttpStatusCode = decoder.decodeInt().let {
        HttpStatusCode.fromValue(value = it)
    }

    override fun serialize(encoder: Encoder, value: HttpStatusCode) = encoder.encodeInt(value = value.value)
}
