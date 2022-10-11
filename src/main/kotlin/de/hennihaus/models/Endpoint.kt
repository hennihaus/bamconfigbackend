package de.hennihaus.models

import java.net.URI
import java.util.UUID

data class Endpoint(
    val uuid: UUID,
    val type: EndpointType,
    val url: URI,
    val docsUrl: URI,
)
