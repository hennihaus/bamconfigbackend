package de.hennihaus.routes

import de.hennihaus.models.generated.rest.StatisticDTO
import de.hennihaus.routes.StatisticRoutes.ID_PATH_PARAMETER
import de.hennihaus.routes.StatisticRoutes.INCREMENT_PATH
import de.hennihaus.routes.StatisticRoutes.LIMIT_PATH_PARAMETER
import de.hennihaus.routes.StatisticRoutes.STATISTICS_PATH
import de.hennihaus.routes.mappers.toStatistic
import de.hennihaus.routes.mappers.toStatisticDTO
import de.hennihaus.services.StatisticService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.util.getOrFail
import org.koin.java.KoinJavaComponent.getKoin

object StatisticRoutes {
    const val STATISTICS_PATH = "statistics"
    const val INCREMENT_PATH = "increment"

    const val ID_PATH_PARAMETER = "id"
    const val LIMIT_PATH_PARAMETER = "limit"
}

fun Route.registerStatisticRoutes() = route(path = "/$STATISTICS_PATH") {
    saveStatistics()
    deleteStatistics()
    recreateStatistics()
    incrementStatistic()
}

private fun Route.saveStatistics() = put(path = "/{$ID_PATH_PARAMETER}") {
    val statisticService = getKoin().get<StatisticService>()

    with(receiver = call) {
        val bankId = parameters.getOrFail(name = ID_PATH_PARAMETER)

        val message = statisticService.saveStatistics(bankId = bankId).let { "" }

        respond(
            message = message,
            status = HttpStatusCode.NoContent,
        )
    }
}

private fun Route.deleteStatistics() = delete(path = "/{$ID_PATH_PARAMETER}") {
    val statisticService = getKoin().get<StatisticService>()

    with(receiver = call) {
        val bankId = parameters.getOrFail(name = ID_PATH_PARAMETER)

        val message = statisticService.deleteStatistics(bankId = bankId).let { "" }

        respond(
            message = message,
            status = HttpStatusCode.NoContent,
        )
    }
}

private fun Route.recreateStatistics() = post(path = "/{$LIMIT_PATH_PARAMETER}") {
    val statisticService = getKoin().get<StatisticService>()

    with(receiver = call) {
        val limit = parameters.getOrFail(name = LIMIT_PATH_PARAMETER).toLongOrNull() ?: throw BadRequestException(
            message = "limit must be a number",
        )

        val message = statisticService.recreateStatistics(limit = limit).let { "" }

        respond(
            message = message,
            status = HttpStatusCode.NoContent,
        )
    }
}

private fun Route.incrementStatistic() = patch(path = "/$INCREMENT_PATH") {
    val statisticService = getKoin().get<StatisticService>()

    with(receiver = call) {
        val dto = receive<StatisticDTO>()

        val statistic = statisticService.incrementRequest(
            statistic = dto.toStatistic(),
        )

        respond(message = statistic.toStatisticDTO())
    }
}
