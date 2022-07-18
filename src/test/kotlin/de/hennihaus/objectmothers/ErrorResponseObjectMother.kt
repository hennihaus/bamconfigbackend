package de.hennihaus.objectmothers

import de.hennihaus.models.rest.ErrorResponse
import de.hennihaus.plugins.ObjectIdException
import de.hennihaus.services.BankServiceImpl.Companion.BANK_NOT_FOUND_MESSAGE
import de.hennihaus.services.GroupServiceImpl.Companion.GROUP_NOT_FOUND_MESSAGE
import de.hennihaus.services.TaskServiceImpl.Companion.TASK_NOT_FOUND_MESSAGE
import de.hennihaus.utils.withoutNanos
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object ErrorResponseObjectMother {

    const val DEFAULT_ZONE_ID = "Europe/Berlin"

    fun getInvalidIdErrorResponse(
        message: String = ObjectIdException().message,
        dateTime: Instant = Clock.System.now(),
    ) = ErrorResponse(
        message = message,
        dateTime = dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos(),
    )

    fun getGroupNotFoundErrorResponse(
        message: String = GROUP_NOT_FOUND_MESSAGE,
        dateTime: Instant = Clock.System.now(),
    ) = ErrorResponse(
        message = message,
        dateTime = dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos(),
    )

    fun getBankNotFoundErrorResponse(
        message: String = BANK_NOT_FOUND_MESSAGE,
        dateTime: Instant = Clock.System.now(),
    ) = ErrorResponse(
        message = message,
        dateTime = dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos(),
    )

    fun getTaskNotFoundErrorResponse(
        message: String = TASK_NOT_FOUND_MESSAGE,
        dateTime: Instant = Clock.System.now(),
    ) = ErrorResponse(
        message = message,
        dateTime = dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos(),
    )

    fun getInternalServerErrorResponse(
        message: String = "${IllegalStateException()}",
        dateTime: Instant = Clock.System.now(),
    ) = ErrorResponse(
        message = message,
        dateTime = dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos(),
    )
}
