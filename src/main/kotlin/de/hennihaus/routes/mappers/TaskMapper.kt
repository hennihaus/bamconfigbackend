package de.hennihaus.routes.mappers

import de.hennihaus.models.Contact
import de.hennihaus.models.Endpoint
import de.hennihaus.models.EndpointType
import de.hennihaus.models.IntegrationStep
import de.hennihaus.models.Parameter
import de.hennihaus.models.ParameterType
import de.hennihaus.models.Response
import de.hennihaus.models.Task
import de.hennihaus.models.generated.rest.ContactDTO
import de.hennihaus.models.generated.rest.EndpointDTO
import de.hennihaus.models.generated.rest.ParameterDTO
import de.hennihaus.models.generated.rest.ResponseDTO
import de.hennihaus.models.generated.rest.TaskDTO
import de.hennihaus.plugins.ErrorMessage.INTEGRATION_STEP_NOT_FOUND_MESSAGE
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.NotFoundException
import java.net.URI
import java.util.UUID

fun Task.toTaskDTO() = TaskDTO(
    uuid = "$uuid",
    title = title,
    description = description,
    integrationStep = integrationStep.value,
    isOpenApiVerbose = isOpenApiVerbose,
    contact = contact.toContactDTO(),
    endpoints = endpoints.map { it.toEndpointDTO() },
    parameters = parameters.map { it.toParameterDTO() },
    responses = responses.map { it.toResponseDTO() },
    banks = banks.map { it.toBankDTO() },
)

fun TaskDTO.toTask() = Task(
    uuid = UUID.fromString(uuid),
    title = title,
    description = description,
    integrationStep = IntegrationStep.values().find { it.value == integrationStep } ?: throw NotFoundException(
        message = INTEGRATION_STEP_NOT_FOUND_MESSAGE,
    ),
    isOpenApiVerbose = isOpenApiVerbose,
    contact = contact.toContact(),
    endpoints = endpoints.map { it.toEndpoint() },
    parameters = parameters.map { it.toParameter() },
    responses = responses.map { it.toResponse() },
    banks = banks.map { it.toBank() },
)

private fun Contact.toContactDTO() = ContactDTO(
    uuid = "$uuid",
    firstname = firstname,
    lastname = lastname,
    email = email,
)

private fun ContactDTO.toContact() = Contact(
    uuid = UUID.fromString(uuid),
    firstname = firstname,
    lastname = lastname,
    email = email,
)

private fun Endpoint.toEndpointDTO() = EndpointDTO(
    uuid = "$uuid",
    type = type.name,
    url = "$url",
    docsUrl = "$docsUrl",
)

private fun EndpointDTO.toEndpoint() = Endpoint(
    uuid = UUID.fromString(uuid),
    type = EndpointType.valueOf(value = type),
    url = URI(url),
    docsUrl = URI(docsUrl),
)

private fun Parameter.toParameterDTO() = ParameterDTO(
    uuid = "$uuid",
    type = type.name,
    name = name,
    description = description,
    example = example,
)

private fun ParameterDTO.toParameter() = Parameter(
    uuid = UUID.fromString(uuid),
    type = ParameterType.valueOf(value = type),
    name = name,
    description = description,
    example = example,
)

private fun Response.toResponseDTO() = ResponseDTO(
    uuid = "$uuid",
    httpStatusCode = httpStatusCode.value,
    contentType = "$contentType",
    description = description,
    example = example,
)

private fun ResponseDTO.toResponse() = Response(
    uuid = UUID.fromString(uuid),
    httpStatusCode = HttpStatusCode.fromValue(value = httpStatusCode),
    contentType = ContentType.parse(value = contentType),
    description = description,
    example = example,
)
