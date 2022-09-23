package de.hennihaus.models.serializers

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

    override fun deserialize(decoder: Decoder): ContentType = decoder.decodeString().let {
        ContentType.parse(value = it)
    }

    override fun serialize(encoder: Encoder, value: ContentType) = encoder.encodeString(value = "$value")
}
