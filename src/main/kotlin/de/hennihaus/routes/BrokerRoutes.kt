package de.hennihaus.routes

import de.hennihaus.routes.BrokerRoutes.ACTIVE_MQ_PATH
import de.hennihaus.routes.BrokerRoutes.NAME_PATH_PARAMETER
import de.hennihaus.services.BrokerService
import de.hennihaus.services.TeamService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import org.koin.java.KoinJavaComponent.getKoin

object BrokerRoutes {
    const val ACTIVE_MQ_PATH = "activemq"

    const val NAME_PATH_PARAMETER = "name"
}

fun Route.registerBrokerRoutes() = route(path = "/$ACTIVE_MQ_PATH") {
    resetBroker()
    deleteQueueByName()
}

private fun Route.resetBroker() = delete {
    val teamService = getKoin().get<TeamService>()
    val brokerService = getKoin().get<BrokerService>()

    with(receiver = call) {
        teamService.resetAllTeams()
        brokerService.resetBroker()

        respond(
            message = "",
            status = HttpStatusCode.NoContent,
        )
    }
}

private fun Route.deleteQueueByName() = delete(path = "/{$NAME_PATH_PARAMETER}") {
    val brokerService = getKoin().get<BrokerService>()

    with(receiver = call) {
        val name = parameters.getOrFail(name = NAME_PATH_PARAMETER)

        val message = brokerService.deleteQueueByName(name = name).let { "" }

        respond(
            message = message,
            status = HttpStatusCode.NoContent,
        )
    }
}
