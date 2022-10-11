package de.hennihaus.models

import de.hennihaus.bamdatamodel.Bank
import java.util.UUID

data class Task(
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
