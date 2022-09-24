package de.hennihaus.routes.mappers

import de.hennihaus.bamdatamodel.Student
import de.hennihaus.bamdatamodel.Team
import de.hennihaus.models.generated.rest.StudentDTO
import de.hennihaus.models.generated.rest.TeamDTO
import java.util.UUID

fun Team.toTeamDTO() = TeamDTO(
    uuid = "$uuid",
    username = username,
    password = password,
    jmsQueue = jmsQueue,
    students = students.map { it.toStudentDTO() },
    statistics = statistics,
    hasPassed = hasPassed,
)

fun TeamDTO.toTeam() = Team(
    uuid = UUID.fromString(uuid),
    username = username,
    password = password,
    jmsQueue = jmsQueue,
    students = students.map { it.toStudent() },
    statistics = statistics,
    hasPassed = hasPassed,
)

private fun Student.toStudentDTO() = StudentDTO(
    uuid = "$uuid",
    firstname = firstname,
    lastname = lastname,
)

private fun StudentDTO.toStudent() = Student(
    uuid = UUID.fromString(uuid),
    firstname = firstname,
    lastname = lastname,
)
