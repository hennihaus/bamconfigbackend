package de.hennihaus.models.rest

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String,
    val dateTime: LocalDateTime,
)
