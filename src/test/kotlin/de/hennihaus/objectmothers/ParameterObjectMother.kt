package de.hennihaus.objectmothers

import de.hennihaus.models.Parameter
import de.hennihaus.models.ParameterType
import java.util.UUID

object ParameterObjectMother {

    const val SOCIAL_SECURITY_NUMBER_UUID = "caba3d46-2e06-4678-8690-542ce08acda0"
    const val SOCIAL_SECURITY_NUMBER_PARAMETER = "socialSecurityNumber"
    const val SOCIAL_SECURITY_NUMBER_DESCRIPTION = "Zufällige Sozialversicherungsnummer"
    const val SOCIAL_SECURITY_NUMBER_EXAMPLE = "12123456M123"

    const val RATING_LEVEL_UUID = "b5beb647-26be-4e5e-bbee-b4279b9c8e90"
    const val RATING_LEVEL_PARAMETER = "ratingLevel"
    const val RATING_LEVEL_DESCRIPTION = "Bewertung des Debitors"
    const val RATING_LEVEL_EXAMPLE = "A"

    const val DELAY_IN_MILLISECONDS_UUID = "200916e1-9f68-48ba-9fdd-672f27bfe123"
    const val DELAY_IN_MILLISECONDS_PARAMETER = "delayInMilliseconds"
    const val DELAY_IN_MILLISECONDS_DESCRIPTION = "Zeit in Millisekunden, um die die Antwort verzögert werden soll"
    const val DELAY_IN_MILLISECONDS_EXAMPLE = "0"

    const val USERNAME_UUID = "58e5c73b-cad6-4b2c-8e25-635b4e5eb7c6"
    const val USERNAME_PARAMETER = "username"
    const val USERNAME_DESCRIPTION = "Benutzername des Teams"
    const val USERNAME_EXAMPLE = "Beispielteam"

    const val PASSWORD_UUID = "e17421f6-26d6-422a-9087-1ce3dcdf6a12"
    const val PASSWORD_PARAMETER = "password"
    const val PASSWORD_DESCRIPTION = "Passwort des Teams"
    const val PASSWORD_EXAMPLE = "OOfKqWksmA"

    const val AMOUNT_IN_EUROS_UUID = "8fbf56d5-ac30-42aa-aca3-24dde32c63fa"
    const val AMOUNT_IN_EUROS_PARAMETER = "amountInEuros"
    const val AMOUNT_IN_EUROS_DESCRIPTION = "Gewünschte Höhe des Kredites in Euro"
    const val AMOUNT_IN_EUROS_EXAMPLE = "30000"

    const val TERM_IN_MONTHS_UUID = "77bc374e-dcb6-4859-b356-c511a5860bdc"
    const val TERM_IN_MONTHS_PARAMETER = "termInMonths"
    const val TERM_IN_MONTHS_DESCRIPTION = "Gewünschte Länge des Kredites in Monaten"
    const val TERM_IN_MONTHS_EXAMPLE = "21"

    const val REQUEST_ID_UUID = "0ae3aa19-e421-4c5f-bb9a-d9f932689e5f"
    const val REQUEST_ID_PARAMETER = "requestId"
    const val REQUEST_ID_DESCRIPTION = "Zufällige Request-ID"
    const val REQUEST_ID_EXAMPLE = "62a47e4d6230501f22e7c28f"

    fun getSocialSecurityNumberParameter(
        uuid: UUID = UUID.fromString(SOCIAL_SECURITY_NUMBER_UUID),
        name: String = SOCIAL_SECURITY_NUMBER_PARAMETER,
        type: ParameterType = ParameterType.STRING,
        description: String = SOCIAL_SECURITY_NUMBER_DESCRIPTION,
        example: String = SOCIAL_SECURITY_NUMBER_EXAMPLE,
    ) = Parameter(
        uuid = uuid,
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getRatingLevelParameter(
        uuid: UUID = UUID.fromString(RATING_LEVEL_UUID),
        name: String = RATING_LEVEL_PARAMETER,
        type: ParameterType = ParameterType.CHARACTER,
        description: String = RATING_LEVEL_DESCRIPTION,
        example: String = RATING_LEVEL_EXAMPLE,
    ) = Parameter(
        uuid = uuid,
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getDelayInMillisecondsParameter(
        uuid: UUID = UUID.fromString(DELAY_IN_MILLISECONDS_UUID),
        name: String = DELAY_IN_MILLISECONDS_PARAMETER,
        type: ParameterType = ParameterType.LONG,
        description: String = DELAY_IN_MILLISECONDS_DESCRIPTION,
        example: String = DELAY_IN_MILLISECONDS_EXAMPLE,
    ) = Parameter(
        uuid = uuid,
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getUsernameParameter(
        uuid: UUID = UUID.fromString(USERNAME_UUID),
        name: String = USERNAME_PARAMETER,
        type: ParameterType = ParameterType.STRING,
        description: String = USERNAME_DESCRIPTION,
        example: String = USERNAME_EXAMPLE,
    ) = Parameter(
        uuid = uuid,
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getPasswordParameter(
        uuid: UUID = UUID.fromString(PASSWORD_UUID),
        name: String = PASSWORD_PARAMETER,
        type: ParameterType = ParameterType.STRING,
        description: String = PASSWORD_DESCRIPTION,
        example: String = PASSWORD_EXAMPLE,
    ) = Parameter(
        uuid = uuid,
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getAmountInEurosParameter(
        uuid: UUID = UUID.fromString(AMOUNT_IN_EUROS_UUID),
        name: String = AMOUNT_IN_EUROS_PARAMETER,
        type: ParameterType = ParameterType.INTEGER,
        description: String = AMOUNT_IN_EUROS_DESCRIPTION,
        example: String = AMOUNT_IN_EUROS_EXAMPLE,
    ) = Parameter(
        uuid = uuid,
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getTermInMonthsParameter(
        uuid: UUID = UUID.fromString(TERM_IN_MONTHS_UUID),
        name: String = TERM_IN_MONTHS_PARAMETER,
        type: ParameterType = ParameterType.INTEGER,
        description: String = TERM_IN_MONTHS_DESCRIPTION,
        example: String = TERM_IN_MONTHS_EXAMPLE,
    ) = Parameter(
        uuid = uuid,
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getRequestIdParameter(
        uuid: UUID = UUID.fromString(REQUEST_ID_UUID),
        name: String = REQUEST_ID_PARAMETER,
        type: ParameterType = ParameterType.STRING,
        description: String = REQUEST_ID_DESCRIPTION,
        example: String = REQUEST_ID_EXAMPLE,
    ) = Parameter(
        uuid = uuid,
        name = name,
        type = type,
        description = description,
        example = example,
    )
}
