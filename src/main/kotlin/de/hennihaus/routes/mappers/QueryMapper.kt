package de.hennihaus.routes.mappers

import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.configurations.RoutesConfiguration.DEFAULT_LIMIT_PARAMETER
import de.hennihaus.configurations.RoutesConfiguration.LIMIT_QUERY_PARAMETER
import de.hennihaus.models.cursors.TeamQuery
import de.hennihaus.models.generated.rest.QueryDTO
import de.hennihaus.routes.TeamRoutes.BANKS_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.HAS_PASSED_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.JMS_QUEUE_PARAMETER
import de.hennihaus.routes.TeamRoutes.MAX_REQUESTS_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.MIN_REQUESTS_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.PASSWORD_PARAMETER
import de.hennihaus.routes.TeamRoutes.STUDENT_FIRSTNAME_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.STUDENT_LASTNAME_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.TYPE_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.USERNAME_PARAMETER
import io.ktor.http.Parameters

fun TeamQuery.toTeamQueryDTO() = QueryDTO(
    limit = limit,
    type = type?.name,
    username = username,
    password = password,
    jmsQueue = jmsQueue,
    hasPassed = hasPassed,
    minRequests = minRequests,
    maxRequests = maxRequests,
    studentFirstname = studentFirstname,
    studentLastname = studentLastname,
    banks = banks,
)

fun Parameters.toTeamQueryDTO() = QueryDTO(
    limit = get(name = LIMIT_QUERY_PARAMETER)?.toIntOrNull() ?: DEFAULT_LIMIT_PARAMETER,
    type = get(name = TYPE_QUERY_PARAMETER),
    username = get(name = USERNAME_PARAMETER),
    password = get(name = PASSWORD_PARAMETER),
    jmsQueue = get(name = JMS_QUEUE_PARAMETER),
    hasPassed = get(name = HAS_PASSED_QUERY_PARAMETER)?.toBoolean(),
    minRequests = get(name = MIN_REQUESTS_QUERY_PARAMETER)?.toLongOrNull(),
    maxRequests = get(name = MAX_REQUESTS_QUERY_PARAMETER)?.toLongOrNull(),
    studentFirstname = get(name = STUDENT_FIRSTNAME_QUERY_PARAMETER),
    studentLastname = get(name = STUDENT_LASTNAME_QUERY_PARAMETER),
    banks = getAll(name = BANKS_QUERY_PARAMETER),
)

fun Parameters.toTeamQuery() = TeamQuery(
    limit = get(name = LIMIT_QUERY_PARAMETER)?.toIntOrNull() ?: DEFAULT_LIMIT_PARAMETER,
    type = get(name = TYPE_QUERY_PARAMETER)?.let { TeamType.valueOf(value = it) },
    username = get(name = USERNAME_PARAMETER),
    password = get(name = PASSWORD_PARAMETER),
    jmsQueue = get(name = JMS_QUEUE_PARAMETER),
    hasPassed = get(name = HAS_PASSED_QUERY_PARAMETER)?.toBoolean(),
    minRequests = get(name = MIN_REQUESTS_QUERY_PARAMETER)?.toLongOrNull(),
    maxRequests = get(name = MAX_REQUESTS_QUERY_PARAMETER)?.toLongOrNull(),
    studentFirstname = get(name = STUDENT_FIRSTNAME_QUERY_PARAMETER),
    studentLastname = get(name = STUDENT_LASTNAME_QUERY_PARAMETER),
    banks = getAll(name = BANKS_QUERY_PARAMETER),
)
