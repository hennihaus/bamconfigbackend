package de.hennihaus.routes.validations

import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.models.generated.rest.StudentDTO
import de.hennihaus.models.generated.rest.TeamDTO
import de.hennihaus.models.generated.rest.TeamQueryDTO
import de.hennihaus.routes.validations.BankValidationService.Companion.BANK_NAME_MAX_LENGTH
import de.hennihaus.routes.validations.BankValidationService.Companion.BANK_NAME_MIN_LENGTH
import de.hennihaus.routes.validations.ValidationService.Companion.JMS_QUEUE_MAX_LENGTH
import de.hennihaus.routes.validations.ValidationService.Companion.JMS_QUEUE_MIN_LENGTH
import de.hennihaus.routes.validations.ValidationService.Companion.LIMIT_MAXIMUM
import de.hennihaus.routes.validations.ValidationService.Companion.LIMIT_MINIMUM
import de.hennihaus.routes.validations.ValidationService.Companion.NAME_MAX_LENGTH
import de.hennihaus.routes.validations.ValidationService.Companion.NAME_MIN_LENGTH
import de.hennihaus.services.BankService
import de.hennihaus.services.TeamService
import de.hennihaus.utils.validations.localDateTime
import de.hennihaus.utils.validations.oneOf
import de.hennihaus.utils.validations.unique
import de.hennihaus.utils.validations.uuid
import io.konform.validation.Validation
import io.konform.validation.jsonschema.enum
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.maximum
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.minimum
import io.konform.validation.jsonschema.uniqueItems
import io.konform.validation.onEach
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.koin.core.annotation.Single

