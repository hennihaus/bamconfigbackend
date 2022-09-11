package de.hennihaus.repositories.mappers

import de.hennihaus.models.generated.Statistic
import de.hennihaus.repositories.entities.StatisticEntity

fun StatisticEntity.toStatistic() = Statistic(
    bankId = bank.id.value,
    teamId = team.id.value,
    requestsCount = requestsCount,
)
