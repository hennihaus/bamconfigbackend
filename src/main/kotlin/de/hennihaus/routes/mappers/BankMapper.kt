package de.hennihaus.routes.mappers

import de.hennihaus.bamdatamodel.Bank
import de.hennihaus.bamdatamodel.CreditConfiguration
import de.hennihaus.bamdatamodel.RatingLevel
import de.hennihaus.models.generated.rest.BankDTO
import de.hennihaus.models.generated.rest.CreditConfigurationDTO
import java.net.URI
import java.util.UUID

fun Bank.toBankDTO() = BankDTO(
    uuid = "$uuid",
    name = name,
    jmsQueue = jmsQueue,
    thumbnailUrl = "$thumbnailUrl",
    isAsync = isAsync,
    isActive = isActive,
    creditConfiguration = creditConfiguration?.toCreditConfigurationDTO(),
    teamsCount = teamsCount,
)

fun BankDTO.toBank() = Bank(
    uuid = UUID.fromString(uuid),
    name = name,
    jmsQueue = jmsQueue,
    thumbnailUrl = URI(thumbnailUrl),
    isAsync = isAsync,
    isActive = isActive,
    creditConfiguration = creditConfiguration?.toCreditConfiguration(),
    teamsCount = teamsCount,
)

private fun CreditConfiguration.toCreditConfigurationDTO() = CreditConfigurationDTO(
    minAmountInEuros = minAmountInEuros,
    maxAmountInEuros = maxAmountInEuros,
    minTermInMonths = minTermInMonths,
    maxTermInMonths = maxTermInMonths,
    minSchufaRating = minSchufaRating.name,
    maxSchufaRating = maxSchufaRating.name,
)

private fun CreditConfigurationDTO.toCreditConfiguration() = CreditConfiguration(
    minAmountInEuros = minAmountInEuros,
    maxAmountInEuros = maxAmountInEuros,
    minTermInMonths = minTermInMonths,
    maxTermInMonths = maxTermInMonths,
    minSchufaRating = RatingLevel.valueOf(value = minSchufaRating),
    maxSchufaRating = RatingLevel.valueOf(value = maxSchufaRating),
)
