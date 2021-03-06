package de.hennihaus.objectmothers

import de.hennihaus.models.Endpoint
import de.hennihaus.models.EndpointType
import java.net.URI

object EndpointObjectMother {

    fun getSchufaRestEndpoint(
        type: EndpointType = EndpointType.REST,
        url: URI = URI(""),
        docsUrl: URI = URI("http://bambusinessintegration.wi.hs-furtwangen.de/schufa/docs.html"),
    ) = Endpoint(
        type = type,
        url = url,
        docsUrl = docsUrl,
    )

    fun getVBankRestEndpoint(
        type: EndpointType = EndpointType.REST,
        url: URI = URI(""),
        docsUrl: URI = URI("http://bambusinessintegration.wi.hs-furtwangen.de/deutschebank/docs.html"),
    ) = Endpoint(
        type = type,
        url = url,
        docsUrl = docsUrl,
    )

    fun getActiveMqEndpoint(
        type: EndpointType = EndpointType.JMS,
        url: URI = URI("tcp://bambusinessintegration.wi.hs-furtwangen.de:61616"),
        docsUrl: URI = URI(""),
    ) = Endpoint(
        type = type,
        url = url,
        docsUrl = docsUrl,
    )
}
