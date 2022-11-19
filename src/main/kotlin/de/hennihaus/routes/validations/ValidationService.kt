package de.hennihaus.routes.validations

import de.hennihaus.utils.validations.cursor
import io.konform.validation.Validation

interface ValidationService<Body : Any, QueryDTO : Any> {

    suspend fun bodyValidation(body: Body): Validation<Body> = Validation {}

    suspend fun urlValidation(query: QueryDTO): Validation<QueryDTO> = Validation {}

    fun <Query : Any> cursorValidation(cursor: String): Validation<String> = Validation {
        cursor<Query>()
    }

    suspend fun validateBody(body: Body): List<String> = bodyValidation(body = body).validate(value = body).errors.map {
        "${it.dataPath.substringAfter(delimiter = ".")} ${it.message}"
    }

    suspend fun validateUrl(query: QueryDTO): List<String> {
        return urlValidation(query = query).validate(value = query).errors.map {
            "${it.dataPath.substringAfter(delimiter = ".")} ${it.message}"
        }
    }

    fun <Query : Any> validateCursor(cursor: String): List<String> {
        return cursorValidation<Query>(cursor = cursor).validate(value = cursor).errors.map {
            "request ${it.message}"
        }
    }

    companion object {
        const val LIMIT_MINIMUM = 1
        const val LIMIT_MAXIMUM = 1_000
        const val JMS_QUEUE_MIN_LENGTH = 6
        const val JMS_QUEUE_MAX_LENGTH = 50
        const val NAME_MIN_LENGTH = 2
        const val NAME_MAX_LENGTH = 50
    }
}
