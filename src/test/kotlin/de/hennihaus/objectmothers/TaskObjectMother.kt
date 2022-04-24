package de.hennihaus.objectmothers

import de.hennihaus.models.Bank
import de.hennihaus.models.Endpoint
import de.hennihaus.models.Parameter
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
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

object TaskObjectMother {

    fun getSchufaTask(
        id: Id<Task> = ObjectId("6150281b8031b5b4e70a881e").toId(),
        title: String = "Schufa-Auskunft",
        description: String = "<p>Schufa-Auskunft-Beschreibung</p>",
        step: Int = 1,
        endpoints: List<Endpoint> = listOf(
            getSchufaRestEndpoint(),
            getSchufaSoapEndpoint()
        ),
        parameters: List<Parameter> = listOf(
            getSocialSecurityNumberParameter(),
            getRatingLevelParameter(),
            getDelayInMillisecondsParameter(),
            getUsernameParameter(),
            getPasswordParameter()
        ),
        banks: List<Bank> = listOf(
            getSchufaBank()
        )
    ) = Task(
        id = id,
        title = title,
        description = description,
        step = step,
        endpoints = endpoints,
        parameters = parameters,
        banks = banks
    )

    fun getSynchronousBankTask(
        id: Id<Task> = ObjectId("61502fa9afad97203db562b3").toId(),
        title: String = "Synchrone Bank",
        description: String = "<p>Synchrone-Bank-Beschreibung</p>",
        step: Int = 2,
        endpoints: List<Endpoint> = listOf(
            getVBankRestEndpoint(),
            getVBankSoapEndpoint()
        ),
        parameters: List<Parameter> = listOf(
            getAmountInEurosParameter(),
            getTermInMonthsParameter(),
            getRatingLevelParameter(),
            getDelayInMillisecondsParameter(),
            getUsernameParameter(),
            getPasswordParameter()
        ),
        banks: List<Bank> = listOf(
            getVBank()
        )
    ) = Task(
        id = id,
        title = title,
        description = description,
        step = step,
        endpoints = endpoints,
        parameters = parameters,
        banks = banks
    )

    fun getAsynchronousBankTask(
        id: Id<Task> = ObjectId("61503edf6354bd996d9e89a6").toId(),
        title: String = "Asynchrone Banken",
        description: String = "<p>Asynchrone-Bank-Beschreibung</p>",
        step: Int = 3,
        endpoints: List<Endpoint> = listOf(
            getActiveMqEndpoint()
        ),
        parameters: List<Parameter> = listOf(
            getRequestIdParameter(),
            getAmountInEurosParameter(),
            getTermInMonthsParameter(),
            getRatingLevelParameter(),
            getDelayInMillisecondsParameter(),
            getUsernameParameter(),
            getPasswordParameter()
        ),
        banks: List<Bank> = listOf(
            getJmsBank()
        )
    ) = Task(
        id = id,
        title = title,
        description = description,
        step = step,
        endpoints = endpoints,
        parameters = parameters,
        banks = banks
    )
}
