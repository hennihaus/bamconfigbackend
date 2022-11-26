package de.hennihaus.objectmothers

import de.hennihaus.models.generated.rest.ReasonDTO
import de.hennihaus.plugins.TransactionException
import de.hennihaus.plugins.UUIDException
import de.hennihaus.routes.validations.ValidationService.Companion.LIMIT_MINIMUM
import de.hennihaus.services.BankService.Companion.BANK_NOT_FOUND_MESSAGE
import de.hennihaus.services.StatisticService.Companion.STATISTIC_NOT_FOUND_MESSAGE
import de.hennihaus.services.TaskService.Companion.TASK_NOT_FOUND_MESSAGE
import de.hennihaus.services.TeamService.Companion.TEAM_NOT_FOUND_MESSAGE
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.plugins.requestvalidation.RequestValidationException

object ReasonObjectMother {

    const val INVALID_TEAM_MESSAGE = "uuid must have valid uuid format"
    const val INVALID_BANK_MESSAGE = "uuid must have valid uuid format"
    const val INVALID_TASK_MESSAGE = "uuid must have valid uuid format"
    const val INVALID_STATISTIC_MESSAGE = "bankId must have valid uuid format"
    const val INVALID_CURSOR_MESSAGE = "request must have valid cursor"
    const val INVALID_QUERY_MESSAGE = "limit must be at least '$LIMIT_MINIMUM'"

    fun getInvalidIdReason(
        exception: String = UUIDException::class.simpleName!!,
        message: String = UUIDException().message,
    ) = ReasonDTO(
        exception = exception,
        message = message,
    )

    fun getInvalidTeamReason(
        exception: String = RequestValidationException::class.simpleName!!,
        message: String = INVALID_TEAM_MESSAGE,
    ) = ReasonDTO(
        exception = exception,
        message = message,
    )

    fun getInvalidBankReason(
        exception: String = RequestValidationException::class.simpleName!!,
        message: String = INVALID_BANK_MESSAGE,
    ) = ReasonDTO(
        exception = exception,
        message = message,
    )

    fun getInvalidTaskReason(
        exception: String = RequestValidationException::class.simpleName!!,
        message: String = INVALID_TASK_MESSAGE,
    ) = ReasonDTO(
        exception = exception,
        message = message,
    )

    fun getInvalidStatisticReason(
        exception: String = RequestValidationException::class.simpleName!!,
        message: String = INVALID_STATISTIC_MESSAGE,
    ) = ReasonDTO(
        exception = exception,
        message = message,
    )

    fun getInvalidCursorReason(
        exception: String = RequestValidationException::class.simpleName!!,
        message: String = INVALID_CURSOR_MESSAGE,
    ) = ReasonDTO(
        exception = exception,
        message = message,
    )

    fun getInvalidQueryReason(
        exception: String = RequestValidationException::class.simpleName!!,
        message: String = INVALID_QUERY_MESSAGE,
    ) = ReasonDTO(
        exception = exception,
        message = message,
    )

    fun getTeamNotFoundReason(
        exception: String = NotFoundException::class.simpleName!!,
        message: String = TEAM_NOT_FOUND_MESSAGE,
    ) = ReasonDTO(
        exception = exception,
        message = message,
    )

    fun getBankNotFoundReason(
        exception: String = NotFoundException::class.simpleName!!,
        message: String = BANK_NOT_FOUND_MESSAGE,
    ) = ReasonDTO(
        exception = exception,
        message = message,
    )

    fun getTaskNotFoundReason(
        exception: String = NotFoundException::class.simpleName!!,
        message: String = TASK_NOT_FOUND_MESSAGE,
    ) = ReasonDTO(
        exception = exception,
        message = message,
    )

    fun getStatisticNotFoundReason(
        exception: String = NotFoundException::class.simpleName!!,
        message: String = STATISTIC_NOT_FOUND_MESSAGE,
    ) = ReasonDTO(
        exception = exception,
        message = message,
    )

    fun getConflictReason(
        exception: String = TransactionException::class.simpleName!!,
        message: String = "${TransactionException().message}",
    ) = ReasonDTO(
        exception = exception,
        message = message,
    )

    fun getInternalServerErrorReason(
        exception: String = IllegalStateException::class.simpleName!!,
        message: String = "${IllegalStateException().message}",
    ) = ReasonDTO(
        exception = exception,
        message = message,
    )
}
