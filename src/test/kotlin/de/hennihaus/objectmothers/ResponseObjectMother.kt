package de.hennihaus.objectmothers

import de.hennihaus.models.Response
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode

object ResponseObjectMother {

    const val OK_CODE = "200"
    const val SCHUFA_OK_DESCRIPTION = "Schufa-Score des Debitors"
    const val SCHUFA_OK_EXAMPLE = "{ \"score\": 9858, \"failureRiskInPercent\": 0.77 }"
    const val BANK_OK_DESCRIPTION = "Kreditzins in Prozent"
    const val BANK_OK_EXAMPLE = "{ \"lendingRateInPercent\": 3.8 }"

    const val BAD_REQUEST_CODE = "400"
    const val BAD_REQUEST_DESCRIPTION = "Ungültige Parameter"
    @Suppress("MaxLineLength")
    const val BAD_REQUEST_EXAMPLE = "{ \"message\": \"[username is required, password is required]\", \"dateTime\": \"2022-01-31T07:43:30\" }"

    const val NOT_FOUND_CODE = "404"
    const val NOT_FOUND_DESCRIPTION = "Gruppe wurde nicht gefunden"
    @Suppress("MaxLineLength")
    const val NOT_FOUND_EXAMPLE = "{ \"message\": \"[group not found by username and password]\", \"dateTime\": \"2022-01-31T07:43:30\" }"

    const val INTERNAL_SERVER_ERROR_CODE = "500"
    const val INTERNAL_SERVER_ERROR_DESCRIPTION = "Interner Server Fehler"
    @Suppress("MaxLineLength")
    const val INTERNAL_SERVER_ERROR_EXAMPLE = "{ \"message\": \"[kotlin.Exception: Internal server error]\", \"dateTime\": \"2022-01-31T07:43:30\" }"

    const val JMS_CODE = "-1"
    const val JMS_DESCRIPTION = "Kreditzins in Prozent"
    const val JMS_EXAMPLE = "{ \"requestId\": \"123\", \"lendingRateInPercent\": 2.738343644690228 }"

    fun getSchufaOkResponse(
        code: HttpStatusCode = HttpStatusCode.fromValue(value = OK_CODE.toInt()),
        contentType: ContentType = ContentType.Application.Json,
        description: String = SCHUFA_OK_DESCRIPTION,
        example: String = SCHUFA_OK_EXAMPLE,
    ) = Response(
        code = code,
        contentType = contentType,
        description = description,
        example = example,
    )

    fun getBankOkResponse(
        code: HttpStatusCode = HttpStatusCode.fromValue(value = OK_CODE.toInt()),
        contentType: ContentType = ContentType.Application.Json,
        description: String = BANK_OK_DESCRIPTION,
        example: String = BANK_OK_EXAMPLE,
    ) = Response(
        code = code,
        contentType = contentType,
        description = description,
        example = example,
    )

    fun getBadRequestResponse(
        code: HttpStatusCode = HttpStatusCode.fromValue(value = BAD_REQUEST_CODE.toInt()),
        contentType: ContentType = ContentType.Application.Json,
        description: String = BAD_REQUEST_DESCRIPTION,
        example: String = BAD_REQUEST_EXAMPLE,
    ) = Response(
        code = code,
        contentType = contentType,
        description = description,
        example = example,
    )

    fun getNotFoundResponse(
        code: HttpStatusCode = HttpStatusCode.fromValue(value = NOT_FOUND_CODE.toInt()),
        contentType: ContentType = ContentType.Application.Json,
        description: String = NOT_FOUND_DESCRIPTION,
        example: String = NOT_FOUND_EXAMPLE,
    ) = Response(
        code = code,
        contentType = contentType,
        description = description,
        example = example,
    )

    fun getInternalServerErrorResponse(
        code: HttpStatusCode = HttpStatusCode.fromValue(value = INTERNAL_SERVER_ERROR_CODE.toInt()),
        contentType: ContentType = ContentType.Application.Json,
        description: String = INTERNAL_SERVER_ERROR_DESCRIPTION,
        example: String = INTERNAL_SERVER_ERROR_EXAMPLE,
    ) = Response(
        code = code,
        contentType = contentType,
        description = description,
        example = example,
    )

    fun getJmsResponse(
        code: HttpStatusCode = HttpStatusCode.fromValue(value = JMS_CODE.toInt()),
        contentType: ContentType = ContentType.Application.Json,
        description: String = JMS_DESCRIPTION,
        example: String = JMS_EXAMPLE,
    ) = Response(
        code = code,
        contentType = contentType,
        description = description,
        example = example,
    )
}
