package de.hennihaus.objectmothers

import de.hennihaus.bamdatamodel.Bank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getAsyncBank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getSchufaBank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getSyncBank
import de.hennihaus.models.Contact
import de.hennihaus.models.Endpoint
import de.hennihaus.models.IntegrationStep
import de.hennihaus.models.Parameter
import de.hennihaus.models.Response
import de.hennihaus.models.Task
import de.hennihaus.objectmothers.EndpointObjectMother.getActiveMqEndpoint
import de.hennihaus.objectmothers.EndpointObjectMother.getSchufaRestEndpoint
import de.hennihaus.objectmothers.EndpointObjectMother.getVBankRestEndpoint
import de.hennihaus.objectmothers.ParameterObjectMother.getAmountInEurosParameter
import de.hennihaus.objectmothers.ParameterObjectMother.getDelayInMillisecondsParameter
import de.hennihaus.objectmothers.ParameterObjectMother.getPasswordParameter
import de.hennihaus.objectmothers.ParameterObjectMother.getRatingLevelParameter
import de.hennihaus.objectmothers.ParameterObjectMother.getRequestIdParameter
import de.hennihaus.objectmothers.ParameterObjectMother.getSocialSecurityNumberParameter
import de.hennihaus.objectmothers.ParameterObjectMother.getTermInMonthsParameter
import de.hennihaus.objectmothers.ParameterObjectMother.getUsernameParameter
import de.hennihaus.objectmothers.ResponseObjectMother.getBadRequestResponse
import de.hennihaus.objectmothers.ResponseObjectMother.getBankOkResponse
import de.hennihaus.objectmothers.ResponseObjectMother.getInternalServerErrorResponse
import de.hennihaus.objectmothers.ResponseObjectMother.getJmsResponse
import de.hennihaus.objectmothers.ResponseObjectMother.getNotFoundResponse
import de.hennihaus.objectmothers.ResponseObjectMother.getSchufaOkResponse
import java.util.UUID

object TaskObjectMother {

    const val DEFAULT_SCHUFA_UUID = "72255554-d295-4684-9ff8-8d262849bb3d"
    const val DEFAULT_SCHUFA_TITLE = "Schufa-Auskunft"
    const val DEFAULT_SCHUFA_DESCRIPTION = "<p>Schufa-Auskunft-Beschreibung (\"1. Integrationsschritt\")</p>"

    const val DEFAULT_SYNC_BANK_UUID = "e2aba7e5-9bb8-458b-a927-8e672b1370f2"
    const val DEFAULT_SYNC_BANK_TITLE = "Deutsche Bank"
    const val DEFAULT_SYNC_BANK_DESCRIPTION = "<p>Synchrone-Bank-Beschreibung (\"2. Integrationsschritt\")</p>"

    const val DEFAULT_ASYNC_BANK_UUID = "4ff1f9cb-e65d-4c8f-908a-d036700b757e"
    const val DEFAULT_ASYNC_BANK_TITLE = "Asynchrone Banken"
    const val DEFAULT_ASYNC_BANK_DESCRIPTION = "<p>Asynchrone-Bank-Beschreibung (\"3. Integrationsschritt\")</p>"

    const val DEFAULT_CONTACT_UUID = "ba16bd92-9e64-4de6-916f-d0c4ea91a530"
    const val DEFAULT_CONTACT_FIRSTNAME = "Jan-Hendrik"
    const val DEFAULT_CONTACT_LASTNAME = "Hausner"
    const val DEFAULT_CONTACT_EMAIL = "jan-hendrik.hausner@outlook.com"

    fun getSchufaTask(
        uuid: UUID = UUID.fromString(DEFAULT_SCHUFA_UUID),
        title: String = DEFAULT_SCHUFA_TITLE,
        description: String = DEFAULT_SCHUFA_DESCRIPTION,
        integrationStep: IntegrationStep = IntegrationStep.SCHUFA_STEP,
        isOpenApiVerbose: Boolean = true,
        contact: Contact = getDefaultContact(),
        endpoints: List<Endpoint> = getSchufaEndpoints(),
        parameters: List<Parameter> = getSchufaParameters(),
        responses: List<Response> = getSchufaResponses(),
        banks: List<Bank> = getSchufaBanks(),
    ) = Task(
        uuid = uuid,
        title = title,
        description = description,
        integrationStep = integrationStep,
        isOpenApiVerbose = isOpenApiVerbose,
        contact = contact,
        endpoints = endpoints,
        parameters = parameters,
        responses = responses,
        banks = banks,
    )

