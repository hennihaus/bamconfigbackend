package de.hennihaus.objectmothers

import de.hennihaus.models.CreditConfiguration
import de.hennihaus.models.RatingLevel

object CreditConfigurationObjectMother {

    private const val DEFAULT_MIN_AMOUNT_IN_EUROS = 10_000
    private const val DEFAULT_MAX_AMOUNT_IN_EUROS = 50_000
    private const val DEFAULT_MIN_TERM_IN_MONTHS = 6
    private const val DEFAULT_MAX_TERM_IN_MONTHS = 36
    private val DEFAULT_MIN_SCHUFA_RATING = RatingLevel.A
    private val DEFAULT_MAX_SCHUFA_RATING = RatingLevel.P

    fun getCreditConfigurationWithNoEmptyFields(
        minAmountInEuros: Int = DEFAULT_MIN_AMOUNT_IN_EUROS,
        maxAmountInEuros: Int = DEFAULT_MAX_AMOUNT_IN_EUROS,
        minTermInMonths: Int = DEFAULT_MIN_TERM_IN_MONTHS,
        maxTermInMonths: Int = DEFAULT_MAX_TERM_IN_MONTHS,
        minSchufaRating: RatingLevel = DEFAULT_MIN_SCHUFA_RATING,
        maxSchufaRating: RatingLevel = DEFAULT_MAX_SCHUFA_RATING
    ) = CreditConfiguration(
        minAmountInEuros = minAmountInEuros,
        maxAmountInEuros = maxAmountInEuros,
        minTermInMonths = minTermInMonths,
        maxTermInMonths = maxTermInMonths,
        minSchufaRating = minSchufaRating,
        maxSchufaRating = maxSchufaRating
    )
}
