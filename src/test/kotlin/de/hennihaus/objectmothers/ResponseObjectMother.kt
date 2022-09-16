package de.hennihaus.objectmothers

import de.hennihaus.models.generated.Response
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import java.util.UUID

object ResponseObjectMother {

    const val OK_UUID = "d95fe79a-ee08-4481-86d2-6b95f0b96f99"
    const val OK_CODE = "200"
    const val SCHUFA_OK_DESCRIPTION = "Schufa-Score des Debitors"
    const val SCHUFA_OK_EXAMPLE = "{\"score\": 9858, \"failureRiskInPercent\": 0.77}"
    const val BANK_OK_DESCRIPTION = "Kreditzins in Prozent"
    const val BANK_OK_EXAMPLE = "{\"lendingRateInPercent\": 3.8}"

    const val BAD_REQUEST_UUID = "a4133bba-824a-4063-abdf-29399ccf685f"
    const val BAD_REQUEST_CODE = "400"
    const val BAD_REQUEST_DESCRIPTION = "Ungültige Parameter"
    const val BAD_REQUEST_EXAMPLE = "{\"message\": \"[username is required, password is required]\", \"dateTime\": \"2022-01-31T07:43:30\"}"

    const val NOT_FOUND_UUID = "7736d7b7-9f55-4a2c-8e6a-7f7fb1bf6ce5"
    const val NOT_FOUND_CODE = "404"
    const val NOT_FOUND_DESCRIPTION = "Team wurde nicht gefunden"
    const val NOT_FOUND_EXAMPLE = "{\"message\": \"[team not found by username and password]\", \"dateTime\": \"2022-01-31T07:43:30\"}"

    const val INTERNAL_SERVER_ERROR_UUID = "52c2b022-7b0b-4017-baba-3dae02f938b0"
    const val INTERNAL_SERVER_ERROR_CODE = "500"
    const val INTERNAL_SERVER_ERROR_DESCRIPTION = "Interner Server Fehler"
    const val INTERNAL_SERVER_ERROR_EXAMPLE = "{\"message\": \"[kotlin.Exception: Internal server error]\", \"dateTime\": \"2022-01-31T07:43:30\"}"

    const val JMS_UUID = "45d2b3fd-f87f-4dde-a8cd-36c02612cd50"
    const val JMS_CODE = "-1"
    const val JMS_DESCRIPTION = "Kreditzins in Prozent"
    const val JMS_EXAMPLE = "{\"requestId\": \"123\", \"lendingRateInPercent\": 2.738343644690228}"

    fun getSchufaOkResponse(
        uuid: UUID = UUID.fromString(OK_UUID),
        httpStatusCode: HttpStatusCode = HttpStatusCode.fromValue(value = OK_CODE.toInt()),
        contentType: ContentType = ContentType.Application.Json,
        description: String = SCHUFA_OK_DESCRIPTION,
        example: String = SCHUFA_OK_EXAMPLE,
    ) = Response(
        uuid = uuid,
        httpStatusCode = httpStatusCode,
        contentType = contentType,
        description = description,
        example = example,
    )

    fun getBankOkResponse(
        uuid: UUID = UUID.fromString(OK_UUID),
        httpStatusCode: HttpStatusCode = HttpStatusCode.fromValue(value = OK_CODE.toInt()),
        contentType: ContentType = ContentType.Application.Json,
        description: String = BANK_OK_DESCRIPTION,
        example: String = BANK_OK_EXAMPLE,
    ) = Response(
        uuid = uuid,
        httpStatusCode = httpStatusCode,
        contentType = contentType,
        description = description,
        example = example,
    )

    fun getBadRequestResponse(
        uuid: UUID = UUID.fromString(BAD_REQUEST_UUID),
        httpStatusCode: HttpStatusCode = HttpStatusCode.fromValue(value = BAD_REQUEST_CODE.toInt()),
        contentType: ContentType = ContentType.Application.Json,
        description: String = BAD_REQUEST_DESCRIPTION,
        example: String = BAD_REQUEST_EXAMPLE,
    ) = Response(
        uuid = uuid,
        httpStatusCode = httpStatusCode,
        contentType = contentType,
        description = description,
        example = example,
    )

    fun getNotFoundResponse(
        uuid: UUID = UUID.fromString(NOT_FOUND_UUID),
        httpStatusCode: HttpStatusCode = HttpStatusCode.fromValue(value = NOT_FOUND_CODE.toInt()),
        contentType: ContentType = ContentType.Application.Json,
        description: String = NOT_FOUND_DESCRIPTION,
        example: String = NOT_FOUND_EXAMPLE,
    ) = Response(
        uuid = uuid,
        httpStatusCode = httpStatusCode,
        contentType = contentType,
        description = description,
        example = example,
    )

    fun getInternalServerErrorResponse(
        uuid: UUID = UUID.fromString(INTERNAL_SERVER_ERROR_UUID),
        httpStatusCode: HttpStatusCode = HttpStatusCode.fromValue(value = INTERNAL_SERVER_ERROR_CODE.toInt()),
        contentType: ContentType = ContentType.Application.Json,
        description: String = INTERNAL_SERVER_ERROR_DESCRIPTION,
        example: String = INTERNAL_SERVER_ERROR_EXAMPLE,
    ) = Response(
        uuid = uuid,
        httpStatusCode = httpStatusCode,
        contentType = contentType,
        description = description,
        example = example,
    )

    fun getJmsResponse(
        uuid: UUID = UUID.fromString(JMS_UUID),
        httpStatusCode: HttpStatusCode = HttpStatusCode.fromValue(value = JMS_CODE.toInt()),
        contentType: ContentType = ContentType.Application.Json,
        description: String = JMS_DESCRIPTION,
        example: String = JMS_EXAMPLE,
    ) = Response(
        uuid = uuid,
        httpStatusCode = httpStatusCode,
        contentType = contentType,
        description = description,
        example = example,
    )
}
