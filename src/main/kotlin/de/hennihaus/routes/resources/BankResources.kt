package de.hennihaus.routes.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

object BankPaths {
    const val BANKS_PATH = "/banks"
    const val JMS_TOPIC_PATH = "/{jmsTopic}"
}

@Serializable
@Resource(BankPaths.BANKS_PATH)
class Banks {

    @Serializable
    @Resource(BankPaths.JMS_TOPIC_PATH)
    data class JmsTopic(val parent: Banks = Banks(), val jmsTopic: String)
}
