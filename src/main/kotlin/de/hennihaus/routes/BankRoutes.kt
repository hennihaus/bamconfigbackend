package de.hennihaus.routes

import de.hennihaus.routes.resources.Banks
import de.hennihaus.services.BankService
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.put
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.java.KoinJavaComponent.getKoin

fun Route.registerBankRoutes() {
    getAllBanks()
    getBankByJmsTopic()
    updateAllBanks()
    updateBank()
}

private fun Route.getAllBanks() = get<Banks> {
    val bankService = getKoin().get<BankService>()
    call.respond(message = bankService.getAllBanks())
}

private fun Route.getBankByJmsTopic() = get<Banks.JmsTopic> { request ->
    val bankService = getKoin().get<BankService>()
    call.respond(
        message = bankService.getBankByJmsTopic(
            jmsTopic = request.jmsTopic
        )
    )
}

private fun Route.updateAllBanks() = put<Banks> {
    val bankService = getKoin().get<BankService>()
    call.respond(
        message = bankService.updateAllBanks(
            banks = call.receive()
        )
    )
}

private fun Route.updateBank() = put<Banks.JmsTopic> {
    val bankService = getKoin().get<BankService>()
    call.respond(
        message = bankService.updateBank(
            bank = call.receive()
        )
    )
}
