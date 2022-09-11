package de.hennihaus.plugins

import de.hennihaus.models.generated.ErrorResponse
import de.hennihaus.plugins.ErrorMessage.BROKER_EXCEPTION_MESSAGE
import de.hennihaus.plugins.ErrorMessage.EXPOSED_TRANSACTION_EXCEPTION
import de.hennihaus.plugins.ErrorMessage.MISSING_PROPERTY_MESSAGE
import de.hennihaus.plugins.ErrorMessage.UUID_EXCEPTION_MESSAGE
import de.hennihaus.utils.withoutNanos
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Application.configureErrorHandling() {
    val zoneId = getProperty<String>(key = "ktor.application.timezoneId")

    install(StatusPages) {
        exception<Throwable> { call, throwable ->
            val dateTime = Clock.System.now()
                .toLocalDateTime(
                    timeZone = TimeZone.of(
                        zoneId = zoneId,
                    ),
                )
                .withoutNanos()

            when (throwable) {
                is UUIDException -> call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ErrorResponse(
                        message = throwable.message,
                        dateTime = dateTime,
                    )
                )
                is NotFoundException -> call.respond(
                    status = HttpStatusCode.NotFound,
                    message = ErrorResponse(
                        message = "${throwable.message}",
                        dateTime = dateTime,
                    ),
                )
                is TransactionException -> call.respond(
                    status = HttpStatusCode.Conflict,
                    message = ErrorResponse(
                        message = "[${throwable.message}]",
                        dateTime = dateTime,
                    )
                )
                else -> call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = ErrorResponse(
                        message = "$throwable",
                        dateTime = dateTime,
                    ),
                )
            }
        }
    }
}

object ErrorMessage {
    @Suppress("MaxLineLength")
    const val UUID_EXCEPTION_MESSAGE = """
        [id must match the expected pattern [a-fA-F0-9]{8}-[a-fA-F0-9]{4}-4[a-fA-F0-9]{3}-[89abAB][a-fA-F0-9]{3}-[a-fA-F0-9]{12}]
    """
    const val BROKER_EXCEPTION_MESSAGE = "Error while calling ActiveMQ"
    const val EXPOSED_TRANSACTION_EXCEPTION = "Exposed transaction failed"
    const val MISSING_PROPERTY_MESSAGE = "Missing property"
}

class UUIDException(override val message: String = UUID_EXCEPTION_MESSAGE) : RuntimeException()

class BrokerException(message: String?) : RuntimeException(message ?: BROKER_EXCEPTION_MESSAGE)

class TransactionException(message: String? = null) : RuntimeException(message ?: EXPOSED_TRANSACTION_EXCEPTION)

class PropertyNotFoundException(key: String) : IllegalStateException("$MISSING_PROPERTY_MESSAGE $key")
