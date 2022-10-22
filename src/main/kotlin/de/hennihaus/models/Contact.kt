package de.hennihaus.models

import java.util.UUID

data class Contact(
    val uuid: UUID,
    val firstname: String,
    val lastname: String,
    val email: String,
)
