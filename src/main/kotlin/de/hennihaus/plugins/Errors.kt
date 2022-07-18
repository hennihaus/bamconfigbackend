package de.hennihaus.plugins

import de.hennihaus.models.rest.ErrorResponse
import de.hennihaus.plugins.ErrorMessage.BROKER_EXCEPTION_MESSAGE
import de.hennihaus.plugins.ErrorMessage.MISSING_PROPERTY_MESSAGE
import de.hennihaus.plugins.ErrorMessage.OBJECT_ID_EXCEPTION_MESSAGE
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
                is ObjectIdException -> call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ErrorResponse(
                        message = throwable.message,
                        dateTime = dateTime,
                    ),
                )
                is NotFoundException -> call.respond(
                    status = HttpStatusCode.NotFound,
                    message = ErrorResponse(
                        message = "${throwable.message}",
                        dateTime = dateTime,
                    ),
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
    const val OBJECT_ID_EXCEPTION_MESSAGE = """
        [id must match the expected pattern ^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}]
    """
    const val BROKER_EXCEPTION_MESSAGE = "Error while calling ActiveMQ"
    const val MISSING_PROPERTY_MESSAGE = "Missing property"
}

class ObjectIdException(override val message: String = OBJECT_ID_EXCEPTION_MESSAGE) : RuntimeException()

class BrokerException(override val message: String?) : RuntimeException(message ?: BROKER_EXCEPTION_MESSAGE)

class PropertyNotFoundException(key: String) : IllegalStateException("$MISSING_PROPERTY_MESSAGE $key")
