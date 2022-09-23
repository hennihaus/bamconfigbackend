package de.hennihaus.models

import de.hennihaus.bamdatamodel.Bank
import de.hennihaus.bamdatamodel.serializers.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Task(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val title: String,
    val description: String,
    val integrationStep: IntegrationStep,
    val isOpenApiVerbose: Boolean,
    val contact: Contact,
    val endpoints: List<Endpoint>,
    val parameters: List<Parameter>,
    val responses: List<Response>,
    val banks: List<Bank>,
)
