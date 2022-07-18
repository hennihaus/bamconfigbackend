package de.hennihaus.models.rest

import kotlinx.serialization.Serializable

@Serializable
data class ExistsResponse(
    val exists: Boolean,
)
