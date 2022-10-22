package de.hennihaus.models

import java.util.UUID

data class Parameter(
    val uuid: UUID,
    val name: String,
    val type: ParameterType,
    val description: String,
    val example: String,
)
