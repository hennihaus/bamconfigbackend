package de.hennihaus.models

import de.hennihaus.bamdatamodel.serializers.URISerializer
import de.hennihaus.bamdatamodel.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.net.URI
import java.util.UUID

@Serializable
data class Endpoint(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val type: EndpointType,
    @Serializable(with = URISerializer::class)
    val url: URI,
    @Serializable(with = URISerializer::class)
    val docsUrl: URI,
)
