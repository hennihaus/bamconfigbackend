package de.hennihaus.objectmothers

import de.hennihaus.models.Bank
import de.hennihaus.models.Contact
import de.hennihaus.models.Endpoint
import de.hennihaus.models.Parameter
import de.hennihaus.models.Response
import de.hennihaus.models.Task
import de.hennihaus.objectmothers.BankObjectMother.getJmsBank
import de.hennihaus.objectmothers.BankObjectMother.getSchufaBank
import de.hennihaus.objectmothers.BankObjectMother.getVBank
import de.hennihaus.objectmothers.EndpointObjectMother.getActiveMqEndpoint
import de.hennihaus.objectmothers.EndpointObjectMother.getSchufaRestEndpoint
import de.hennihaus.objectmothers.EndpointObjectMother.getSchufaSoapEndpoint
import de.hennihaus.objectmothers.EndpointObjectMother.getVBankRestEndpoint
import de.hennihaus.objectmothers.EndpointObjectMother.getVBankSoapEndpoint
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
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

object TaskObjectMother {

    const val DEFAULT_SCHUFA_TITLE = "Schufa-Auskunft"
    const val DEFAULT_SCHUFA_DESCRIPTION = "<p>Schufa-Auskunft-Beschreibung (\"1. Integrationsschritt\")</p>"

    const val DEFAULT_SYNC_BANK_TITLE = "Synchrone Bank"
    const val DEFAULT_SYNC_BANK_DESCRIPTION = "<p>Synchrone-Bank-Beschreibung (\"2. Integrationsschritt\")</p>"

    const val DEFAULT_ASYNC_BANK_TITLE = "Asynchrone Banken"
    const val DEFAULT_ASYNC_BANK_DESCRIPTION = "<p>Asynchrone-Bank-Beschreibung (\"3. Integrationsschritt\")</p>"

    const val DEFAULT_CONTACT_NAME = "Jan-Hendrik Hausner"
    const val DEFAULT_CONTACT_EMAIL = "jan-hendrik.hausner@outlook.com"

    fun getSchufaTask(
        id: Id<Task> = ObjectId("6150281b8031b5b4e70a881e").toId(),
        title: String = DEFAULT_SCHUFA_TITLE,
        description: String = DEFAULT_SCHUFA_DESCRIPTION,
        step: Int = 1,
        isOpenApiVerbose: Boolean = true,
        contact: Contact = getDefaultContact(),
        endpoints: List<Endpoint> = getSchufaEndpoints(),
        parameters: List<Parameter> = getSchufaParameters(),
        responses: List<Response> = getSchufaResponses(),
        banks: List<Bank> = getSchufaBanks(),
    ) = Task(
        id = id,
        title = title,
        description = description,
        step = step,
        isOpenApiVerbose = isOpenApiVerbose,
        contact = contact,
        endpoints = endpoints,
        parameters = parameters,
        responses = responses,
        banks = banks,
    )

    fun getSynchronousBankTask(
        id: Id<Task> = ObjectId("61502fa9afad97203db562b3").toId(),
        title: String = DEFAULT_SYNC_BANK_TITLE,
        description: String = DEFAULT_SYNC_BANK_DESCRIPTION,
        step: Int = 2,
        isOpenApiVerbose: Boolean = true,
        contact: Contact = getDefaultContact(),
        endpoints: List<Endpoint> = getSynchronousBankEndpoints(),
        parameters: List<Parameter> = getSynchronousBankParameters(),
        responses: List<Response> = getSynchronousBankResponses(),
        banks: List<Bank> = getSynchronousBanks(),
    ) = Task(
        id = id,
        title = title,
        description = description,
        step = step,
        isOpenApiVerbose = isOpenApiVerbose,
        contact = contact,
        endpoints = endpoints,
        parameters = parameters,
        responses = responses,
        banks = banks,
    )

    fun getAsynchronousBankTask(
        id: Id<Task> = ObjectId("61503edf6354bd996d9e89a6").toId(),
        title: String = DEFAULT_ASYNC_BANK_TITLE,
        description: String = DEFAULT_ASYNC_BANK_DESCRIPTION,
        step: Int = 3,
        isOpenApiVerbose: Boolean = false,
        contact: Contact = getDefaultContact(),
        endpoints: List<Endpoint> = getAsynchronousBankEndpoints(),
        parameters: List<Parameter> = getAsynchronousBankParameters(),
        responses: List<Response> = getAsynchronousBankResponses(),
        banks: List<Bank> = getAsynchronousBanks(),
    ) = Task(
        id = id,
        title = title,
        description = description,
        step = step,
        isOpenApiVerbose = isOpenApiVerbose,
        contact = contact,
        endpoints = endpoints,
        parameters = parameters,
        responses = responses,
        banks = banks,
    )

    fun getDefaultContact(
        name: String = DEFAULT_CONTACT_NAME,
        email: String = DEFAULT_CONTACT_EMAIL,
    ) = Contact(
        name = name,
        email = email,
    )

    private fun getSchufaEndpoints(): List<Endpoint> = listOf(
        getSchufaRestEndpoint(),
        getSchufaSoapEndpoint(),
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
        getVBankSoapEndpoint(),
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
        getVBank(),
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
        getJmsBank(),
    )
}
