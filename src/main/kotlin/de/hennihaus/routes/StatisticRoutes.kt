package de.hennihaus.routes

import de.hennihaus.models.generated.rest.StatisticDTO
import de.hennihaus.routes.StatisticRoutes.INCREMENT_PATH
import de.hennihaus.routes.StatisticRoutes.STATISTICS_PATH
import de.hennihaus.routes.mappers.toStatistic
import de.hennihaus.routes.mappers.toStatisticDTO
import de.hennihaus.services.StatisticService
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.patch
import io.ktor.server.routing.route
import org.koin.java.KoinJavaComponent.getKoin

object StatisticRoutes {
    const val STATISTICS_PATH = "statistics"
    const val INCREMENT_PATH = "increment"
}

fun Route.registerStatisticRoutes() = route(path = "/$STATISTICS_PATH") {
    incrementStatistic()
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
