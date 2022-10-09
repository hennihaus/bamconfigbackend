package de.hennihaus.routes.mappers

import de.hennihaus.bamdatamodel.Statistic
import de.hennihaus.models.generated.rest.StatisticDTO
import java.util.UUID

fun Statistic.toStatisticDTO() = StatisticDTO(
    bankId = "$bankId",
    teamId = "$teamId",
    requestsCount = requestsCount,
)

fun StatisticDTO.toStatistic() = Statistic(
    bankId = UUID.fromString(bankId),
    teamId = UUID.fromString(teamId),
    requestsCount = requestsCount,
)
