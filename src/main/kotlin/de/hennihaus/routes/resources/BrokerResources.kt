package de.hennihaus.routes.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

object BrokerPaths {
    const val ACTIVE_MQ_PATH = "/activemq"
    const val NAME_PATH = "/{name}"
}

@Serializable
@Resource(BrokerPaths.ACTIVE_MQ_PATH)
class Broker {

    @Serializable
    @Resource(BrokerPaths.NAME_PATH)
    data class Name(val parent: Broker = Broker(), val name: String)
}
