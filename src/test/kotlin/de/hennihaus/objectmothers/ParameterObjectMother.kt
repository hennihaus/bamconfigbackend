package de.hennihaus.objectmothers

import de.hennihaus.models.Parameter
import de.hennihaus.models.ParameterType

object ParameterObjectMother {

    const val SOCIAL_SECURITY_NUMBER_PARAMETER = "socialSecurityNumber"
    const val SOCIAL_SECURITY_NUMBER_DESCRIPTION = "Zufällige Sozialversicherungsnummer"
    const val SOCIAL_SECURITY_NUMBER_EXAMPLE = "12123456M123"

    const val RATING_LEVEL_PARAMETER = "ratingLevel"
    const val RATING_LEVEL_DESCRIPTION = "Bewertung des Debitors"
    const val RATING_LEVEL_EXAMPLE = "A"

    const val DELAY_IN_MILLISECONDS_PARAMETER = "delayInMilliseconds"
    const val DELAY_IN_MILLISECONDS_DESCRIPTION = "Zeit in Millisekunden, um die die Antwort verzögert werden soll"
    const val DELAY_IN_MILLISECONDS_EXAMPLE = "0"

    const val USERNAME_PARAMETER = "username"
    const val USERNAME_DESCRIPTION = "Benutzername der Gruppe"
    const val USERNAME_EXAMPLE = "Beispielgruppe"

    const val PASSWORD_PARAMETER = "password"
    const val PASSWORD_DESCRIPTION = "Passwort der Gruppe"
    const val PASSWORD_EXAMPLE = "OOfKqWksmA"

    const val AMOUNT_IN_EUROS_PARAMETER = "amountInEuros"
    const val AMOUNT_IN_EUROS_DESCRIPTION = "Gewünschte Höhe des Kredites in Euro"
    const val AMOUNT_IN_EUROS_EXAMPLE = "30000"

    const val TERM_IN_MONTHS_PARAMETER = "termInMonths"
    const val TERM_IN_MONTHS_DESCRIPTION = "Gewünschte Länge des Kredites in Monaten"
    const val TERM_IN_MONTHS_EXAMPLE = "21"

    const val REQUEST_ID_PARAMETER = "requestId"
    const val REQUEST_ID_DESCRIPTION = "Zufällige Request-ID"
    const val REQUEST_ID_EXAMPLE = "62a47e4d6230501f22e7c28f"

    fun getSocialSecurityNumberParameter(
        name: String = SOCIAL_SECURITY_NUMBER_PARAMETER,
        type: ParameterType = ParameterType.STRING,
        description: String = SOCIAL_SECURITY_NUMBER_DESCRIPTION,
        example: String = SOCIAL_SECURITY_NUMBER_EXAMPLE,
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getRatingLevelParameter(
        name: String = RATING_LEVEL_PARAMETER,
        type: ParameterType = ParameterType.CHARACTER,
        description: String = RATING_LEVEL_DESCRIPTION,
        example: String = RATING_LEVEL_EXAMPLE,
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getDelayInMillisecondsParameter(
        name: String = DELAY_IN_MILLISECONDS_PARAMETER,
        type: ParameterType = ParameterType.LONG,
        description: String = DELAY_IN_MILLISECONDS_DESCRIPTION,
        example: String = DELAY_IN_MILLISECONDS_EXAMPLE,
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getUsernameParameter(
        name: String = USERNAME_PARAMETER,
        type: ParameterType = ParameterType.STRING,
        description: String = USERNAME_DESCRIPTION,
        example: String = USERNAME_EXAMPLE,
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getPasswordParameter(
        name: String = PASSWORD_PARAMETER,
        type: ParameterType = ParameterType.STRING,
        description: String = PASSWORD_DESCRIPTION,
        example: String = PASSWORD_EXAMPLE,
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getAmountInEurosParameter(
        name: String = AMOUNT_IN_EUROS_PARAMETER,
        type: ParameterType = ParameterType.INTEGER,
        description: String = AMOUNT_IN_EUROS_DESCRIPTION,
        example: String = AMOUNT_IN_EUROS_EXAMPLE,
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getTermInMonthsParameter(
        name: String = TERM_IN_MONTHS_PARAMETER,
        type: ParameterType = ParameterType.INTEGER,
        description: String = TERM_IN_MONTHS_DESCRIPTION,
        example: String = TERM_IN_MONTHS_EXAMPLE,
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getRequestIdParameter(
        name: String = REQUEST_ID_PARAMETER,
        type: ParameterType = ParameterType.STRING,
        description: String = REQUEST_ID_DESCRIPTION,
        example: String = REQUEST_ID_EXAMPLE,
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )
}
