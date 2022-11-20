package de.hennihaus.objectmothers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.hennihaus.bamdatamodel.objectmothers.CreditConfigurationObjectMother.DEFAULT_MAX_AMOUNT_IN_EUROS
import de.hennihaus.bamdatamodel.objectmothers.CreditConfigurationObjectMother.DEFAULT_MAX_TERM_IN_MONTHS
import de.hennihaus.bamdatamodel.objectmothers.CreditConfigurationObjectMother.DEFAULT_MIN_AMOUNT_IN_EUROS
import de.hennihaus.bamdatamodel.objectmothers.CreditConfigurationObjectMother.DEFAULT_MIN_TERM_IN_MONTHS
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
import de.hennihaus.objectmothers.GithubObjectMother.DEFAULT_TITLE
import de.hennihaus.objectmothers.ParameterObjectMother.AMOUNT_IN_EUROS_DESCRIPTION
import de.hennihaus.objectmothers.ParameterObjectMother.AMOUNT_IN_EUROS_EXAMPLE
import de.hennihaus.objectmothers.ParameterObjectMother.AMOUNT_IN_EUROS_PARAMETER
import de.hennihaus.objectmothers.ParameterObjectMother.DELAY_IN_MILLISECONDS_DESCRIPTION
import de.hennihaus.objectmothers.ParameterObjectMother.DELAY_IN_MILLISECONDS_EXAMPLE
import de.hennihaus.objectmothers.ParameterObjectMother.DELAY_IN_MILLISECONDS_PARAMETER
import de.hennihaus.objectmothers.ParameterObjectMother.PASSWORD_DESCRIPTION
import de.hennihaus.objectmothers.ParameterObjectMother.PASSWORD_EXAMPLE
import de.hennihaus.objectmothers.ParameterObjectMother.PASSWORD_PARAMETER
import de.hennihaus.objectmothers.ParameterObjectMother.RATING_LEVEL_DESCRIPTION
import de.hennihaus.objectmothers.ParameterObjectMother.RATING_LEVEL_EXAMPLE
import de.hennihaus.objectmothers.ParameterObjectMother.RATING_LEVEL_PARAMETER
import de.hennihaus.objectmothers.ParameterObjectMother.SOCIAL_SECURITY_NUMBER_DESCRIPTION
import de.hennihaus.objectmothers.ParameterObjectMother.SOCIAL_SECURITY_NUMBER_EXAMPLE
import de.hennihaus.objectmothers.ParameterObjectMother.SOCIAL_SECURITY_NUMBER_PARAMETER
import de.hennihaus.objectmothers.ParameterObjectMother.TERM_IN_MONTHS_DESCRIPTION
import de.hennihaus.objectmothers.ParameterObjectMother.TERM_IN_MONTHS_EXAMPLE
import de.hennihaus.objectmothers.ParameterObjectMother.TERM_IN_MONTHS_PARAMETER
import de.hennihaus.objectmothers.ParameterObjectMother.USERNAME_DESCRIPTION
import de.hennihaus.objectmothers.ParameterObjectMother.USERNAME_EXAMPLE
import de.hennihaus.objectmothers.ParameterObjectMother.USERNAME_PARAMETER
import de.hennihaus.objectmothers.ResponseObjectMother.BAD_REQUEST_CODE
import de.hennihaus.objectmothers.ResponseObjectMother.BAD_REQUEST_DESCRIPTION
import de.hennihaus.objectmothers.ResponseObjectMother.BANK_OK_DESCRIPTION
import de.hennihaus.objectmothers.ResponseObjectMother.INTERNAL_SERVER_ERROR_CODE
import de.hennihaus.objectmothers.ResponseObjectMother.INTERNAL_SERVER_ERROR_DESCRIPTION
import de.hennihaus.objectmothers.ResponseObjectMother.NOT_FOUND_CODE
import de.hennihaus.objectmothers.ResponseObjectMother.NOT_FOUND_DESCRIPTION
import de.hennihaus.objectmothers.ResponseObjectMother.OK_CODE
import de.hennihaus.objectmothers.ResponseObjectMother.SCHUFA_OK_DESCRIPTION
import de.hennihaus.objectmothers.TaskObjectMother.DEFAULT_CONTACT_EMAIL
import de.hennihaus.objectmothers.TaskObjectMother.DEFAULT_CONTACT_FIRSTNAME
import de.hennihaus.objectmothers.TaskObjectMother.DEFAULT_CONTACT_LASTNAME
import de.hennihaus.objectmothers.TaskObjectMother.DEFAULT_SCHUFA_DESCRIPTION
import de.hennihaus.objectmothers.TaskObjectMother.DEFAULT_SCHUFA_TITLE
import de.hennihaus.objectmothers.TaskObjectMother.DEFAULT_SYNC_BANK_DESCRIPTION
import de.hennihaus.objectmothers.TaskObjectMother.DEFAULT_SYNC_BANK_TITLE
import java.io.File

