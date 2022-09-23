package de.hennihaus.routes

import de.hennihaus.models.generated.rest.BankDTO
import de.hennihaus.routes.mappers.toBank
import de.hennihaus.routes.mappers.toBankDTO
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
    getBankById()
    saveBank()
}

private fun Route.getAllBanks() = get<Banks> {
    val bankService = getKoin().get<BankService>()
    call.respond(
        message = bankService.getAllBanks().map {
            it.toBankDTO()
        },
    )
}

private fun Route.getBankById() = get<Banks.Id> { request ->
    val bankService = getKoin().get<BankService>()
    call.respond(
        message = bankService.getBankById(id = request.id).toBankDTO(),
    )
}

private fun Route.saveBank() = put<Banks.Id> {
    val bankService = getKoin().get<BankService>()
    call.respond(
        message = bankService.saveBank(
            bank = call.receive<BankDTO>().toBank(),
        ),
    )
}
