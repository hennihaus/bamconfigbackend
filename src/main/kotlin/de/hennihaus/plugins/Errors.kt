package de.hennihaus.plugins

import de.hennihaus.models.generated.rest.ErrorsDTO
import de.hennihaus.models.generated.rest.ReasonDTO
import de.hennihaus.plugins.ErrorMessage.ANONYMOUS_OBJECT
import de.hennihaus.plugins.ErrorMessage.BROKER_EXCEPTION_MESSAGE
import de.hennihaus.plugins.ErrorMessage.EXPOSED_TRANSACTION_EXCEPTION
import de.hennihaus.plugins.ErrorMessage.UUID_EXCEPTION_MESSAGE
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import java.io.Serial
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

fun Application.configureErrorHandling() = install(plugin = StatusPages) {
    exception<Throwable> { call, throwable ->
        val dateTime = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS)

        when (throwable) {
            is UUIDException -> call.respond(
                status = HttpStatusCode.BadRequest,
                message = throwable.toErrorsDTO(dateTime = dateTime),
            )
            is RequestValidationException -> call.respond(
                status = HttpStatusCode.BadRequest,
                message = throwable.toErrorsDTO(dateTime = dateTime),
            )
            is MissingRequestParameterException -> call.respond(
                status = HttpStatusCode.BadRequest,
                message = throwable.toErrorsDTO(dateTime = dateTime),
            )
            is BadRequestException -> call.respond(
                status = HttpStatusCode.BadRequest,
                message = throwable.toErrorsDTO(dateTime = dateTime),
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

private fun Throwable.toErrorsDTO(dateTime: ZonedDateTime) = ErrorsDTO(
    reasons = listOf(
        ReasonDTO(
            exception = this::class.simpleName ?: ANONYMOUS_OBJECT,
            message = "$message".replaceFirstChar { it.lowercase() },
        ),
    ),
    dateTime = "$dateTime",
)

private fun RequestValidationException.toErrorsDTO(dateTime: ZonedDateTime) = ErrorsDTO(
    reasons = this.reasons.map { reason ->
        ReasonDTO(
            exception = this::class.simpleName ?: ANONYMOUS_OBJECT,
            message = reason.replaceFirstChar { it.lowercase() },
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

class UUIDException(override val message: String = UUID_EXCEPTION_MESSAGE) : RuntimeException() {
    companion object {
        @Serial
        private const val serialVersionUID: Long = -1318755554914629714L
    }
}

class BrokerException(message: String?) : RuntimeException(message ?: BROKER_EXCEPTION_MESSAGE) {
    companion object {
        @Serial
        private const val serialVersionUID: Long = -3796942227686013324L
    }
}

class TransactionException(message: String? = null) : RuntimeException(message ?: EXPOSED_TRANSACTION_EXCEPTION) {
    companion object {
        @Serial
        private const val serialVersionUID: Long = 291194568384862334L
    }
}
