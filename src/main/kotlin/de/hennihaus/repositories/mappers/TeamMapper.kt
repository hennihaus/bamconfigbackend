package de.hennihaus.repositories.mappers

import de.hennihaus.bamdatamodel.Student
import de.hennihaus.bamdatamodel.Team
import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.repositories.StatisticRepository.Companion.ZERO_REQUESTS
import de.hennihaus.repositories.entities.StatisticEntity
import de.hennihaus.repositories.entities.StudentEntity
import de.hennihaus.repositories.entities.TeamEntity
import java.time.LocalDateTime
import java.time.ZoneOffset

fun TeamEntity.toTeam() = Team(
    uuid = id.value,
    type = TeamType.valueOf(value = type),
    username = username,
    password = password,
    jmsQueue = jmsQueue,
    students = students.map { it.toStudent() },
    statistics = statistics.filter { it.bank.isActive }.associate { it.toPair() },
    hasPassed = statistics.filter { it.bank.isActive }.all { it.requestsCount > ZERO_REQUESTS },
    createdAt = LocalDateTime.ofInstant(createdAt, ZoneOffset.UTC.normalized()),
    updatedAt = LocalDateTime.ofInstant(updatedAt, ZoneOffset.UTC.normalized()),
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
