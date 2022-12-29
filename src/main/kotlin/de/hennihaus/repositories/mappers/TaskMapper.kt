package de.hennihaus.repositories.mappers

import de.hennihaus.configurations.Configuration.DEFAULT_ZONE_ID
import de.hennihaus.models.Contact
import de.hennihaus.models.Endpoint
import de.hennihaus.models.EndpointType
import de.hennihaus.models.IntegrationStep
import de.hennihaus.models.Parameter
import de.hennihaus.models.ParameterType
import de.hennihaus.models.Response
import de.hennihaus.models.Task
import de.hennihaus.repositories.entities.ContactEntity
import de.hennihaus.repositories.entities.EndpointEntity
import de.hennihaus.repositories.entities.ParameterEntity
import de.hennihaus.repositories.entities.ResponseEntity
import de.hennihaus.repositories.entities.TaskEntity
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import java.net.URI
import java.time.LocalDateTime
import java.time.ZoneId

fun TaskEntity.toTask() = Task(
    uuid = id.value,
    title = title,
    description = description,
    integrationStep = IntegrationStep.values().find { it.value == integrationStep }
        ?: throw IllegalArgumentException("No enum constant $integrationStep in IntegrationStep found"),
    isOpenApiVerbose = isOpenApiVerbose,
    contact = contact.toContact(),
    endpoints = endpoints.map { it.toEndpoint() },
    parameters = parameters.map { it.toParameter() },
    responses = responses.map { it.toResponse() },
    banks = banks.map { it.toBank() },
    updatedAt = LocalDateTime.ofInstant(updatedAt, ZoneId.of(DEFAULT_ZONE_ID)),
)

fun ContactEntity.toContact() = Contact(
    uuid = id.value,
    firstname = firstname,
    lastname = lastname,
    email = email,
)

fun EndpointEntity.toEndpoint() = Endpoint(
    uuid = id.value,
    type = EndpointType.valueOf(value = type),
    url = URI(url),
    docsUrl = URI(docsUrl),
)

fun ParameterEntity.toParameter() = Parameter(
    uuid = id.value,
    name = name,
    type = ParameterType.valueOf(value = type),
    description = description,
    example = example,
)

fun ResponseEntity.toResponse() = Response(
    uuid = id.value,
    httpStatusCode = HttpStatusCode.fromValue(value = httpStatusCode),
    contentType = ContentType.parse(value = contentType),
    description = description,
    example = example,
)
