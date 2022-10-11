package de.hennihaus.plugins

import de.hennihaus.models.generated.rest.ErrorsDTO
import de.hennihaus.models.generated.rest.ReasonDTO
import de.hennihaus.plugins.ErrorMessage.ANONYMOUS_OBJECT
import de.hennihaus.plugins.ErrorMessage.BROKER_EXCEPTION_MESSAGE
import de.hennihaus.plugins.ErrorMessage.EXPOSED_TRANSACTION_EXCEPTION
import de.hennihaus.plugins.ErrorMessage.MISSING_PROPERTY_MESSAGE
import de.hennihaus.plugins.ErrorMessage.UUID_EXCEPTION_MESSAGE
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import java.time.ZonedDateTime

fun Application.configureErrorHandling() {
    install(plugin = StatusPages) {
        exception<Throwable> { call, throwable ->
            val dateTime = ZonedDateTime.now()

            when (throwable) {
                is UUIDException -> call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = throwable.toErrorsDTO(dateTime = dateTime),
                )
                is RequestValidationException -> call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = throwable.toErrorsDTO(dateTime = dateTime),
                )
                is BadRequestException -> call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = throwable.toErrorsDTO(dateTime),
                )
                is NotFoundException -> call.respond(
                    status = HttpStatusCode.NotFound,
                    message = throwable.toErrorsDTO(dateTime = dateTime),
                )
                is TransactionException -> call.respond(
                    status = HttpStatusCode.Conflict,
                    message = throwable.toErrorsDTO(dateTime = dateTime),
                )
                else -> call.also { it.application.environment.log.error("Internal Server Error: ", throwable) }
                    .respond(
                        status = HttpStatusCode.InternalServerError,
                        message = throwable.toErrorsDTO(dateTime = dateTime),
                    )
            }
        }
    }
}

private fun Throwable.toErrorsDTO(dateTime: ZonedDateTime) = ErrorsDTO(
    reasons = listOf(
        ReasonDTO(
            exception = this::class.simpleName ?: ANONYMOUS_OBJECT,
            message = "$message",
        ),
    ),
    dateTime = "$dateTime",
)

private fun RequestValidationException.toErrorsDTO(dateTime: ZonedDateTime) = ErrorsDTO(
    reasons = this.reasons.map {
        ReasonDTO(
            exception = this::class.simpleName ?: ANONYMOUS_OBJECT,
            message = it,
        )
    },
    dateTime = "$dateTime",
)

object ErrorMessage {
    const val ANONYMOUS_OBJECT = "Anonymous Object"

    const val UUID_EXCEPTION_MESSAGE = "uuid must have valid uuid format"
    const val BROKER_EXCEPTION_MESSAGE = "error while calling ActiveMQ"
    const val EXPOSED_TRANSACTION_EXCEPTION = "exposed transaction failed"
    const val MISSING_PROPERTY_MESSAGE = "missing property"
    const val INTEGRATION_STEP_NOT_FOUND_MESSAGE = "integrationStep is not found"
}

class UUIDException(override val message: String = UUID_EXCEPTION_MESSAGE) : RuntimeException()

class BrokerException(message: String?) : RuntimeException(message ?: BROKER_EXCEPTION_MESSAGE)

class TransactionException(message: String? = null) : RuntimeException(message ?: EXPOSED_TRANSACTION_EXCEPTION)

class PropertyNotFoundException(key: String) : IllegalStateException("$MISSING_PROPERTY_MESSAGE $key")
