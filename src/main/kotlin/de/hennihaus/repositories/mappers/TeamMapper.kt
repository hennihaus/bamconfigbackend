package de.hennihaus.repositories.mappers

import de.hennihaus.bamdatamodel.Student
import de.hennihaus.bamdatamodel.Team
import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.configurations.Configuration.DEFAULT_ZONE_ID
import de.hennihaus.repositories.StatisticRepository.Companion.ZERO_REQUESTS
import de.hennihaus.repositories.entities.StatisticEntity
import de.hennihaus.repositories.entities.StudentEntity
import de.hennihaus.repositories.entities.TeamEntity
import java.time.LocalDateTime
import java.time.ZoneId

fun TeamEntity.toTeam() = Team(
    uuid = id.value,
    type = TeamType.valueOf(value = type),
    username = username,
    password = password,
    jmsQueue = jmsQueue,
    students = students.map { it.toStudent() },
    statistics = statistics.associate { it.toPair() },
    hasPassed = statistics.filter { it.bank.isActive }.all { it.requestsCount > ZERO_REQUESTS },
    createdAt = LocalDateTime.ofInstant(createdAt, ZoneId.of(DEFAULT_ZONE_ID)),
    updatedAt = LocalDateTime.ofInstant(updatedAt, ZoneId.of(DEFAULT_ZONE_ID)),
)

private fun StudentEntity.toStudent() = Student(
    uuid = id.value,
    firstname = firstname,
    lastname = lastname,
)

private fun StatisticEntity.toPair() = Pair(
    first = bank.name,
    second = requestsCount,
)