    fun getSynchronousBankTask(
        uuid: UUID = UUID.fromString(DEFAULT_SYNC_BANK_UUID),
        title: String = DEFAULT_SYNC_BANK_TITLE,
        description: String = DEFAULT_SYNC_BANK_DESCRIPTION,
        integrationStep: IntegrationStep = IntegrationStep.SYNC_BANK_STEP,
        isOpenApiVerbose: Boolean = true,
        contact: Contact = getDefaultContact(),
        endpoints: List<Endpoint> = getSynchronousBankEndpoints(),
        parameters: List<Parameter> = getSynchronousBankParameters(),
        responses: List<Response> = getSynchronousBankResponses(),
        banks: List<Bank> = getSynchronousBanks(),
    ) = Task(
        uuid = uuid,
        title = title,
        description = description,
        integrationStep = integrationStep,
        isOpenApiVerbose = isOpenApiVerbose,
        contact = contact,
        endpoints = endpoints,
        parameters = parameters,
        responses = responses,
        banks = banks,
    )

    fun getAsynchronousBankTask(
        uuid: UUID = UUID.fromString(DEFAULT_ASYNC_BANK_UUID),
        title: String = DEFAULT_ASYNC_BANK_TITLE,
        description: String = DEFAULT_ASYNC_BANK_DESCRIPTION,
        integrationStep: IntegrationStep = IntegrationStep.ASYNC_BANK_STEP,
        isOpenApiVerbose: Boolean = false,
        contact: Contact = getDefaultContact(),
        endpoints: List<Endpoint> = getAsynchronousBankEndpoints(),
        parameters: List<Parameter> = getAsynchronousBankParameters(),
        responses: List<Response> = getAsynchronousBankResponses(),
        banks: List<Bank> = getAsynchronousBanks(),
    ) = Task(
        uuid = uuid,
        title = title,
        description = description,
        integrationStep = integrationStep,
        isOpenApiVerbose = isOpenApiVerbose,
        contact = contact,
        endpoints = endpoints,
        parameters = parameters,
        responses = responses,
        banks = banks,
    )

    fun getDefaultContact(
        uuid: UUID = UUID.fromString(DEFAULT_CONTACT_UUID),
        firstname: String = DEFAULT_CONTACT_FIRSTNAME,
        lastname: String = DEFAULT_CONTACT_LASTNAME,
        email: String = DEFAULT_CONTACT_EMAIL,
    ) = Contact(
        uuid = uuid,
        firstname = firstname,
        lastname = lastname,
        email = email,
    )

    private fun getSchufaEndpoints(): List<Endpoint> = listOf(
        getSchufaRestEndpoint(),
    )

    private fun getSchufaParameters(): List<Parameter> = listOf(
        getSocialSecurityNumberParameter(),
        getRatingLevelParameter(),
        getDelayInMillisecondsParameter(),
        getUsernameParameter(),
        getPasswordParameter(),
    )

    private fun getSchufaResponses(): List<Response> = listOf(
        getSchufaOkResponse(),
        getBadRequestResponse(),
        getNotFoundResponse(),
        getInternalServerErrorResponse(),
    )

    private fun getSchufaBanks(): List<Bank> = listOf(
        getSchufaBank(),
    )

    private fun getSynchronousBankEndpoints(): List<Endpoint> = listOf(
        getVBankRestEndpoint(),
    )

    private fun getSynchronousBankParameters(): List<Parameter> = listOf(
        getAmountInEurosParameter(),
        getTermInMonthsParameter(),
        getRatingLevelParameter(),
        getDelayInMillisecondsParameter(),
        getUsernameParameter(),
        getPasswordParameter(),
    )

    private fun getSynchronousBankResponses(): List<Response> = listOf(
        getBankOkResponse(),
        getBadRequestResponse(),
        getNotFoundResponse(),
        getInternalServerErrorResponse(),
    )

    private fun getSynchronousBanks(): List<Bank> = listOf(
        getSyncBank(),
    )

    private fun getAsynchronousBankEndpoints(): List<Endpoint> = listOf(
        getActiveMqEndpoint(),
    )

    private fun getAsynchronousBankParameters(): List<Parameter> = listOf(
        getRequestIdParameter(),
        getAmountInEurosParameter(),
        getTermInMonthsParameter(),
        getRatingLevelParameter(),
        getDelayInMillisecondsParameter(),
        getUsernameParameter(),
        getPasswordParameter(),
    )

    private fun getAsynchronousBankResponses(): List<Response> = listOf(
        getJmsResponse(),
    )

    private fun getAsynchronousBanks(): List<Bank> = listOf(
        getAsyncBank(),
    )
}
