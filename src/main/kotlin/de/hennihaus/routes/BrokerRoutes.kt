package de.hennihaus.routes

import de.hennihaus.routes.resources.Broker
import de.hennihaus.services.BrokerService
import de.hennihaus.services.GroupService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.resources.delete
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.java.KoinJavaComponent.getKoin

fun Route.registerBrokerRoutes() {
    resetBroker()
    deleteQueueByName()
}

private fun Route.resetBroker() = delete<Broker> {
    getKoin().get<GroupService>().resetAllGroups()
    getKoin().get<BrokerService>().resetBroker()
    call.respond(
        status = HttpStatusCode.NoContent,
        message = "",
    )
}

private fun Route.deleteQueueByName() = delete<Broker.Name> { request ->
    val brokerService = getKoin().get<BrokerService>()
    call.respond(
        status = HttpStatusCode.NoContent,
        message = brokerService.deleteQueueByName(name = request.name).let { "" },
    )
}
