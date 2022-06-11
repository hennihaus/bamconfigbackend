package de.hennihaus.models.serializer

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

    override fun deserialize(decoder: Decoder): HttpStatusCode {
        val code = decoder.decodeInt()
        return HttpStatusCode.fromValue(value = code)
    }

    override fun serialize(encoder: Encoder, value: HttpStatusCode) {
        encoder.encodeInt(value = value.value)
    }
}
