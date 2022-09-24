package de.hennihaus.repositories.mappers

import de.hennihaus.bamdatamodel.Student
import de.hennihaus.bamdatamodel.Team
import de.hennihaus.repositories.entities.StatisticEntity
import de.hennihaus.repositories.entities.StudentEntity
import de.hennihaus.repositories.entities.TeamEntity
import de.hennihaus.services.StatisticService.Companion.ZERO_REQUESTS

fun TeamEntity.toTeam() = Team(
    uuid = id.value,
    username = username,
    password = password,
    jmsQueue = jmsQueue,
    students = students.map { it.toStudent() },
    statistics = statistics.associate { it.toPair() },
    hasPassed = statistics.filter { it.bank.isActive }.all { it.requestsCount > ZERO_REQUESTS },
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
