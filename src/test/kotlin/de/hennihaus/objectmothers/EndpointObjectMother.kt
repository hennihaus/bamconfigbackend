package de.hennihaus.objectmothers

import de.hennihaus.models.generated.Endpoint
import de.hennihaus.models.generated.EndpointType
import java.net.URI
import java.util.UUID

object EndpointObjectMother {

    const val SCHUFA_UUID = "a2058a25-5934-414a-a67f-afeb4c0e1bec"
    const val SCHUFA_ENDPOINT_TYPE = "REST"
    const val SCHUFA_URL = ""
    const val SCHUFA_DOCS_URL = "http://bambusinessintegration.wi.hs-furtwangen.de/schufa/docs.html"

    const val V_BANK_UUID = "08191f25-c0c0-43ac-8244-217965006118"
    const val V_BANK_ENDPOINT_TYPE = "REST"
    const val V_BANK_URL = ""
    const val V_BANK_DOCS_URL = "http://bambusinessintegration.wi.hs-furtwangen.de/deutschebank/docs.html"

    const val ACTIVE_MQ_UUID = "35540453-ea3d-4f87-a19f-486c29e68b4f"
    const val ACTIVE_MQ_ENDPOINT_TYPE = "JMS"
    const val ACTIVE_MQ_URL = "tcp://bambusinessintegration.wi.hs-furtwangen.de:61616"
    const val ACTIVE_MQ_DOCS_URL = ""

    fun getSchufaRestEndpoint(
        uuid: UUID = UUID.fromString(SCHUFA_UUID),
        type: EndpointType = EndpointType.valueOf(value = SCHUFA_ENDPOINT_TYPE),
        url: URI = URI(SCHUFA_URL),
        docsUrl: URI = URI(SCHUFA_DOCS_URL),
    ) = Endpoint(
        uuid = uuid,
        type = type,
        url = url,
        docsUrl = docsUrl,
    )

    fun getVBankRestEndpoint(
        uuid: UUID = UUID.fromString(V_BANK_UUID),
        type: EndpointType = EndpointType.valueOf(value = V_BANK_ENDPOINT_TYPE),
        url: URI = URI(V_BANK_URL),
        docsUrl: URI = URI(V_BANK_DOCS_URL),
    ) = Endpoint(
        uuid = uuid,
        type = type,
        url = url,
        docsUrl = docsUrl,
    )

    fun getActiveMqEndpoint(
        uuid: UUID = UUID.fromString(ACTIVE_MQ_UUID),
        type: EndpointType = EndpointType.valueOf(value = ACTIVE_MQ_ENDPOINT_TYPE),
        url: URI = URI(ACTIVE_MQ_URL),
        docsUrl: URI = URI(ACTIVE_MQ_DOCS_URL),
    ) = Endpoint(
        uuid = uuid,
        type = type,
        url = url,
        docsUrl = docsUrl,
    )
}
