package de.hennihaus.routes.validations

import io.konform.validation.Validation

interface ValidationService<Body> {

    suspend fun bodyValidation(body: Body): Validation<Body>

    suspend fun validateBody(body: Body): List<String> = bodyValidation(body).validate(value = body).errors.map {
        "${it.dataPath.substringAfter(delimiter = ".")} ${it.message}"
    }

    companion object {
        const val JMS_QUEUE_MIN_LENGTH = 6
        const val JMS_QUEUE_MAX_LENGTH = 50
        const val NAME_MIN_LENGTH = 2
        const val NAME_MAX_LENGTH = 50
    }
}
