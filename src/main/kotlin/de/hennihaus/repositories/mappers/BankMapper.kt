package de.hennihaus.repositories.mappers

import de.hennihaus.bamdatamodel.Bank
import de.hennihaus.bamdatamodel.CreditConfiguration
import de.hennihaus.bamdatamodel.RatingLevel
import de.hennihaus.repositories.entities.BankEntity
import de.hennihaus.repositories.entities.CreditConfigurationEntity
import java.net.URI

fun BankEntity.toBank() = Bank(
    uuid = id.value,
    name = name,
    jmsQueue = jmsQueue,
    thumbnailUrl = URI(thumbnailUrl),
    isAsync = isAsync,
    isActive = isActive,
    creditConfiguration = creditConfiguration?.toCreditConfiguration(),
    teamsCount = statistics.count(),
)

private fun CreditConfigurationEntity.toCreditConfiguration() = CreditConfiguration(
    minAmountInEuros = minAmountInEuros,
    maxAmountInEuros = maxAmountInEuros,
    minTermInMonths = minTermInMonths,
    maxTermInMonths = maxTermInMonths,
    minSchufaRating = RatingLevel.valueOf(value = minSchufaRating),
    maxSchufaRating = RatingLevel.valueOf(value = maxSchufaRating),
)
