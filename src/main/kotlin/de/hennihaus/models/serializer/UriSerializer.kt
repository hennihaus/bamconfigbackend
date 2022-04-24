package de.hennihaus.models.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.URI

object UriSerializer : KSerializer<URI> {
    override val descriptor = PrimitiveSerialDescriptor("URI", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): URI {
        val url = decoder.decodeString()
        return URI(url)
    }

    override fun serialize(encoder: Encoder, value: URI) {
        encoder.encodeString(value = value.toString())
    }
}
