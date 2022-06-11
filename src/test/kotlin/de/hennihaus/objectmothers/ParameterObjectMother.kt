package de.hennihaus.objectmothers

import de.hennihaus.models.Parameter
import de.hennihaus.models.ParameterType

object ParameterObjectMother {

    fun getSocialSecurityNumberParameter(
        name: String = "socialSecurityNumber",
        type: ParameterType = ParameterType.STRING,
        description: String = "Zufällige Sozialversicherungsnummer",
        example: String = "12123456M123",
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getRatingLevelParameter(
        name: String = "ratingLevel",
        type: ParameterType = ParameterType.CHARACTER,
        description: String = "Bewertung des Debitors",
        example: String = "A",
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getDelayInMillisecondsParameter(
        name: String = "delayInMilliseconds",
        type: ParameterType = ParameterType.LONG,
        description: String = "Zeit in Millisekunden, um die die Antwort verzögert werden soll",
        example: String = "0",
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getUsernameParameter(
        name: String = "username",
        type: ParameterType = ParameterType.STRING,
        description: String = "Benutzername der Gruppe",
        example: String = "Beispielgruppe",
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getPasswordParameter(
        name: String = "password",
        type: ParameterType = ParameterType.STRING,
        description: String = "Passwort der Gruppe",
        example: String = "OOfKqWksmA",
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getAmountInEurosParameter(
        name: String = "amountInEuros",
        type: ParameterType = ParameterType.INTEGER,
        description: String = "Gewünschte Höhe des Kredites in Euro",
        example: String = "30000",
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getTermInMonthsParameter(
        name: String = "termInMonths",
        type: ParameterType = ParameterType.INTEGER,
        description: String = "Gewünschte Länge des Kredites in Monaten",
        example: String = "21",
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )

    fun getRequestIdParameter(
        name: String = "requestId",
        type: ParameterType = ParameterType.STRING,
        description: String = "Zufällige Request-ID",
        example: String = "62a47e4d6230501f22e7c28f",
    ) = Parameter(
        name = name,
        type = type,
        description = description,
        example = example,
    )
}