object OpenApiObjectMother {

    private val taskSchufaApi: SchufaApi by lazy {
        jacksonObjectMapper().readValue(
            src = File("./src/test/resources/openapi/task-rating.json"),
        )
    }

    private val teamSchufaApi: SchufaApi by lazy {
        jacksonObjectMapper().readValue(
            src = File("./src/test/resources/openapi/team-rating.json"),
        )
    }

    private val taskBankApi: BankApi by lazy {
        jacksonObjectMapper().readValue(
            src = File("./src/test/resources/openapi/task-credit.json"),
        )
    }

    private val teamBankApi: BankApi by lazy {
        jacksonObjectMapper().readValue(
            src = File("./src/test/resources/openapi/team-credit.json")
        )
    }

    private val creditConfigurationBankApi: BankApi by lazy {
        jacksonObjectMapper().readValue(
            src = File("./src/test/resources/openapi/credit-configuration-credit.json")
        )
    }

    fun getNonUpdatedByTaskSchufaApi(): SchufaApi = taskSchufaApi

    fun getNonUpdatedByTeamSchufaApi(): SchufaApi = teamSchufaApi

    fun getNonUpdatedByTaskBankApi(): BankApi = taskBankApi

    fun getNonUpdatedByTeamBankApi(): BankApi = teamBankApi

    fun getNonUpdatedByCreditConfigurationBankApi(): BankApi = creditConfigurationBankApi

    fun getByTaskUpdatedSchufaApi(
        info: SchufaInfo = getByTaskUpdatedSchufaInfo(),
        components: SchufaComponents = getByTaskUpdatedSchufaComponents(),
    ): SchufaApi = taskSchufaApi.copy(
        info = info,
        components = components,
    )

    fun getByTeamUpdatedSchufaApi(
        components: SchufaComponents = getByTeamUpdatedSchufaComponents(),
    ): SchufaApi = teamSchufaApi.copy(
        components = components,
    )

    fun getByTaskUpdatedBankApi(
        info: BankInfo = getByTaskUpdatedBankInfo(),
        components: BankComponents = getByTaskUpdatedBankComponents(),
    ): BankApi = taskBankApi.copy(
        info = info,
        components = components,
    )

    fun getByTeamUpdatedBankApi(
        components: BankComponents = getByTeamUpdatedBankComponents(),
    ): BankApi = teamBankApi.copy(
        components = components,
    )

    fun getByCreditConfigurationUpdatedBankApi(
        components: BankComponents = getByCreditConfigurationBankComponents(),
    ): BankApi = creditConfigurationBankApi.copy(
        components = components,
    )

    fun getByTaskUpdatedSchufaInfo(
        title: String = "$DEFAULT_TITLE - $DEFAULT_SCHUFA_TITLE",
        contact: SchufaContact = getByTaskUpdatedSchufaContact(),
        description: String = DEFAULT_SCHUFA_DESCRIPTION,
    ): SchufaInfo = taskSchufaApi.info.copy(
        title = title,
        contact = contact,
        description = description,
    )

