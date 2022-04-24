package de.hennihaus.objectmothers

import de.hennihaus.models.Parameter
import de.hennihaus.models.ParameterType

object ParameterObjectMother {

    fun getSocialSecurityNumberParameter(
        name: String = "socialSecurityNumber",
        type: ParameterType = ParameterType.STRING,
        description: String = "Zufällige Sozialversicherungsnummer z.B. 12123456M123"
    ) = Parameter(
        name = name,
        type = type,
        description = description
    )

    fun getRatingLevelParameter(
        name: String = "ratingLevel",
        type: ParameterType = ParameterType.CHARACTER,
        description: String = "Rating zwischen A und P (Groß- und Kleinschreibung möglich), wie aus Tabelle" +
            " bei der Beschreibung des Loan Broker zu entnehmen ist"
    ) = Parameter(
        name = name,
        type = type,
        description = description
    )

    fun getDelayInMillisecondsParameter(
        name: String = "delayInMilliseconds",
        type: ParameterType = ParameterType.LONG,
        description: String = "Rating zwischen A und P (Groß- und Kleinschreibung möglich), wie aus Tabelle " +
            "bei der Beschreibung des Loan Broker zu entnehmen ist"
    ) = Parameter(
        name = name,
        type = type,
        description = description
    )

    fun getUsernameParameter(
        name: String = "username",
        type: ParameterType = ParameterType.STRING,
        description: String = "Benutzername der Gruppe"
    ) = Parameter(
        name = name,
        type = type,
        description = description
    )

    fun getPasswordParameter(
        name: String = "password",
        type: ParameterType = ParameterType.STRING,
        description: String = "Passwort der Gruppe"
    ) = Parameter(
        name = name,
        type = type,
        description = description
    )

    fun getAmountInEurosParameter(
        name: String = "amountInEuros",
        type: ParameterType = ParameterType.INTEGER,
        description: String = "Gewünschte Höhe des Kredites in Euro"
    ) = Parameter(
        name = name,
        type = type,
        description = description
    )

    fun getTermInMonthsParameter(
        name: String = "termInMonths",
        type: ParameterType = ParameterType.INTEGER,
        description: String = "Gewünschte Länge des Kredites in Monaten"
    ) = Parameter(
        name = name,
        type = type,
        description = description
    )

    fun getRequestIdParameter(
        name: String = "requestId",
        type: ParameterType = ParameterType.STRING,
        description: String = "Zufällige Request-ID"
    ) = Parameter(
        name = name,
        type = type,
        description = description
    )
}
