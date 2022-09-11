package de.hennihaus.objectmothers

import de.hennihaus.models.generated.Student
import de.hennihaus.models.generated.Team
import de.hennihaus.objectmothers.BankObjectMother.ASYNC_BANK_NAME
import de.hennihaus.objectmothers.BankObjectMother.SCHUFA_BANK_NAME
import de.hennihaus.objectmothers.BankObjectMother.SYNC_BANK_NAME
import de.hennihaus.objectmothers.StudentObjectMother.getFirstStudent
import de.hennihaus.objectmothers.StudentObjectMother.getSecondStudent
import de.hennihaus.objectmothers.StudentObjectMother.getThirdStudent
import java.util.UUID

object TeamObjectMother {

    const val FIRST_TEAM_UUID = "63701a57-91b8-40d4-b4e2-b8dd9e7a75a5"
    const val SECOND_TEAM_UUID = "c4a2dd22-f3d4-4de2-9fe0-dbe70062bef9"
    const val THIRD_TEAM_UUID = "40226a72-b520-4f84-b806-b3ab30ff1769"

    const val FIRST_TEAM_USERNAME = "Team01"
    const val SECOND_TEAM_USERNAME = "Team02"
    const val THIRD_TEAM_USERNAME = "Team03"

    const val FIRST_TEAM_JMS_QUEUE = "ResponseLoanBrokerTeam01"
    const val SECOND_TEAM_JMS_QUEUE = "ResponseLoanBrokerTeam02"
    const val THIRD_TEAM_JMS_QUEUE = "ResponseLoanBrokerTeam03"

    const val DEFAULT_PASSWORD = "lkhNqstcxs"
    const val DEFAULT_HAS_PASSED = false

    fun getFirstTeam(
        uuid: UUID = UUID.fromString(FIRST_TEAM_UUID),
        username: String = FIRST_TEAM_USERNAME,
        password: String = DEFAULT_PASSWORD,
        jmsQueue: String = FIRST_TEAM_JMS_QUEUE,
        students: List<Student> = getDefaultStudents(),
        statistics: Map<String, Long> = getZeroStatistics(),
        hasPassed: Boolean = DEFAULT_HAS_PASSED,
    ) = Team(
        uuid = uuid,
        username = username,
        password = password,
        jmsQueue = jmsQueue,
        students = students,
        statistics = statistics,
        hasPassed = hasPassed,
    )

    fun getSecondTeam(
        uuid: UUID = UUID.fromString(SECOND_TEAM_UUID),
        username: String = SECOND_TEAM_USERNAME,
        password: String = DEFAULT_PASSWORD,
        jmsQueue: String = SECOND_TEAM_JMS_QUEUE,
        students: List<Student> = getDefaultStudents(),
        statistics: Map<String, Long> = getZeroStatistics(),
        hasPassed: Boolean = DEFAULT_HAS_PASSED,
    ) = Team(
        uuid = uuid,
        username = username,
        password = password,
        jmsQueue = jmsQueue,
        students = students,
        statistics = statistics,
        hasPassed = hasPassed,
    )

    fun getThirdTeam(
        uuid: UUID = UUID.fromString(THIRD_TEAM_UUID),
        username: String = THIRD_TEAM_USERNAME,
        password: String = DEFAULT_PASSWORD,
        jmsQueue: String = THIRD_TEAM_JMS_QUEUE,
        students: List<Student> = getDefaultStudents(),
        statistics: Map<String, Long> = getZeroStatistics(),
        hasPassed: Boolean = DEFAULT_HAS_PASSED,
    ) = Team(
        uuid = uuid,
        username = username,
        password = password,
        jmsQueue = jmsQueue,
        students = students,
        statistics = statistics,
        hasPassed = hasPassed,
    )

    fun getZeroStatistics(): Map<String, Long> = mapOf(
        SCHUFA_BANK_NAME to 0L,
        SYNC_BANK_NAME to 0L,
        ASYNC_BANK_NAME to 0L,
    )

    fun getNonZeroStatistics(): Map<String, Long> = mapOf(
        SCHUFA_BANK_NAME to 1L,
        SYNC_BANK_NAME to 1L,
        ASYNC_BANK_NAME to 1L,
    )

    private fun getDefaultStudents(): List<Student> = listOf(
        getFirstStudent(),
        getSecondStudent(),
        getThirdStudent(),
    )
}
