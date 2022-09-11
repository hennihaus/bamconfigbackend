package de.hennihaus.services.mapperservices

import de.hennihaus.configurations.GithubConfiguration.Companion.DEFAULT_TITLE
import de.hennihaus.models.generated.CreditConfiguration
import de.hennihaus.models.generated.IntegrationStep
import de.hennihaus.models.generated.Parameter
import de.hennihaus.models.generated.Response
import de.hennihaus.models.generated.Task
import de.hennihaus.models.generated.openapi.BankApi
import de.hennihaus.models.generated.openapi.BankComponents
import de.hennihaus.models.generated.openapi.BankContact
import de.hennihaus.models.generated.openapi.BankInfo
import de.hennihaus.models.generated.openapi.BankParameter
import de.hennihaus.models.generated.openapi.BankResponse
import de.hennihaus.models.generated.openapi.BankSchema
import de.hennihaus.models.generated.openapi.SchufaApi
import de.hennihaus.models.generated.openapi.SchufaComponents
import de.hennihaus.models.generated.openapi.SchufaContact
import de.hennihaus.models.generated.openapi.SchufaInfo
import de.hennihaus.models.generated.openapi.SchufaParameter
import de.hennihaus.models.generated.openapi.SchufaResponse
import io.ktor.http.HttpStatusCode
import org.koin.core.annotation.Property
import org.koin.core.annotation.Single
import kotlin.math.roundToInt

@Single
@Suppress("TooManyFunctions")
class GithubMapperService(@Property(DEFAULT_TITLE) private val defaultTitle: String) {

    fun updateSchufaApi(api: SchufaApi, task: Task): SchufaApi {
        check(value = task.integrationStep == IntegrationStep.SCHUFA_STEP) {
            NO_SCHUFA_STEP_MESSAGE
        }
        return api.copy(
            info = api.info.updateSchufaInfo(task = task),
            components = api.components.updateSchufaComponents(task = task),
        )
    }

    fun updateBankApi(api: BankApi, task: Task): BankApi {
        check(value = task.integrationStep == IntegrationStep.SYNC_BANK_STEP) {
            NO_BANK_STEP_MESSAGE
        }
        return api.copy(
            info = api.info.updateBankInfo(task = task),
            components = api.components.updateBankComponents(task = task),
        )
    }

    private fun SchufaInfo.updateSchufaInfo(task: Task) = copy(
        title = "$defaultTitle - ${task.title}",
        contact = contact.updateSchufaContact(task = task),
        description = if (task.isOpenApiVerbose) task.description else "",
    )

    private fun BankInfo.updateBankInfo(task: Task) = copy(
        title = "$defaultTitle - ${task.title}",
        contact = contact.updateBankContact(task = task),
        description = if (task.isOpenApiVerbose) task.description else "",
    )

    private fun SchufaContact.updateSchufaContact(task: Task) = copy(
        name = "${task.contact.firstname} ${task.contact.lastname}",
        email = task.contact.email,
    )

    private fun BankContact.updateBankContact(task: Task) = copy(
        name = "${task.contact.firstname} ${task.contact.lastname}",
        email = task.contact.email,
    )

    private fun SchufaComponents.updateSchufaComponents(task: Task) = copy(
        parameters = parameters.updateSchufaParameters(task = task),
        responses = responses.updateSchufaResponses(task = task),
    )

    private fun BankComponents.updateBankComponents(task: Task) = copy(
        parameters = parameters.updateBankParameters(task = task),
        responses = responses.updateBankResponses(task = task),
    )

    private fun LinkedHashMap<String, SchufaParameter>.updateSchufaParameters(task: Task) = mapValuesTo(
        destination = this,
        transform = { (key, parameter) ->
            task.parameters.find { it.name == key }
                ?.let {
                    parameter.updateSchufaParameter(parameter = it)
                }
                ?: throw IllegalStateException(
                    buildParameterErrorMessage<SchufaApi>(
                        parameter = key,
                    )
                )
        },
    )