@Single
class TeamValidationService(
    private val team: TeamService,
    private val bank: BankService,
) : ValidationService<TeamDTO, TeamQueryDTO> {

    override suspend fun bodyValidation(body: TeamDTO): Validation<TeamDTO> = coroutineScope {
        val asyncValidations = listOf(
            async { typeValidation(body = body) },
            async { usernameValidation(body = body) },
            async { passwordValidation(body = body) },
            async { jmsQueueValidation(body = body) },
            async { statisticsValidation(body = body) },
        )
        val syncValidations = listOf(
            studentsValidation(),
        )
        val validations = asyncValidations.awaitAll() + syncValidations

        Validation {
            TeamDTO::uuid {
                uuid()
            }
            TeamDTO::createdAt {
                localDateTime()
            }
            TeamDTO::updatedAt {
                localDateTime()
            }
            validations.forEach {
                run(validation = it)
            }
        }
    }

    override suspend fun urlValidation(query: TeamQueryDTO): Validation<TeamQueryDTO> = coroutineScope {
        val asyncValidations = listOf(
            async { banksValidation(query = query) },
        )
        val validations = asyncValidations.awaitAll()

        Validation {
            TeamQueryDTO::limit {
                minimum(minimumInclusive = LIMIT_MINIMUM)
                maximum(maximumInclusive = LIMIT_MAXIMUM)
            }
            TeamQueryDTO::type ifPresent {
                enum<TeamType>()
            }
            TeamQueryDTO::password ifPresent {
                minLength(length = TEAM_PASSWORD_MIN_LENGTH)
                maxLength(length = TEAM_PASSWORD_MAX_LENGTH)
            }
            TeamQueryDTO::minRequests ifPresent {
                minimum(minimumInclusive = TEAM_MIN_REQUESTS)
            }
            TeamQueryDTO::maxRequests ifPresent {
                minimum(
                    minimumInclusive = if (query.minRequests is Long && query.minRequests >= TEAM_MIN_REQUESTS) {
                        query.minRequests
                    } else {
                        TEAM_MIN_REQUESTS
                    },
                )
            }
            validations.forEach {
                run(validation = it)
            }
        }
    }

    private suspend fun typeValidation(body: TeamDTO): Validation<TeamDTO> {
        if (body.type == TeamType.EXAMPLE.name) {
            val isTypeUnique = team.isTypeUnique(
                id = body.uuid,
                type = TeamType.valueOf(
                    value = body.type,
                ),
            )
            return Validation {
                TeamDTO::type {
                    unique(isUnique = isTypeUnique)
                }
            }
        } else {
            return Validation {
                TeamDTO::type {
                    enum<TeamType>()
                }
            }
        }
    }

    private suspend fun usernameValidation(body: TeamDTO): Validation<TeamDTO> {
        val isUsernameUnique = team.isUsernameUnique(
            id = body.uuid,
            username = body.username,
        )

        return Validation {
            TeamDTO::username {
                minLength(length = TEAM_USERNAME_MIN_LENGTH)
                maxLength(length = TEAM_USERNAME_MAX_LENGTH)
                unique(isUnique = isUsernameUnique)
            }
        }
    }

    private suspend fun passwordValidation(body: TeamDTO): Validation<TeamDTO> {
        val isPasswordUnique = team.isPasswordUnique(
            id = body.uuid,
            password = body.password,
        )

        return Validation {
            TeamDTO::password {
                minLength(length = TEAM_PASSWORD_MIN_LENGTH)
                maxLength(length = TEAM_PASSWORD_MAX_LENGTH)
                unique(isUnique = isPasswordUnique)
            }
        }
    }

    private suspend fun jmsQueueValidation(body: TeamDTO): Validation<TeamDTO> {
        val isJmsQueueUnique = team.isJmsQueueUnique(
            id = body.uuid,
            jmsQueue = body.jmsQueue,
        )

        return Validation {
            TeamDTO::jmsQueue {
                minLength(length = JMS_QUEUE_MIN_LENGTH)
                maxLength(length = JMS_QUEUE_MAX_LENGTH)
                unique(isUnique = isJmsQueueUnique)
            }
        }
    }

    private suspend fun statisticsValidation(body: TeamDTO): Validation<TeamDTO> = coroutineScope {
        val bankNameExistsRequests = body.statistics.keys.map { name ->
            async {
                name to bank.hasName(name = name)
            }
        }
        val bankNamesExists = bankNameExistsRequests.awaitAll().filter { (_, exists) -> exists }.map { it.first }

        Validation {
            TeamDTO::statistics onEach {
                Map.Entry<String, Long>::key {
                    oneOf(items = bankNamesExists)
                }
            }
        }
    }

    private fun studentsValidation(): Validation<TeamDTO> = Validation {
        TeamDTO::students {
            uniqueItems(unique = true)
        }
        TeamDTO::students onEach {
            StudentDTO::uuid {
                uuid()
            }
            StudentDTO::firstname {
                minLength(length = NAME_MIN_LENGTH)
                maxLength(length = NAME_MAX_LENGTH)
            }
            StudentDTO::lastname {
                minLength(length = NAME_MIN_LENGTH)
                maxLength(length = NAME_MAX_LENGTH)
            }
        }
    }

    private suspend fun banksValidation(query: TeamQueryDTO): Validation<TeamQueryDTO> = coroutineScope {
        val bankNameExistsRequests = query.banks?.map { name ->
            async {
                name to bank.hasName(name = name)
            }
        }
        val bankNamesExists = bankNameExistsRequests?.awaitAll()
            ?.filter { (_, exists) -> exists }
            ?.map { it.first }
            ?: emptyList()

        Validation {
            TeamQueryDTO::banks ifPresent {
                uniqueItems(unique = true)
                onEach {
                    minLength(length = BANK_NAME_MIN_LENGTH)
                    maxLength(length = BANK_NAME_MAX_LENGTH)
                    oneOf(items = bankNamesExists)
                }
            }
        }
    }

    companion object {
        const val TEAM_USERNAME_MIN_LENGTH = 6
        const val TEAM_USERNAME_MAX_LENGTH = 50
        const val TEAM_PASSWORD_MIN_LENGTH = 10
        const val TEAM_PASSWORD_MAX_LENGTH = 50
        const val TEAM_MIN_REQUESTS = 0L
    }
}
