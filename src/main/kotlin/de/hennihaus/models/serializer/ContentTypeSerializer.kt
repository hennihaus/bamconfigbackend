package de.hennihaus.models.serializer

import io.ktor.http.ContentType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ContentTypeSerializer : KSerializer<ContentType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "ContentType",
        kind = PrimitiveKind.STRING,
    )

    override fun deserialize(decoder: Decoder): ContentType {
        val type = decoder.decodeString()
        return ContentType.parse(value = type)
    }

    override fun serialize(encoder: Encoder, value: ContentType) {
        encoder.encodeString(value = "$value")
    }
}
