package de.hennihaus.routes.validations

import de.hennihaus.bamdatamodel.RatingLevel
import de.hennihaus.models.generated.rest.BankDTO
import de.hennihaus.models.generated.rest.CreditConfigurationDTO
import de.hennihaus.models.generated.rest.TeamDTO
import de.hennihaus.services.TeamService
import de.hennihaus.utils.validations.containsAll
import de.hennihaus.utils.validations.containsToMany
import de.hennihaus.utils.validations.oneOf
import de.hennihaus.utils.validations.url
import de.hennihaus.utils.validations.uuid
import io.konform.validation.Constraint
import io.konform.validation.Validation
import io.konform.validation.ValidationBuilder
import io.konform.validation.jsonschema.const
import io.konform.validation.jsonschema.enum
import io.konform.validation.jsonschema.minimum
import io.konform.validation.jsonschema.uniqueItems
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class BankValidationService(private val team: TeamService) : ValidationService<BankDTO> {

    override suspend fun bodyValidation(body: BankDTO): Validation<BankDTO> = coroutineScope {
        val asyncValidations = listOf(
            async { teamsValidation(body = body) },
        )
        val syncValidations = listOf(
            isActiveValidation(body = body),
            creditConfigurationValidation(body = body),
        )
        val validations = asyncValidations.awaitAll() + syncValidations

        Validation {
            BankDTO::uuid {
                uuid()
            }
            BankDTO::thumbnailUrl {
                url()
            }
            validations.forEach {
                run(validation = it)
            }
        }
    }

    private suspend fun teamsValidation(body: BankDTO): Validation<BankDTO> {
        val teamIds = team.getAllTeamIds().map {
            "$it"
        }

        return Validation {
            BankDTO::teams {
                uniqueItems(unique = true)
            }
            BankDTO::teams onEach {
                TeamDTO::uuid {
                    uuid()
                }
            }
            if (body.isAsync) {
                BankDTO::teams onEach {
                    TeamDTO::uuid {
                        oneOf(items = teamIds)
                    }
                }
            } else {
                BankDTO::teams {
                    containsAll(
                        items = body.toValidTeamUUIDs(),
                        expectedItems = teamIds,
                        fieldName = TEAM_UUID_FIELD,
                    )
                    containsToMany(
                        items = body.toValidTeamUUIDs(),
                        expectedItems = teamIds,
                        fieldName = TEAM_UUID_FIELD,
                    )
                }
            }
        }
    }

    private fun isActiveValidation(body: BankDTO): Validation<BankDTO> = Validation {
        if (body.isAsync.not()) {
            BankDTO::isActive {
                const(expected = BANK_MUST_BE_ACTIVE)
            }
        }
    }

    private fun creditConfigurationValidation(body: BankDTO): Validation<BankDTO> = Validation {
        if (body.isAsync) {
            BankDTO::creditConfiguration required {
                body.creditConfiguration?.let {
                    run(
                        validation = creditConfigurationValidation(
                            body = it,
                        ),
                    )
                }
            }
        } else {
            BankDTO::creditConfiguration ifPresent {
                body.creditConfiguration?.let {
                    run(
                        validation = creditConfigurationValidation(
                            body = it,
                        ),
                    )
                }
            }
        }
    }

    private fun creditConfigurationValidation(body: CreditConfigurationDTO) = Validation {
        CreditConfigurationDTO::minAmountInEuros {
            minimum(minimumInclusive = CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS)
        }
        CreditConfigurationDTO::maxAmountInEuros {
            minimum(
                minimumInclusive = if (body.maxAmountInEuros >= CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS) {
                    body.minAmountInEuros
                } else {
                    CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS
                },
            )
        }
        CreditConfigurationDTO::minTermInMonths {
            minimum(minimumInclusive = CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS)
        }
        CreditConfigurationDTO::maxTermInMonths {
            minimum(
                minimumInclusive = if (body.maxTermInMonths >= CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS) {
                    body.minTermInMonths
                } else {
                    CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS
                },
            )
        }
        CreditConfigurationDTO::minSchufaRating {
            enum<RatingLevel>()
        }
        CreditConfigurationDTO::maxSchufaRating {
            enum<RatingLevel>()
            minimum(minimumInclusive = body.minSchufaRating)
        }
    }

    private fun ValidationBuilder<String>.minimum(minimumInclusive: String): Constraint<String> {
        val enumNames = enumValues<RatingLevel>().map { it.name }

        return addConstraint(
            errorMessage = "must be at least '{0}'", templateValues = arrayOf(minimumInclusive)
        ) {
            it.takeIf { rating -> (rating in enumNames) and (minimumInclusive in enumNames) }
                ?.let { rating -> RatingLevel.valueOf(value = rating) >= RatingLevel.valueOf(value = minimumInclusive) }
                ?: true
        }
    }

    private fun BankDTO.toValidTeamUUIDs() = teams.map { it.uuid }.filter {
        runCatching { UUID.fromString(it) }
            .map { true }
            .getOrElse { false }
    }

    companion object {
        const val BANK_MUST_BE_ACTIVE = true

        const val CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS = 0
        const val CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS = 0

        const val TEAM_UUID_FIELD = "uuids"
    }
}
