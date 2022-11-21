package de.hennihaus.objectmothers

import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.ASYNC_BANK_NAME
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.SCHUFA_BANK_NAME
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.SYNC_BANK_NAME
import de.hennihaus.bamdatamodel.objectmothers.StatisticObjectMother.ZERO_REQUESTS_COUNT
import de.hennihaus.bamdatamodel.objectmothers.StudentObjectMother.FIRST_STUDENT_FIRSTNAME
import de.hennihaus.bamdatamodel.objectmothers.StudentObjectMother.FIRST_STUDENT_LASTNAME
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.DEFAULT_HAS_PASSED
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.DEFAULT_PASSWORD
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.FIRST_TEAM_JMS_QUEUE
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.FIRST_TEAM_USERNAME
import de.hennihaus.models.cursors.TeamQuery

object TeamQueryObjectMother {

    const val DEFAULT_MIN_LIMIT = 1
    const val DEFAULT_TEAM_TYPE = "REGULAR"
    const val DEFAULT_MAX_REQUESTS = Long.MAX_VALUE

    fun getTeamQueryWithNoEmptyFields(
        limit: Int = DEFAULT_MIN_LIMIT,
        type: TeamType? = TeamType.valueOf(value = DEFAULT_TEAM_TYPE),
        username: String? = FIRST_TEAM_USERNAME,
        password: String? = DEFAULT_PASSWORD,
        jmsQueue: String? = FIRST_TEAM_JMS_QUEUE,
        hasPassed: Boolean? = DEFAULT_HAS_PASSED,
        minRequests: Long? = ZERO_REQUESTS_COUNT,
        maxRequests: Long? = DEFAULT_MAX_REQUESTS,
        studentFirstname: String? = FIRST_STUDENT_FIRSTNAME,
        studentLastname: String? = FIRST_STUDENT_LASTNAME,
        banks: List<String>? = getAllBanks(),
    ) = TeamQuery(
        limit = limit,
        type = type,
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

    fun getTeamQueryWithEmptyFields(
        limit: Int = DEFAULT_MIN_LIMIT,
        type: TeamType? = null,
        username: String? = null,
        password: String? = null,
        jmsQueue: String? = null,
        hasPassed: Boolean? = null,
        minRequests: Long? = null,
        maxRequests: Long? = null,
        studentFirstname: String? = null,
        studentLastname: String? = null,
        banks: List<String>? = null,
    ) = TeamQuery(
        limit = limit,
        type = type,
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

    private fun getAllBanks() = listOf(
        SCHUFA_BANK_NAME,
        SYNC_BANK_NAME,
        ASYNC_BANK_NAME,
    )
}