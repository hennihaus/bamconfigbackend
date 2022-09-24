package de.hennihaus.objectmothers

import de.hennihaus.models.generated.rest.ErrorResponseDTO
import de.hennihaus.plugins.TransactionException
import de.hennihaus.plugins.UUIDException
import de.hennihaus.services.BankService.Companion.BANK_NOT_FOUND_MESSAGE
import de.hennihaus.services.StatisticService.Companion.STATISTIC_NOT_FOUND_MESSAGE
import de.hennihaus.services.TaskService.Companion.TASK_NOT_FOUND_MESSAGE
import de.hennihaus.services.TeamService.Companion.TEAM_NOT_FOUND_MESSAGE
import de.hennihaus.utils.withoutNanos
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object ErrorResponseObjectMother {

    const val DEFAULT_ZONE_ID = "Europe/Berlin"

    fun getInvalidIdErrorResponse(
        message: String = UUIDException().message,
        dateTime: Instant = Clock.System.now(),
    ) = ErrorResponseDTO(
        message = message,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getTeamNotFoundErrorResponse(
        message: String = TEAM_NOT_FOUND_MESSAGE,
        dateTime: Instant = Clock.System.now(),
    ) = ErrorResponseDTO(
        message = message,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getBankNotFoundErrorResponse(
        message: String = BANK_NOT_FOUND_MESSAGE,
        dateTime: Instant = Clock.System.now(),
    ) = ErrorResponseDTO(
        message = message,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getTaskNotFoundErrorResponse(
        message: String = TASK_NOT_FOUND_MESSAGE,
        dateTime: Instant = Clock.System.now(),
    ) = ErrorResponseDTO(
        message = message,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getStatisticNotFoundErrorResponse(
        message: String = STATISTIC_NOT_FOUND_MESSAGE,
        dateTime: Instant = Clock.System.now(),
    ) = ErrorResponseDTO(
        message = message,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getConflictErrorResponse(
        message: String = "[${TransactionException().message}]",
        dateTime: Instant = Clock.System.now(),
    ) = ErrorResponseDTO(
        message = message,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getInternalServerErrorResponse(
        message: String = "${IllegalStateException()}",
        dateTime: Instant = Clock.System.now(),
    ) = ErrorResponseDTO(
        message = message,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )
}
