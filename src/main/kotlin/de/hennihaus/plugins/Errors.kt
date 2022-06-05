package de.hennihaus.plugins

import de.hennihaus.plugins.ErrorMessage.BROKER_EXCEPTION_DEFAULT_MESSAGE
import de.hennihaus.plugins.ErrorMessage.MISSING_PROPERTY_MESSAGE
import de.hennihaus.plugins.ErrorMessage.NOT_FOUND_MESSAGE
import de.hennihaus.plugins.ErrorMessage.OBJECT_ID_EXCEPTION_DEFAULT_MESSAGE
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.NotFoundException
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
                        error = throwable.message ?: NOT_FOUND_MESSAGE
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

object ErrorMessage {
    const val OBJECT_ID_EXCEPTION_DEFAULT_MESSAGE = "ObjectId could not be parsed due to an invalid format"
    const val BROKER_EXCEPTION_DEFAULT_MESSAGE = "Error while calling ActiveMQ"
    const val MISSING_PROPERTY_MESSAGE = "Missing property"
    const val NOT_FOUND_MESSAGE = "Resource not found"
}

class ObjectIdException(override val message: String = OBJECT_ID_EXCEPTION_DEFAULT_MESSAGE) : RuntimeException()

class BrokerException(override val message: String?) : RuntimeException(message ?: BROKER_EXCEPTION_DEFAULT_MESSAGE)

class PropertyNotFoundException(key: String) : IllegalStateException("$MISSING_PROPERTY_MESSAGE $key")

@Serializable
data class ExceptionResponse(
    val code: Int,
    val error: String
)
