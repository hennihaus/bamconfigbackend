package de.hennihaus.models

import de.hennihaus.bamdatamodel.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Parameter(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val name: String,
    val type: ParameterType,
    val description: String,
    val example: String,
)
