package de.hennihaus.models

import de.hennihaus.bamdatamodel.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Contact(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val firstname: String,
    val lastname: String,
    val email: String,
)
