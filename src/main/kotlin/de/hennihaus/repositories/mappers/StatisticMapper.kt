package de.hennihaus.repositories.mappers

import de.hennihaus.bamdatamodel.Statistic
import de.hennihaus.repositories.entities.StatisticEntity

fun StatisticEntity.toStatistic() = Statistic(
    bankId = bankId.value,
    teamId = teamId.value,
    requestsCount = requestsCount,
)
