package de.hennihaus.plugins

import de.hennihaus.configurations.Configuration.API_VERSION
import de.hennihaus.models.generated.rest.BankDTO
import de.hennihaus.models.generated.rest.StatisticDTO
import de.hennihaus.models.generated.rest.TaskDTO
import de.hennihaus.models.generated.rest.TeamDTO
import de.hennihaus.models.generated.rest.TeamQueryDTO
import de.hennihaus.routes.TeamRoutes.TEAMS_PATH
import de.hennihaus.routes.mappers.toNativeCursor
import de.hennihaus.routes.mappers.toTeamQueryDTO
import de.hennihaus.routes.validations.BankValidationService
import de.hennihaus.routes.validations.StatisticValidationService
import de.hennihaus.routes.validations.TaskValidationService
import de.hennihaus.routes.validations.TeamValidationService
import de.hennihaus.routes.validations.ValidationService
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.uri
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
    install(plugin = UrlValidation) {
        validate(path = "/$TEAMS_PATH") {
            it.parameters.toTeamQueryDTO().validateUrlWithService<TeamQueryDTO, TeamValidationService>()
            it.parameters.toNativeCursor()?.validateCursorWithService<TeamQueryDTO, TeamValidationService>()
        }
    }
}

private suspend inline fun <B, reified S : ValidationService<B, *>> B.validateBodyWithService(): ValidationResult {
    val reasons = getKoin().get<S>().validateBody(body = this)
    return if (reasons.isEmpty()) ValidationResult.Valid else ValidationResult.Invalid(reasons = reasons)
}

private suspend inline fun <Q : Any, reified S : ValidationService<*, Q>> Q.validateUrlWithService() {
    val reasons = getKoin().get<S>().validateUrl(query = this)

    if (reasons.isNotEmpty()) throw RequestValidationException(
        value = this,
        reasons = reasons,
    )
}

private inline fun <Q : Any, reified S : ValidationService<*, Q>> String.validateCursorWithService() {
    val reasons = getKoin().get<S>().validateCursor<Q>(cursor = this)

    if (reasons.isNotEmpty()) throw RequestValidationException(
        value = this,
        reasons = reasons,
    )
}

private val UrlValidation = createRouteScopedPlugin(
    name = "UrlValidation",
    createConfiguration = ::UrlValidationConfiguration,
) {
    val validations = pluginConfig.validations
    val apiVersion = applicationConfig?.property(path = API_VERSION)?.getString() ?: ""

    onCall { call ->
        validations.toList()
            .find { (path, _) ->
                call.request.uri.replace(oldValue = "/$apiVersion", newValue = "").startsWith(prefix = path)
            }
            ?.also { (_, validation) ->
                validation(call)
            }
    }
}

private class UrlValidationConfiguration {
    var validations: MutableMap<String, suspend (ApplicationCall) -> Unit> = mutableMapOf()

    fun validate(path: String, validation: suspend (ApplicationCall) -> Unit) {
        validations += path to validation
    }
}
