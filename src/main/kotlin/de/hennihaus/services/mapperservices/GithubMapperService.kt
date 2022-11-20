package de.hennihaus.services.mapperservices

import de.hennihaus.bamdatamodel.CreditConfiguration
import de.hennihaus.bamdatamodel.Team
import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.configurations.GithubConfiguration.Companion.DEFAULT_TITLE
import de.hennihaus.models.IntegrationStep
import de.hennihaus.models.Parameter
import de.hennihaus.models.Response
import de.hennihaus.models.Task
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
import de.hennihaus.services.TaskService.Companion.AMOUNT_IN_EUROS_PARAMETER
import de.hennihaus.services.TaskService.Companion.PASSWORD_PARAMETER
import de.hennihaus.services.TaskService.Companion.TERM_IN_MONTHS_PARAMETER
import de.hennihaus.services.TaskService.Companion.USERNAME_PARAMETER
import io.ktor.http.HttpStatusCode
import org.koin.core.annotation.Property
import org.koin.core.annotation.Single
import kotlin.math.roundToInt

@Single
class GithubMapperService(@Property(DEFAULT_TITLE) private val defaultTitle: String) {

    fun updateSchufaApi(api: SchufaApi, task: Task): SchufaApi {
        check(value = task.integrationStep == IntegrationStep.SCHUFA_STEP) {
            NO_SCHUFA_STEP_MESSAGE
        }
        return api.copy(
            info = api.info.updateSchufaInfo(
                task = task,
            ),
            components = api.components.updateSchufaComponents(
                task = task,
            ),
        )
    }

    fun updateSchufaApi(api: SchufaApi, team: Team): SchufaApi {
        check(value = team.type == TeamType.EXAMPLE) {
            NO_EXAMPLE_TEAM_MESSAGE
        }
        return api.copy(
            components = api.components.updateSchufaComponents(
                team = team,
            ),
        )
    }

    fun updateBankApi(api: BankApi, task: Task): BankApi {
        check(value = task.integrationStep == IntegrationStep.SYNC_BANK_STEP) {
            NO_BANK_STEP_MESSAGE
        }
        return api.copy(
            info = api.info.updateBankInfo(
                task = task,
            ),
            components = api.components.updateBankComponents(
                task = task,
            ),
        )
    }

    fun updateBankApi(api: BankApi, team: Team): BankApi {
        check(value = team.type == TeamType.EXAMPLE) {
            NO_EXAMPLE_TEAM_MESSAGE
        }
        return api.copy(
            components = api.components.updateBankComponents(
                team = team,
            ),
        )
    }

