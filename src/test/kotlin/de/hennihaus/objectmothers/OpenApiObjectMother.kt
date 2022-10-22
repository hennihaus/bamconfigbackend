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

    private val schufaApi: SchufaApi by lazy {
        jacksonObjectMapper().readValue(
            src = File("./src/test/resources/openapi/rating.json"),
        )
    }

    private val bankApi: BankApi by lazy {
        jacksonObjectMapper().readValue(
            src = File("./src/test/resources/openapi/credit.json"),
        )
    }

    fun getNonUpdatedSchufaApi(): SchufaApi = schufaApi

    fun getNonUpdatedBankApi(): BankApi = bankApi

    fun getUpdatedSchufaApi(
        info: SchufaInfo = getUpdatedSchufaInfo(),
        components: SchufaComponents = getUpdatedSchufaComponents(),
    ): SchufaApi = schufaApi.copy(
        info = info,
        components = components,
    )

    fun getUpdatedBankApi(
        info: BankInfo = getUpdatedBankInfo(),
        components: BankComponents = getUpdatedBankComponents(),
    ): BankApi = bankApi.copy(
        info = info,
        components = components,
    )

    fun getUpdatedSchufaInfo(
        title: String = "$DEFAULT_TITLE - $DEFAULT_SCHUFA_TITLE",
        contact: SchufaContact = getUpdatedSchufaContact(),
        description: String = DEFAULT_SCHUFA_DESCRIPTION,
    ): SchufaInfo = schufaApi.info.copy(
        title = title,
        contact = contact,
        description = description,
    )

    fun getUpdatedBankInfo(
        title: String = "$DEFAULT_TITLE - $DEFAULT_SYNC_BANK_TITLE",
        contact: BankContact = getUpdatedBankContact(),
        description: String = DEFAULT_SYNC_BANK_DESCRIPTION,
    ): BankInfo = bankApi.info.copy(
        title = title,
        contact = contact,
        description = description,
    )

    fun getUpdatedSchufaComponents(
        parameters: LinkedHashMap<String, SchufaParameter> = getUpdatedSchufaParameters(),
        responses: LinkedHashMap<String, SchufaResponse> = getUpdatedSchufaResponses(),
    ): SchufaComponents = schufaApi.components.copy(
        parameters = parameters,
        responses = responses,
    )

    fun getUpdatedBankComponents(
        parameters: LinkedHashMap<String, BankParameter> = getUpdatedBankParameters(),
        responses: LinkedHashMap<String, BankResponse> = getUpdatedBankResponses(),
    ): BankComponents = bankApi.components.copy(
        parameters = parameters,
        responses = responses,
    )

    fun getUpdatedSchufaContact(
        name: String = "$DEFAULT_CONTACT_FIRSTNAME $DEFAULT_CONTACT_LASTNAME",
        email: String = DEFAULT_CONTACT_EMAIL,
    ) = SchufaContact(
        name = name,
        email = email,
    )

    fun getUpdatedBankContact(
        name: String = "$DEFAULT_CONTACT_FIRSTNAME $DEFAULT_CONTACT_LASTNAME",
        email: String = DEFAULT_CONTACT_EMAIL,
    ) = BankContact(
        name = name,
        email = email,
    )

    fun getUpdatedSocialSecuritySchufaParameter(
        description: String = SOCIAL_SECURITY_NUMBER_DESCRIPTION,
        example: String = SOCIAL_SECURITY_NUMBER_EXAMPLE,
    ): SchufaParameter = schufaApi.components.parameters[SOCIAL_SECURITY_NUMBER_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getUpdatedRatingLevelSchufaParameter(
        description: String = RATING_LEVEL_DESCRIPTION,
        example: String = RATING_LEVEL_EXAMPLE,
    ): SchufaParameter = schufaApi.components.parameters[RATING_LEVEL_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getUpdatedDelayInMillisecondsSchufaParameter(
        description: String = DELAY_IN_MILLISECONDS_DESCRIPTION,
        example: String = DELAY_IN_MILLISECONDS_EXAMPLE,
    ): SchufaParameter = schufaApi.components.parameters[DELAY_IN_MILLISECONDS_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getUpdatedUsernameSchufaParameter(
        description: String = USERNAME_DESCRIPTION,
        example: String = USERNAME_EXAMPLE,
    ): SchufaParameter = schufaApi.components.parameters[USERNAME_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getUpdatedPasswordSchufaParameter(
        description: String = PASSWORD_DESCRIPTION,
        example: String = PASSWORD_EXAMPLE,
    ): SchufaParameter = schufaApi.components.parameters[PASSWORD_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getUpdatedAmountInEurosBankParameter(
        description: String = AMOUNT_IN_EUROS_DESCRIPTION,
        example: String = AMOUNT_IN_EUROS_EXAMPLE,
        schema: BankSchema = getUpdatedAmountInEurosBankSchema(),
    ): BankParameter = bankApi.components.parameters[AMOUNT_IN_EUROS_PARAMETER]!!.copy(
        description = description,
        example = example,
        schema = schema,
    )

    fun getUpdatedTermInMonthsBankParameter(
        description: String = TERM_IN_MONTHS_DESCRIPTION,
        example: String = TERM_IN_MONTHS_EXAMPLE,
        schema: BankSchema = getUpdatedTermInMonthsBankSchema(),
    ): BankParameter = bankApi.components.parameters[TERM_IN_MONTHS_PARAMETER]!!.copy(
        description = description,
        example = example,
        schema = schema,
    )

    fun getUpdatedRatingLevelBankParameter(
        description: String = RATING_LEVEL_DESCRIPTION,
        example: String = RATING_LEVEL_EXAMPLE,
    ): BankParameter = bankApi.components.parameters[RATING_LEVEL_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getUpdatedDelayInMillisecondsBankParameter(
        description: String = DELAY_IN_MILLISECONDS_DESCRIPTION,
        example: String = DELAY_IN_MILLISECONDS_EXAMPLE,
    ): BankParameter = bankApi.components.parameters[DELAY_IN_MILLISECONDS_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getUpdatedUsernameBankParameter(
        description: String = USERNAME_DESCRIPTION,
        example: String = USERNAME_EXAMPLE,
    ): BankParameter = bankApi.components.parameters[USERNAME_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getUpdatedPasswordBankParameter(
        description: String = PASSWORD_DESCRIPTION,
        example: String = PASSWORD_EXAMPLE,
    ): BankParameter = bankApi.components.parameters[PASSWORD_PARAMETER]!!.copy(
        description = description,
        example = example,
    )

    fun getUpdatedAmountInEurosBankSchema(
        minAmountInEuros: Int = DEFAULT_MIN_AMOUNT_IN_EUROS,
        maxAmountInEuros: Int = DEFAULT_MAX_AMOUNT_IN_EUROS,
    ): BankSchema = bankApi.components.parameters[AMOUNT_IN_EUROS_PARAMETER]!!.schema.copy(
        minimum = minAmountInEuros,
        maximum = maxAmountInEuros,
    )

    fun getUpdatedTermInMonthsBankSchema(
        minTermInMonths: Int = DEFAULT_MIN_TERM_IN_MONTHS,
        maxTermInMonths: Int = DEFAULT_MAX_TERM_IN_MONTHS,
    ): BankSchema = bankApi.components.parameters[TERM_IN_MONTHS_PARAMETER]!!.schema.copy(
        minimum = minTermInMonths,
        maximum = maxTermInMonths,
    )

    fun getUpdatedOkSchufaResponse(
        description: String = SCHUFA_OK_DESCRIPTION,
    ): SchufaResponse = schufaApi.components.responses[OK_CODE]!!.copy(
        description = description,
    )

    fun getUpdatedBadRequestSchufaResponse(
        description: String = BAD_REQUEST_DESCRIPTION,
    ): SchufaResponse = schufaApi.components.responses[BAD_REQUEST_CODE]!!.copy(
        description = description,
    )

    fun getUpdatedNotFoundSchufaResponse(
        description: String = NOT_FOUND_DESCRIPTION,
    ): SchufaResponse = schufaApi.components.responses[NOT_FOUND_CODE]!!.copy(
        description = description,
    )

    fun getUpdatedInternalServerErrorSchufaResponse(
        description: String = INTERNAL_SERVER_ERROR_DESCRIPTION,
    ): SchufaResponse = schufaApi.components.responses[INTERNAL_SERVER_ERROR_CODE]!!.copy(
        description = description,
    )

    fun getUpdatedOkBankResponse(
        description: String = BANK_OK_DESCRIPTION,
    ): BankResponse = bankApi.components.responses[OK_CODE]!!.copy(
        description = description,
    )

    fun getUpdatedBadRequestBankResponse(
        description: String = BAD_REQUEST_DESCRIPTION,
    ): BankResponse = bankApi.components.responses[BAD_REQUEST_CODE]!!.copy(
        description = description,
    )

    fun getUpdatedNotFoundBankResponse(
        description: String = NOT_FOUND_DESCRIPTION,
    ): BankResponse = bankApi.components.responses[NOT_FOUND_CODE]!!.copy(
        description = description,
    )

    fun getUpdatedInternalServerErrorBankResponse(
        description: String = INTERNAL_SERVER_ERROR_DESCRIPTION,
    ): BankResponse = bankApi.components.responses[INTERNAL_SERVER_ERROR_CODE]!!.copy(
        description = description,
    )

    private fun getUpdatedSchufaParameters(): LinkedHashMap<String, SchufaParameter> = linkedMapOf(
        SOCIAL_SECURITY_NUMBER_PARAMETER to getUpdatedSocialSecuritySchufaParameter(),
        RATING_LEVEL_PARAMETER to getUpdatedRatingLevelSchufaParameter(),
        DELAY_IN_MILLISECONDS_PARAMETER to getUpdatedDelayInMillisecondsSchufaParameter(),
        USERNAME_PARAMETER to getUpdatedUsernameSchufaParameter(),
        PASSWORD_PARAMETER to getUpdatedPasswordSchufaParameter(),
    )

    private fun getUpdatedBankParameters(): LinkedHashMap<String, BankParameter> = linkedMapOf(
        AMOUNT_IN_EUROS_PARAMETER to getUpdatedAmountInEurosBankParameter(),
        TERM_IN_MONTHS_PARAMETER to getUpdatedTermInMonthsBankParameter(),
        RATING_LEVEL_PARAMETER to getUpdatedRatingLevelBankParameter(),
        DELAY_IN_MILLISECONDS_PARAMETER to getUpdatedDelayInMillisecondsBankParameter(),
        USERNAME_PARAMETER to getUpdatedUsernameBankParameter(),
        PASSWORD_PARAMETER to getUpdatedPasswordBankParameter(),
    )

    private fun getUpdatedSchufaResponses(): LinkedHashMap<String, SchufaResponse> = linkedMapOf(
        OK_CODE to getUpdatedOkSchufaResponse(),
        BAD_REQUEST_CODE to getUpdatedBadRequestSchufaResponse(),
        NOT_FOUND_CODE to getUpdatedNotFoundSchufaResponse(),
        INTERNAL_SERVER_ERROR_CODE to getUpdatedInternalServerErrorSchufaResponse(),
    )

    private fun getUpdatedBankResponses(): LinkedHashMap<String, BankResponse> = linkedMapOf(
        OK_CODE to getUpdatedOkBankResponse(),
        BAD_REQUEST_CODE to getUpdatedBadRequestBankResponse(),
        NOT_FOUND_CODE to getUpdatedNotFoundBankResponse(),
        INTERNAL_SERVER_ERROR_CODE to getUpdatedInternalServerErrorBankResponse(),
    )
}
