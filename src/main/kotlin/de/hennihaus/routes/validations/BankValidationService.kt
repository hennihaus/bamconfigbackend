package de.hennihaus.routes.validations

import de.hennihaus.bamdatamodel.RatingLevel
import de.hennihaus.models.cursors.Query
import de.hennihaus.models.generated.rest.BankDTO
import de.hennihaus.models.generated.rest.CreditConfigurationDTO
import de.hennihaus.utils.validations.url
import de.hennihaus.utils.validations.uuid
import io.konform.validation.Constraint
import io.konform.validation.Validation
import io.konform.validation.ValidationBuilder
import io.konform.validation.jsonschema.const
import io.konform.validation.jsonschema.enum
import io.konform.validation.jsonschema.minimum
import kotlinx.coroutines.coroutineScope
import org.koin.core.annotation.Single

@Single
class BankValidationService : ValidationService<BankDTO, Query> {

    override suspend fun bodyValidation(body: BankDTO): Validation<BankDTO> = coroutineScope {
        val validations = listOf(
            isActiveValidation(body = body),
            creditConfigurationValidation(body = body),
        )

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

    companion object {
        const val BANK_MUST_BE_ACTIVE = true

        const val BANK_NAME_MIN_LENGTH = 6
        const val BANK_NAME_MAX_LENGTH = 50

        const val CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS = 0
        const val CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS = 0
    }
}
