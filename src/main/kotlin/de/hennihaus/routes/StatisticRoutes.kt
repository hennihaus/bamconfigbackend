package de.hennihaus.routes

import de.hennihaus.routes.resources.Statistics
import de.hennihaus.services.StatisticService
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.patch
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.java.KoinJavaComponent.getKoin

fun Route.registerStatisticRoutes() {
    incrementStatistic()
}

private fun Route.incrementStatistic() = patch<Statistics.Increment> {
    val statisticService = getKoin().get<StatisticService>()
    call.respond(
        message = statisticService.incrementRequest(
            statistic = call.receive(),
        ),
    )
}
