package de.hennihaus.plugins

import de.hennihaus.models.generated.rest.BankDTO
import de.hennihaus.models.generated.rest.StatisticDTO
import de.hennihaus.models.generated.rest.TaskDTO
import de.hennihaus.models.generated.rest.TeamDTO
import de.hennihaus.routes.validations.BankValidationService
import de.hennihaus.routes.validations.StatisticValidationService
import de.hennihaus.routes.validations.TaskValidationService
import de.hennihaus.routes.validations.TeamValidationService
import de.hennihaus.routes.validations.ValidationService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import org.koin.java.KoinJavaComponent.getKoin

fun Application.configureValidation() {
    install(plugin = RequestValidation) {
        validate<TeamDTO> {
            it.validateBodyWithService<TeamDTO, TeamValidationService>()
        }
        validate<BankDTO> {
            it.validateBodyWithService<BankDTO, BankValidationService>()
        }
        validate<StatisticDTO> {
            it.validateBodyWithService<StatisticDTO, StatisticValidationService>()
        }
        validate<TaskDTO> {
            it.validateBodyWithService<TaskDTO, TaskValidationService>()
        }
    }
}

private suspend inline fun <B, reified S : ValidationService<B>> B.validateBodyWithService(): ValidationResult {
    val errors = getKoin().get<S>().validateBody(body = this)
    return if (errors.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(reasons = errors)
}