    fun updateBankApi(api: BankApi, creditConfiguration: CreditConfiguration): BankApi {
        return api.copy(
            components = api.components.updateBankComponents(
                creditConfiguration = creditConfiguration,
            ),
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
        parameters = parameters.updateSchufaParameters(
            task = task,
        ),
        responses = responses.updateSchufaResponses(
            task = task,
        ),
    )

    private fun SchufaComponents.updateSchufaComponents(team: Team) = copy(
        parameters = parameters.updateSchufaParameters(
            team = team,
        ),
    )

    private fun BankComponents.updateBankComponents(task: Task) = copy(
        parameters = parameters.updateBankParameters(
            task = task,
        ),
        responses = responses.updateBankResponses(
            task = task,
        ),
    )

    private fun BankComponents.updateBankComponents(team: Team) = copy(
        parameters = parameters.updateBankParameters(
            team = team,
        ),
    )

    private fun BankComponents.updateBankComponents(creditConfiguration: CreditConfiguration) = copy(
        parameters = parameters.updateBankParameters(
            creditConfiguration = creditConfiguration,
        ),
    )

    private fun LinkedHashMap<String, SchufaParameter>.updateSchufaParameters(task: Task) = mapValuesTo(
        destination = this,
        transform = { (key, parameter) ->
            task.parameters.find { it.name == key }
                ?.let {
                    parameter.updateSchufaParameter(
                        parameter = it,
                    )
                }
                ?: throw IllegalStateException(
                    buildParameterErrorMessage<SchufaApi, Task>(
                        parameter = key,
                    )
                )
        },
    )

    private fun LinkedHashMap<String, SchufaParameter>.updateSchufaParameters(team: Team) = mapValuesTo(
        destination = this,
        transform = { (key, parameter) ->
            when (key) {
                USERNAME_PARAMETER -> parameter.updateSchufaParameter(
                    example = team.username,
                )
                PASSWORD_PARAMETER -> parameter.updateSchufaParameter(
                    example = team.password,
                )
                else -> parameter
            }
        }
    )

    private fun LinkedHashMap<String, BankParameter>.updateBankParameters(task: Task) = mapValuesTo(
        destination = this,
        transform = { (key, parameter) ->
            task.parameters.find { it.name == key }
                ?.let {
                    parameter.updateBankParameter(
                        parameter = it,
                    )
                }
                ?: throw IllegalStateException(
                    buildParameterErrorMessage<BankApi, Task>(
                        parameter = key,
                    )
                )
        },
    )

    private fun LinkedHashMap<String, BankParameter>.updateBankParameters(team: Team) = mapValuesTo(
        destination = this,
        transform = { (key, parameter) ->
            when (key) {
                USERNAME_PARAMETER -> parameter.updateBankParameter(
                    example = team.username,
                )
                PASSWORD_PARAMETER -> parameter.updateBankParameter(
                    example = team.password,
                )
                else -> parameter
            }
        }
    )

    private fun LinkedHashMap<String, BankParameter>.updateBankParameters(creditConfiguration: CreditConfiguration) =
        mapValuesTo(
            destination = this,
            transform = { (key, parameter) ->
                when (key) {
                    AMOUNT_IN_EUROS_PARAMETER -> parameter.updateBankParameter(
                        minimum = creditConfiguration.minAmountInEuros,
                        maximum = creditConfiguration.maxAmountInEuros,
                    )
                    TERM_IN_MONTHS_PARAMETER -> parameter.updateBankParameter(
                        minimum = creditConfiguration.minTermInMonths,
                        maximum = creditConfiguration.maxTermInMonths,
                    )
                    else -> parameter
                }
            }
        )

    private fun SchufaParameter.updateSchufaParameter(parameter: Parameter) = copy(
        description = parameter.description,
        example = parameter.example,
    )

    private fun SchufaParameter.updateSchufaParameter(example: String) = copy(
        example = example,
    )

    private fun BankParameter.updateBankParameter(parameter: Parameter) = copy(
        description = parameter.description,
        example = parameter.example,
    )

    private fun BankParameter.updateBankParameter(minimum: Int, maximum: Int) = copy(
        example = "${arrayOf(minimum, maximum).average().roundToInt()}",
        schema = schema.updateBankSchema(
            minimum = minimum,
            maximum = maximum,
        ),
    )

    private fun BankParameter.updateBankParameter(example: String) = copy(
        example = example,
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
                    buildResponseErrorMessage<SchufaApi, Task>(
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
                    buildResponseErrorMessage<BankApi, Task>(
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

    private inline fun <reified T : Any, reified E : Any> buildParameterErrorMessage(parameter: String) =
        "parameter $parameter not found in ${E::class.simpleName} while updating ${T::class.simpleName}"

    private inline fun <reified T : Any, reified E : Any> buildResponseErrorMessage(response: String) =
        "response $response not found in ${E::class.simpleName} while updating ${T::class.simpleName}"

    companion object {
        const val NO_SCHUFA_STEP_MESSAGE = "provided task is not a schufa task"
        const val NO_BANK_STEP_MESSAGE = "provided task is not a bank task"
        const val NO_EXAMPLE_TEAM_MESSAGE = "provided team is not an example team"
    }
}
