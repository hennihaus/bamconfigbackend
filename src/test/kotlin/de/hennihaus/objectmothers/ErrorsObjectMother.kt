package de.hennihaus.objectmothers

import de.hennihaus.models.generated.rest.ErrorsDTO
import de.hennihaus.models.generated.rest.ReasonDTO
import de.hennihaus.objectmothers.ReasonObjectMother.getBankNotFoundReason
import de.hennihaus.objectmothers.ReasonObjectMother.getConflictReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInternalServerErrorReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInvalidBankReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInvalidCursorReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInvalidIdReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInvalidLimitReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInvalidQueryReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInvalidStatisticReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInvalidTaskReason
import de.hennihaus.objectmothers.ReasonObjectMother.getInvalidTeamReason
import de.hennihaus.objectmothers.ReasonObjectMother.getStatisticNotFoundReason
import de.hennihaus.objectmothers.ReasonObjectMother.getTaskNotFoundReason
import de.hennihaus.objectmothers.ReasonObjectMother.getTeamNotFoundReason
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object ErrorsObjectMother {

    fun getInvalidIdErrors(
        reasons: List<ReasonDTO> = getInvalidIdReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    fun getInvalidLimitErrors(
        reasons: List<ReasonDTO> = getInvalidLimitReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    fun getInvalidTeamErrors(
        reasons: List<ReasonDTO> = getInvalidTeamReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    fun getInvalidBankErrors(
        reasons: List<ReasonDTO> = getInvalidBankReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    fun getInvalidTaskErrors(
        reasons: List<ReasonDTO> = getInvalidTaskReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    fun getInvalidStatisticErrors(
        reasons: List<ReasonDTO> = getInvalidStatisticReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    fun getInvalidCursorErrors(
        reasons: List<ReasonDTO> = getInvalidCursorReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    fun getInvalidQueryErrors(
        reasons: List<ReasonDTO> = getInvalidQueryReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    fun getTeamNotFoundErrors(
        reasons: List<ReasonDTO> = getTeamNotFoundReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    fun getBankNotFoundErrors(
        reasons: List<ReasonDTO> = getBankNotFoundReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    fun getTaskNotFoundErrors(
        reasons: List<ReasonDTO> = getTaskNotFoundReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    fun getStatisticNotFoundErrors(
        reasons: List<ReasonDTO> = getStatisticNotFoundReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    fun getConflictErrors(
        reasons: List<ReasonDTO> = getConflictReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    fun getInternalServerErrors(
        reasons: List<ReasonDTO> = getInternalServerErrorReasons(),
        dateTime: ZonedDateTime = ZonedDateTime.now(),
    ) = ErrorsDTO(
        reasons = reasons,
        dateTime = "${dateTime.truncatedTo(ChronoUnit.SECONDS)}",
    )

    private fun getInvalidIdReasons() = listOf(
        getInvalidIdReason(),
    )

    private fun getInvalidLimitReasons() = listOf(
        getInvalidLimitReason(),
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

    private fun getInvalidCursorReasons() = listOf(
        getInvalidCursorReason(),
    )

    private fun getInvalidQueryReasons() = listOf(
        getInvalidQueryReason(),
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
