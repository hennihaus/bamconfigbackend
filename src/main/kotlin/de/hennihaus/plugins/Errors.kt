package de.hennihaus.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<Throwable> { call, throwable ->
            when (throwable) {
                is NotFoundException -> call.respond(
                    status = HttpStatusCode.NotFound,
                    message = ExceptionResponse(
                        code = HttpStatusCode.NotFound.value,
                        error = throwable.message
                    )
                )
                is ObjectIdException -> call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ExceptionResponse(
                        code = HttpStatusCode.BadRequest.value,
                        error = throwable.message
                    )
                )
                else -> call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = ExceptionResponse(
                        code = HttpStatusCode.InternalServerError.value,
                        error = throwable.message.toString()
                    )
                )
            }
        }
    }
}

class NotFoundException(override val message: String) : RuntimeException()

class ObjectIdException(
    override val message: String = "ObjectId could not be parsed due to an invalid formar"
) : RuntimeException()

@Serializable
data class ExceptionResponse(
    val code: Int,
    val error: String
)
