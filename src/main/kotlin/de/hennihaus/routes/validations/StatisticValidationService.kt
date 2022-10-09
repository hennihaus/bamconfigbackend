package de.hennihaus.routes.validations

import de.hennihaus.models.generated.rest.StatisticDTO
import de.hennihaus.utils.validations.uuid
import io.konform.validation.Validation
import org.koin.core.annotation.Single

@Single
class StatisticValidationService : ValidationService<StatisticDTO> {

    override suspend fun bodyValidation(body: StatisticDTO): Validation<StatisticDTO> = Validation {
        StatisticDTO::bankId {
            uuid()
        }
        StatisticDTO::teamId {
            uuid()
        }
    }
}
