package de.hennihaus.routes.validations

import de.hennihaus.models.EndpointType
import de.hennihaus.models.IntegrationStep
import de.hennihaus.models.ParameterType
import de.hennihaus.models.generated.rest.ContactDTO
import de.hennihaus.models.generated.rest.EndpointDTO
import de.hennihaus.models.generated.rest.ParameterDTO
import de.hennihaus.models.generated.rest.ResponseDTO
import de.hennihaus.models.generated.rest.TaskDTO
import de.hennihaus.routes.validations.ValidationService.Companion.NAME_MAX_LENGTH
import de.hennihaus.routes.validations.ValidationService.Companion.NAME_MIN_LENGTH
import de.hennihaus.services.TaskService
import de.hennihaus.utils.validations.contentType
import de.hennihaus.utils.validations.email
import de.hennihaus.utils.validations.httpStatusCode
import de.hennihaus.utils.validations.json
import de.hennihaus.utils.validations.localDateTime
import de.hennihaus.utils.validations.notConst
import de.hennihaus.utils.validations.oneOf
import de.hennihaus.utils.validations.unique
import de.hennihaus.utils.validations.url
import de.hennihaus.utils.validations.uuid
import io.konform.validation.Validation
import io.konform.validation.jsonschema.enum
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.uniqueItems
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.koin.core.annotation.Single

@Single
class TaskValidationService(private val task: TaskService) : ValidationService<TaskDTO, Any> {

    override suspend fun bodyValidation(body: TaskDTO): Validation<TaskDTO> = coroutineScope {
        val asyncValidations = listOf(
            async { titleValidation(body = body) },
            async { parametersValidation(body = body) },
            async { responsesValidation(body = body) },
        )
        val syncValidations = listOf(
            integrationStepValidation(),
            contactValidation(),
            endpointsValidation(),
        )
        val validations = asyncValidations.awaitAll() + syncValidations

        Validation {
            TaskDTO::uuid {
                uuid()
            }
            TaskDTO::description {
                maxLength(length = TASK_DESCRIPTION_MAX_LENGTH)
            }
            TaskDTO::updatedAt {
                localDateTime()
            }
            validations.forEach {
                run(validation = it)
            }
        }
    }

    private suspend fun titleValidation(body: TaskDTO): Validation<TaskDTO> {
        val isTitleUnique = task.isTitleUnique(
            id = body.uuid,
            title = body.title,
        )

        return Validation {
            TaskDTO::title {
                minLength(length = TASK_TITLE_MIN_LENGTH)
                maxLength(length = TASK_TITLE_MAX_LENGTH)
                unique(isUnique = isTitleUnique)
            }
        }
    }

    private fun integrationStepValidation(): Validation<TaskDTO> {
        val allowed = enumValues<IntegrationStep>().map { it.value }.toTypedArray()

        return Validation {
            TaskDTO::integrationStep {
                enum(allowed = allowed)
            }
        }
    }

    private fun contactValidation(): Validation<TaskDTO> = Validation {
        TaskDTO::contact {
            ContactDTO::uuid {
                uuid()
            }
            ContactDTO::firstname {
                minLength(length = NAME_MIN_LENGTH)
                maxLength(length = NAME_MAX_LENGTH)
            }
            ContactDTO::lastname {
                minLength(length = NAME_MIN_LENGTH)
                maxLength(length = NAME_MAX_LENGTH)
            }
            ContactDTO::email {
                email()
            }
        }
    }

    private fun endpointsValidation(): Validation<TaskDTO> = Validation {
        TaskDTO::endpoints onEach {
            EndpointDTO::uuid {
                uuid()
            }
            EndpointDTO::type {
                enum<EndpointType>()
            }
            EndpointDTO::url {
                url()
            }
            EndpointDTO::docsUrl {
                url()
            }
        }
    }

    private suspend fun parametersValidation(body: TaskDTO): Validation<TaskDTO> {
        val parameterIds = task.getAllParametersById(id = body.uuid).map {
            "$it"
        }

        return Validation {
            TaskDTO::parameters {
                uniqueItems(unique = true)
            }
            TaskDTO::parameters onEach {
                ParameterDTO::uuid {
                    uuid()
                    oneOf(items = parameterIds)
                }
                ParameterDTO::type {
                    enum<ParameterType>()
                }
                ParameterDTO::description {
                    maxLength(length = PARAMETER_DESCRIPTION_MAX_LENGTH)
                }
                ParameterDTO::example {
                    minLength(length = PARAMETER_EXAMPLE_MIN_LENGTH)
                    maxLength(length = PARAMETER_EXAMPLE_MAX_LENGTH)
                }
            }
        }
    }

    private suspend fun responsesValidation(body: TaskDTO): Validation<TaskDTO> {
        val responseIds = task.getAllResponsesById(id = body.uuid).map {
            "$it"
        }

        return Validation {
            TaskDTO::responses {
                uniqueItems(unique = true)
            }
            TaskDTO::responses onEach {
                ResponseDTO::uuid {
                    uuid()
                    oneOf(items = responseIds)
                }
                ResponseDTO::httpStatusCode {
                    httpStatusCode()
                }
                ResponseDTO::contentType {
                    contentType()
                }
                ResponseDTO::description {
                    minLength(length = RESPONSE_DESCRIPTION_MIN_LENGTH)
                    maxLength(length = RESPONSE_DESCRIPTION_MAX_LENGTH)
                }
                ResponseDTO::example {
                    json()
                    notConst(notExpected = RESPONSE_EXAMPLE_EMPTY_JSON_OBJECT)
                    notConst(notExpected = RESPONSE_EXAMPLE_EMPTY_JSON_ARRAY)
                }
            }
        }
    }

    companion object {
        const val TASK_TITLE_MIN_LENGTH = 6
        const val TASK_TITLE_MAX_LENGTH = 50
        const val TASK_DESCRIPTION_MAX_LENGTH = 2_000

        const val PARAMETER_DESCRIPTION_MAX_LENGTH = 100
        const val PARAMETER_EXAMPLE_MIN_LENGTH = 1
        const val PARAMETER_EXAMPLE_MAX_LENGTH = 50

        const val RESPONSE_DESCRIPTION_MIN_LENGTH = 1
        const val RESPONSE_DESCRIPTION_MAX_LENGTH = 100
        const val RESPONSE_EXAMPLE_EMPTY_JSON_OBJECT = "{}"
        const val RESPONSE_EXAMPLE_EMPTY_JSON_ARRAY = "[]"
    }
}