    fun getByTaskUpdatedBankInfo(
        title: String = "$DEFAULT_TITLE - $DEFAULT_SYNC_BANK_TITLE",
        contact: BankContact = getByTaskUpdatedBankContact(),
        description: String = DEFAULT_SYNC_BANK_DESCRIPTION,
    ): BankInfo = taskBankApi.info.copy(
        title = title,
        contact = contact,
        description = description,
    )

    fun getByTaskUpdatedSchufaComponents(
        parameters: LinkedHashMap<String, SchufaParameter> = getByTaskUpdatedSchufaParameters(),
        responses: LinkedHashMap<String, SchufaResponse> = getByTaskUpdatedSchufaResponses(),
    ): SchufaComponents = taskSchufaApi.components.copy(
        parameters = parameters,
        responses = responses,
    )

    fun getByTeamUpdatedSchufaComponents(
        parameters: LinkedHashMap<String, SchufaParameter> = getByTeamUpdatedSchufaParameters(),
    ): SchufaComponents = teamSchufaApi.components.copy(
        parameters = parameters,
    )

    fun getByTaskUpdatedBankComponents(
        parameters: LinkedHashMap<String, BankParameter> = getByTaskUpdatedBankParameters(),
        responses: LinkedHashMap<String, BankResponse> = getByTaskUpdatedBankResponses(),
    ): BankComponents = taskBankApi.components.copy(
        parameters = parameters,
        responses = responses,
    )

    fun getByTeamUpdatedBankComponents(
        parameters: LinkedHashMap<String, BankParameter> = getByTeamUpdatedBankParameters(),
    ): BankComponents = teamBankApi.components.copy(
        parameters = parameters,
    )

    fun getByCreditConfigurationBankComponents(
        parameters: LinkedHashMap<String, BankParameter> = getByCreditConfigurationUpdatedBankParameters(),
    ): BankComponents = creditConfigurationBankApi.components.copy(
        parameters = parameters,
    )

    fun getByTaskUpdatedSchufaContact(
        name: String = "$DEFAULT_CONTACT_FIRSTNAME $DEFAULT_CONTACT_LASTNAME",
        email: String = DEFAULT_CONTACT_EMAIL,
    ) = SchufaContact(
        name = name,
        email = email,
    )

    fun getByTaskUpdatedBankContact(
        name: String = "$DEFAULT_CONTACT_FIRSTNAME $DEFAULT_CONTACT_LASTNAME",
        email: String = DEFAULT_CONTACT_EMAIL,
    ) = BankContact(
        name = name,
        email = email,
    )

