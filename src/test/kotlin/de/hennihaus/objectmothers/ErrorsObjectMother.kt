package de.hennihaus.objectmothers

import de.hennihaus.models.generated.rest.ErrorsDTO
import de.hennihaus.models.generated.rest.ReasonDTO
import de.hennihaus.objectmothers.ReasonObjectMother.getBankNotFoundReason
import de.hennihaus.objectmothers.ReasonObjectMother.getConflictReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInternalServerErrorReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInvalidBankReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInvalidIdReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInvalidStatisticReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInvalidTaskReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInvalidTeamReason
import de.hennihaus.objectmothers.ReasonObjectMother.getStatisticNotFoundReason
import de.hennihaus.objectmothers.ReasonObjectMother.getTaskNotFoundReason
import de.hennihaus.objectmothers.ReasonObjectMother.getTeamNotFoundReason
import de.hennihaus.utils.withoutNanos
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object ErrorsObjectMother {

    const val DEFAULT_ZONE_ID = "Europe/Berlin"

    fun getInvalidIdErrors(
        reasons: List<ReasonDTO> = getInvalidIdReasons(),
        dateTime: Instant = Clock.System.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getInvalidTeamErrors(
        reasons: List<ReasonDTO> = getInvalidTeamReasons(),
        dateTime: Instant = Clock.System.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getInvalidBankErrors(
        reasons: List<ReasonDTO> = getInvalidBankReasons(),
        dateTime: Instant = Clock.System.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getInvalidTaskErrors(
        reasons: List<ReasonDTO> = getInvalidTaskReasons(),
        dateTime: Instant = Clock.System.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getInvalidStatisticErrors(
        reasons: List<ReasonDTO> = getInvalidStatisticReasons(),
        dateTime: Instant = Clock.System.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getTeamNotFoundErrors(
        reasons: List<ReasonDTO> = getTeamNotFoundReasons(),
        dateTime: Instant = Clock.System.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getBankNotFoundErrors(
        reasons: List<ReasonDTO> = getBankNotFoundReasons(),
        dateTime: Instant = Clock.System.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getTaskNotFoundErrors(
        reasons: List<ReasonDTO> = getTaskNotFoundReasons(),
        dateTime: Instant = Clock.System.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getStatisticNotFoundErrors(
        reasons: List<ReasonDTO> = getStatisticNotFoundReasons(),
        dateTime: Instant = Clock.System.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getConflictErrors(
        reasons: List<ReasonDTO> = getConflictReasons(),
        dateTime: Instant = Clock.System.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    fun getInternalServerErrors(
        reasons: List<ReasonDTO> = getInternalServerErrorReasons(),
        dateTime: Instant = Clock.System.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.toLocalDateTime(timeZone = TimeZone.of(zoneId = DEFAULT_ZONE_ID)).withoutNanos()}",
    )

    private fun getInvalidIdReasons() = listOf(
        getInvalidIdReason(),
    )

    private fun getInvalidTeamReasons() = listOf(
        getInvalidTeamReason(),
    )

    private fun getInvalidBankReasons() = listOf(
        getInvalidBankReason(),
    )

    private fun getInvalidTaskReasons() = listOf(
        getInvalidTaskReason(),
    )

    private fun getInvalidStatisticReasons() = listOf(
        getInvalidStatisticReason(),
    )

    private fun getTeamNotFoundReasons() = listOf(
        getTeamNotFoundReason(),
    )

    private fun getBankNotFoundReasons() = listOf(
        getBankNotFoundReason(),
    )

    private fun getTaskNotFoundReasons() = listOf(
        getTaskNotFoundReason(),
    )

    private fun getStatisticNotFoundReasons() = listOf(
        getStatisticNotFoundReason(),
    )

    private fun getConflictReasons() = listOf(
        getConflictReason(),
    )

    private fun getInternalServerErrorReasons() = listOf(
        getInternalServerErrorReason(),
    )
}
