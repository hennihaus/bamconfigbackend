package de.hennihaus.routes

import de.hennihaus.models.generated.rest.BankDTO
import de.hennihaus.routes.BankRoutes.BANKS_PATH
import de.hennihaus.routes.BankRoutes.ID_PATH_PARAMETER
import de.hennihaus.routes.mappers.toBank
import de.hennihaus.routes.mappers.toBankDTO
import de.hennihaus.services.BankService
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import org.koin.java.KoinJavaComponent.getKoin

object BankRoutes {
    const val BANKS_PATH = "banks"

    const val ID_PATH_PARAMETER = "id"
}

fun Route.registerBankRoutes() = route(path = "/$BANKS_PATH") {
    getAllBanks()
    getBankById()
    patchBank()
}

private fun Route.getAllBanks() = get {
    val bankService = getKoin().get<BankService>()

    with(receiver = call) {
        val banks = bankService.getAllBanks()

        respond(message = banks.map { it.toBankDTO() })
    }
}

private fun Route.getBankById() = get(path = "/{$ID_PATH_PARAMETER}") {
    val bankService = getKoin().get<BankService>()

    with(receiver = call) {
        val id = parameters.getOrFail(name = ID_PATH_PARAMETER)

        val bank = bankService.getBankById(id = id)

        respond(message = bank.toBankDTO())
    }
}

private fun Route.patchBank() = patch(path = "/{$ID_PATH_PARAMETER}") {
    val bankService = getKoin().get<BankService>()

    with(receiver = call) {
        val id = parameters.getOrFail(name = ID_PATH_PARAMETER)
        val dto = receive<BankDTO>()

        val bank = bankService.patchBank(
            id = id,
            bank = dto.toBank(),
        )

        respond(message = bank.toBankDTO())
    }
}