    private fun LinkedHashMap<String, BankParameter>.updateBankParameters(task: Task) = mapValuesTo(
        destination = this,
        transform = { (key, parameter) ->
            task.parameters.find { it.name == key }
                ?.let {
                    parameter.updateBankParameter(
                        parameter = it,
                        configuration = task.banks.getOrNull(index = SYNC_BANK_INDEX)
                            ?.creditConfiguration
                            ?: throw IllegalStateException(NO_CONFIGURATION_MESSAGE)
                    )
                }
                ?: throw IllegalStateException(
                    buildParameterErrorMessage<BankApi>(
                        parameter = key,
                    )
                )
        },
    )

    private fun SchufaParameter.updateSchufaParameter(parameter: Parameter) = copy(
        description = parameter.description,
        example = parameter.example,
    )

    private fun BankParameter.updateBankParameter(
        parameter: Parameter,
        configuration: CreditConfiguration,
    ) = when (parameter.name) {
        AMOUNT_IN_EUROS_PARAMETER -> updateBankParameter(
            parameter = parameter,
            minimum = configuration.minAmountInEuros,
            maximum = configuration.maxAmountInEuros,
        )
        TERM_IN_MONTHS_PARAMETER -> updateBankParameter(
            parameter = parameter,
            minimum = configuration.minTermInMonths,
            maximum = configuration.maxTermInMonths,
        )
        else -> copy(
            description = parameter.description,
            example = parameter.example,
        )
    }

    private fun BankParameter.updateBankParameter(parameter: Parameter, minimum: Int, maximum: Int) = copy(
        description = parameter.description,
        example = "${arrayOf(minimum, maximum).average().roundToInt()}",
        schema = schema.updateBankSchema(minimum = minimum, maximum = maximum),
    )

    private fun BankSchema.updateBankSchema(minimum: Int, maximum: Int) = copy(
        minimum = minimum,
        maximum = maximum,
    )

    private fun LinkedHashMap<String, SchufaResponse>.updateSchufaResponses(task: Task) = mapValuesTo(
        destination = this,
        transform = { (key, response) ->
            task.responses.find { it.httpStatusCode == HttpStatusCode.fromValue(value = key.toInt()) }
                ?.let {
                    response.updateSchufaResponse(response = it)
                }
                ?: throw IllegalStateException(
                    buildResponseErrorMessage<SchufaApi>(
                        response = key,
                    )
                )
        },
    )

    private fun LinkedHashMap<String, BankResponse>.updateBankResponses(task: Task) = mapValuesTo(
        destination = this,
        transform = { (key, response) ->
            task.responses.find { it.httpStatusCode == HttpStatusCode.fromValue(value = key.toInt()) }
                ?.let {
                    response.updateBankResponse(response = it)
                }
                ?: throw IllegalStateException(
                    buildResponseErrorMessage<BankApi>(
                        response = key,
                    )
                )
        },
    )

    private fun SchufaResponse.updateSchufaResponse(response: Response) = copy(
        description = response.description,
    )

    private fun BankResponse.updateBankResponse(response: Response) = copy(
        description = response.description,
    )

    private inline fun <reified T : Any> buildParameterErrorMessage(parameter: String) =
        "Parameter $parameter not found in Task while updating ${T::class.simpleName}!"

    private inline fun <reified T : Any> buildResponseErrorMessage(response: String) =
        "Response $response not found in Task while updating ${T::class.simpleName}!"

    companion object {
        const val SYNC_BANK_INDEX = 0

        const val AMOUNT_IN_EUROS_PARAMETER = "amountInEuros"
        const val TERM_IN_MONTHS_PARAMETER = "termInMonths"

        const val NO_SCHUFA_STEP_MESSAGE = "Provided task is not a schufa task!"
        const val NO_BANK_STEP_MESSAGE = "Provided task is not a bank task!"
        const val NO_CONFIGURATION_MESSAGE = "Provided task has no bank with credit configuration!"
    }
}