    fun getByTaskUpdatedSocialSecuritySchufaParameter(
        description: String = SOCIAL_SECURITY_NUMBER_DESCRIPTION,
        example: String = SOCIAL_SECURITY_NUMBER_EXAMPLE,
    ): SchufaParameter = taskSchufaApi.components.parameters[SOCIAL_SECURITY_NUMBER_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getByTaskUpdatedRatingLevelSchufaParameter(
        description: String = RATING_LEVEL_DESCRIPTION,
        example: String = RATING_LEVEL_EXAMPLE,
    ): SchufaParameter = taskSchufaApi.components.parameters[RATING_LEVEL_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getByTaskUpdatedDelayInMillisecondsSchufaParameter(
        description: String = DELAY_IN_MILLISECONDS_DESCRIPTION,
        example: String = DELAY_IN_MILLISECONDS_EXAMPLE,
    ): SchufaParameter = taskSchufaApi.components.parameters[DELAY_IN_MILLISECONDS_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getByTaskUpdatedUsernameSchufaParameter(
        description: String = USERNAME_DESCRIPTION,
        example: String = USERNAME_EXAMPLE,
    ): SchufaParameter = taskSchufaApi.components.parameters[USERNAME_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getByTeamUpdatedUsernameSchufaParameter(
        example: String = USERNAME_EXAMPLE,
    ): SchufaParameter = teamSchufaApi.components.parameters[USERNAME_PARAMETER]!!.copy(
        example = example,
    )

    fun getByTaskUpdatedPasswordSchufaParameter(
        description: String = PASSWORD_DESCRIPTION,
        example: String = PASSWORD_EXAMPLE,
    ): SchufaParameter = taskSchufaApi.components.parameters[PASSWORD_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getByTeamUpdatedPasswordSchufaParameter(
        example: String = PASSWORD_EXAMPLE,
    ): SchufaParameter = teamSchufaApi.components.parameters[PASSWORD_PARAMETER]!!.copy(
        example = example,
    )

    fun getByTaskUpdatedAmountInEurosBankParameter(
        description: String = AMOUNT_IN_EUROS_DESCRIPTION,
        example: String = AMOUNT_IN_EUROS_EXAMPLE,
    ): BankParameter = taskBankApi.components.parameters[AMOUNT_IN_EUROS_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getByCreditConfigurationUpdatedAmountInEurosParameter(
        example: String = AMOUNT_IN_EUROS_EXAMPLE,
        schema: BankSchema = getByCreditConfigurationUpdatedAmountInEurosBankSchema(),
    ): BankParameter = creditConfigurationBankApi.components.parameters[AMOUNT_IN_EUROS_PARAMETER]!!.copy(
        example = example,
        schema = schema,
    )

    fun getByTaskUpdatedTermInMonthsBankParameter(
        description: String = TERM_IN_MONTHS_DESCRIPTION,
        example: String = TERM_IN_MONTHS_EXAMPLE,
    ): BankParameter = taskBankApi.components.parameters[TERM_IN_MONTHS_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getByCreditConfigurationUpdatedTermInMonthsParameter(
        example: String = TERM_IN_MONTHS_EXAMPLE,
        schema: BankSchema = getByCreditConfigurationUpdatedTermInMonthsBankSchema(),
    ): BankParameter = creditConfigurationBankApi.components.parameters[TERM_IN_MONTHS_PARAMETER]!!.copy(
        example = example,
        schema = schema,
    )

    fun getByTaskUpdatedRatingLevelBankParameter(
        description: String = RATING_LEVEL_DESCRIPTION,
        example: String = RATING_LEVEL_EXAMPLE,
    ): BankParameter = taskBankApi.components.parameters[RATING_LEVEL_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getByTaskUpdatedDelayInMillisecondsBankParameter(
        description: String = DELAY_IN_MILLISECONDS_DESCRIPTION,
        example: String = DELAY_IN_MILLISECONDS_EXAMPLE,
    ): BankParameter = taskBankApi.components.parameters[DELAY_IN_MILLISECONDS_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getByTaskUpdatedUsernameBankParameter(
        description: String = USERNAME_DESCRIPTION,
        example: String = USERNAME_EXAMPLE,
    ): BankParameter = taskBankApi.components.parameters[USERNAME_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getByTeamUpdatedUsernameBankParameter(
        example: String = USERNAME_EXAMPLE,
    ): BankParameter = teamBankApi.components.parameters[USERNAME_PARAMETER]!!.copy(
        example = example,
    )

    fun getByTaskUpdatedPasswordBankParameter(
        description: String = PASSWORD_DESCRIPTION,
        example: String = PASSWORD_EXAMPLE,
    ): BankParameter = taskBankApi.components.parameters[PASSWORD_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getByTeamUpdatedPasswordBankParameter(
        example: String = PASSWORD_EXAMPLE,
    ): BankParameter = teamBankApi.components.parameters[PASSWORD_PARAMETER]!!.copy(
        example = example,
    )

    fun getByCreditConfigurationUpdatedAmountInEurosBankSchema(
        minAmountInEuros: Int = DEFAULT_MIN_AMOUNT_IN_EUROS,
        maxAmountInEuros: Int = DEFAULT_MAX_AMOUNT_IN_EUROS,
    ): BankSchema = creditConfigurationBankApi.components.parameters[AMOUNT_IN_EUROS_PARAMETER]!!.schema.copy(
        minimum = minAmountInEuros,
        maximum = maxAmountInEuros,
    )

    fun getByCreditConfigurationUpdatedTermInMonthsBankSchema(
        minTermInMonths: Int = DEFAULT_MIN_TERM_IN_MONTHS,
        maxTermInMonths: Int = DEFAULT_MAX_TERM_IN_MONTHS,
    ): BankSchema = creditConfigurationBankApi.components.parameters[TERM_IN_MONTHS_PARAMETER]!!.schema.copy(
        minimum = minTermInMonths,
        maximum = maxTermInMonths,
    )

    fun getUpdatedOkSchufaResponse(
        description: String = SCHUFA_OK_DESCRIPTION,
    ): SchufaResponse = taskSchufaApi.components.responses[OK_CODE]!!.copy(
        description = description,
    )

    fun getByTaskUpdatedBadRequestSchufaResponse(
        description: String = BAD_REQUEST_DESCRIPTION,
    ): SchufaResponse = taskSchufaApi.components.responses[BAD_REQUEST_CODE]!!.copy(
        description = description,
    )

    fun getByTaskUpdatedNotFoundSchufaResponse(
        description: String = NOT_FOUND_DESCRIPTION,
    ): SchufaResponse = taskSchufaApi.components.responses[NOT_FOUND_CODE]!!.copy(
        description = description,
    )

    fun getByTaskUpdatedInternalServerErrorSchufaResponse(
        description: String = INTERNAL_SERVER_ERROR_DESCRIPTION,
    ): SchufaResponse = taskSchufaApi.components.responses[INTERNAL_SERVER_ERROR_CODE]!!.copy(
        description = description,
    )

    fun getByTaskUpdatedOkBankResponse(
        description: String = BANK_OK_DESCRIPTION,
    ): BankResponse = taskBankApi.components.responses[OK_CODE]!!.copy(
        description = description,
    )

    fun getByTaskUpdatedBadRequestBankResponse(
        description: String = BAD_REQUEST_DESCRIPTION,
    ): BankResponse = taskBankApi.components.responses[BAD_REQUEST_CODE]!!.copy(
        description = description,
    )

    fun getByTaskUpdatedNotFoundBankResponse(
        description: String = NOT_FOUND_DESCRIPTION,
    ): BankResponse = taskBankApi.components.responses[NOT_FOUND_CODE]!!.copy(
        description = description,
    )

    fun getByTaskUpdatedInternalServerErrorBankResponse(
        description: String = INTERNAL_SERVER_ERROR_DESCRIPTION,
    ): BankResponse = taskBankApi.components.responses[INTERNAL_SERVER_ERROR_CODE]!!.copy(
        description = description,
    )

    private fun getByTaskUpdatedSchufaParameters(): LinkedHashMap<String, SchufaParameter> = linkedMapOf(
        SOCIAL_SECURITY_NUMBER_PARAMETER to getByTaskUpdatedSocialSecuritySchufaParameter(),
        RATING_LEVEL_PARAMETER to getByTaskUpdatedRatingLevelSchufaParameter(),
        DELAY_IN_MILLISECONDS_PARAMETER to getByTaskUpdatedDelayInMillisecondsSchufaParameter(),
        USERNAME_PARAMETER to getByTaskUpdatedUsernameSchufaParameter(),
        PASSWORD_PARAMETER to getByTaskUpdatedPasswordSchufaParameter(),
    )

    private fun getByTeamUpdatedSchufaParameters(): LinkedHashMap<String, SchufaParameter> = linkedMapOf(
        SOCIAL_SECURITY_NUMBER_PARAMETER to teamSchufaApi.components.parameters[SOCIAL_SECURITY_NUMBER_PARAMETER]!!,
        RATING_LEVEL_PARAMETER to teamSchufaApi.components.parameters[RATING_LEVEL_PARAMETER]!!,
        DELAY_IN_MILLISECONDS_PARAMETER to teamSchufaApi.components.parameters[DELAY_IN_MILLISECONDS_PARAMETER]!!,
        USERNAME_PARAMETER to getByTeamUpdatedUsernameSchufaParameter(),
        PASSWORD_PARAMETER to getByTeamUpdatedPasswordSchufaParameter(),
    )

    private fun getByTaskUpdatedBankParameters(): LinkedHashMap<String, BankParameter> = linkedMapOf(
        AMOUNT_IN_EUROS_PARAMETER to getByTaskUpdatedAmountInEurosBankParameter(),
        TERM_IN_MONTHS_PARAMETER to getByTaskUpdatedTermInMonthsBankParameter(),
        RATING_LEVEL_PARAMETER to getByTaskUpdatedRatingLevelBankParameter(),
        DELAY_IN_MILLISECONDS_PARAMETER to getByTaskUpdatedDelayInMillisecondsBankParameter(),
        USERNAME_PARAMETER to getByTaskUpdatedUsernameBankParameter(),
        PASSWORD_PARAMETER to getByTaskUpdatedPasswordBankParameter(),
    )

    private fun getByTeamUpdatedBankParameters(): LinkedHashMap<String, BankParameter> = linkedMapOf(
        AMOUNT_IN_EUROS_PARAMETER to teamBankApi.components.parameters[AMOUNT_IN_EUROS_PARAMETER]!!,
        TERM_IN_MONTHS_PARAMETER to teamBankApi.components.parameters[TERM_IN_MONTHS_PARAMETER]!!,
        RATING_LEVEL_PARAMETER to teamBankApi.components.parameters[RATING_LEVEL_PARAMETER]!!,
        DELAY_IN_MILLISECONDS_PARAMETER to teamBankApi.components.parameters[DELAY_IN_MILLISECONDS_PARAMETER]!!,
        USERNAME_PARAMETER to getByTeamUpdatedUsernameBankParameter(),
        PASSWORD_PARAMETER to getByTeamUpdatedPasswordBankParameter(),
    )

    private fun getByCreditConfigurationUpdatedBankParameters(): LinkedHashMap<String, BankParameter> = linkedMapOf(
        AMOUNT_IN_EUROS_PARAMETER to getByCreditConfigurationUpdatedAmountInEurosParameter(),
        TERM_IN_MONTHS_PARAMETER to getByCreditConfigurationUpdatedTermInMonthsParameter(),
        RATING_LEVEL_PARAMETER to creditConfigurationBankApi.components.parameters[RATING_LEVEL_PARAMETER]!!,
        DELAY_IN_MILLISECONDS_PARAMETER to creditConfigurationBankApi.components.parameters[DELAY_IN_MILLISECONDS_PARAMETER]!!,
        USERNAME_PARAMETER to creditConfigurationBankApi.components.parameters[USERNAME_PARAMETER]!!,
        PASSWORD_PARAMETER to creditConfigurationBankApi.components.parameters[PASSWORD_PARAMETER]!!,
    )

    private fun getByTaskUpdatedSchufaResponses(): LinkedHashMap<String, SchufaResponse> = linkedMapOf(
        OK_CODE to getUpdatedOkSchufaResponse(),
        BAD_REQUEST_CODE to getByTaskUpdatedBadRequestSchufaResponse(),
        NOT_FOUND_CODE to getByTaskUpdatedNotFoundSchufaResponse(),
        INTERNAL_SERVER_ERROR_CODE to getByTaskUpdatedInternalServerErrorSchufaResponse(),
    )

    private fun getByTaskUpdatedBankResponses(): LinkedHashMap<String, BankResponse> = linkedMapOf(
        OK_CODE to getByTaskUpdatedOkBankResponse(),
        BAD_REQUEST_CODE to getByTaskUpdatedBadRequestBankResponse(),
        NOT_FOUND_CODE to getByTaskUpdatedNotFoundBankResponse(),
        INTERNAL_SERVER_ERROR_CODE to getByTaskUpdatedInternalServerErrorBankResponse(),
    )
}
