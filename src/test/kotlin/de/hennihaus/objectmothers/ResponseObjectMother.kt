package de.hennihaus.objectmothers

import de.hennihaus.models.Response
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode

object ResponseObjectMother {

    fun getOkResponse(
        code: HttpStatusCode = HttpStatusCode.OK,
        mediaType: ContentType = ContentType.Application.Json,
        description: String = "Schufa-Score des Debitors",
        example: String = "{ \"score\": 9858, \"failureRiskInPercent\": 0.77 }",
    ) = Response(
        code = code,
        mediaType = mediaType,
        description = description,
        example = example,
    )

    fun getBadRequestResponse(
        code: HttpStatusCode = HttpStatusCode.BadRequest,
        mediaType: ContentType = ContentType.Application.Json,
        description: String = "Ungültige Parameter",
        example: String = """
            { "message": "[username is required, password is required]", "dateTime": "2022-01-31T07:43:30" }
        """.trimIndent(),
    ) = Response(
        code = code,
        mediaType = mediaType,
        description = description,
        example = example,
    )

    fun getNotFoundResponse(
        code: HttpStatusCode = HttpStatusCode.NotFound,
        mediaType: ContentType = ContentType.Application.Json,
        description: String = "Gruppe wurde nicht gefunden",
        example: String = """
            { "message": "[group not found by username and password]", "dateTime": "2022-01-31T07:43:30" }
        """.trimIndent(),
    ) = Response(
        code = code,
        mediaType = mediaType,
        description = description,
        example = example,
    )

    fun getInternalServerErrorResponse(
        code: HttpStatusCode = HttpStatusCode.InternalServerError,
        mediaType: ContentType = ContentType.Application.Json,
        description: String = "Interner Server Fehler",
        example: String = "",
    ) = Response(
        code = code,
        mediaType = mediaType,
        description = description,
        example = example,
    )

    fun getJmsResponse(
        code: HttpStatusCode = HttpStatusCode.fromValue(value = -1),
        mediaType: ContentType = ContentType.Application.Json,
        description: String = "{ \"requestId\": \"123\", \"lendingRate\": 2.738343644690228 }",
        example: String = "",
    ) = Response(
        code = code,
        mediaType = mediaType,
        description = description,
        example = example,
    )
}
