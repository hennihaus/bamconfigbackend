package de.hennihaus.models.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor(
        serialName = "UUID",
        kind = PrimitiveKind.STRING,
    )

    override fun deserialize(decoder: Decoder): UUID = decoder.decodeString().let {
        UUID.fromString(it)
    }

    override fun serialize(encoder: Encoder, value: UUID) = encoder.encodeString(value = "$value")
}
