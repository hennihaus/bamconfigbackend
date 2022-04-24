package de.hennihaus.objectmothers

import de.hennihaus.plugins.ExceptionResponse
import de.hennihaus.plugins.ObjectIdException
import de.hennihaus.services.BankServiceImpl
import de.hennihaus.services.GroupServiceImpl
import de.hennihaus.services.TaskServiceImpl
import io.ktor.http.HttpStatusCode

object ExceptionResponseObjectMother {

    const val INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error"

    fun getInternalServerErrorResponse(
        code: HttpStatusCode = HttpStatusCode.InternalServerError,
        message: String = INTERNAL_SERVER_ERROR_MESSAGE
    ) = ExceptionResponse(
        code = code.value,
        error = message
    )

    fun getInvalidIdErrorResponse(
        code: HttpStatusCode = HttpStatusCode.BadRequest,
        message: String = ObjectIdException().message
    ) = ExceptionResponse(
        code = code.value,
        error = message
    )

    fun getGroupNotFoundErrorResponse(
        code: HttpStatusCode = HttpStatusCode.NotFound,
        message: String = GroupServiceImpl.ID_MESSAGE
    ) = ExceptionResponse(
        code = code.value,
        error = message
    )

    fun getBankNotFoundErrorResponse(
        code: HttpStatusCode = HttpStatusCode.NotFound,
        message: String = BankServiceImpl.ID_MESSAGE
    ) = ExceptionResponse(
        code = code.value,
        error = message
    )

    fun getTaskNotFoundErrorResponse(
        code: HttpStatusCode = HttpStatusCode.NotFound,
        message: String = TaskServiceImpl.ID_MESSAGE
    ) = ExceptionResponse(
        code = code.value,
        error = message
    )
}
