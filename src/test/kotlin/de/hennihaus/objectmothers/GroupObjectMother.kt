package de.hennihaus.objectmothers

import de.hennihaus.models.Group
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

object GroupObjectMother {

    private const val DEFAULT_PASSWORD = "0123456789"
    private const val DEFAULT_HAS_PASSED = false
    private val DEFAULT_STUDENTS = listOf(
        "Angelar Merkel",
        "Max Mustermann",
        "Thomas Müller"
    )
    val ZERO_STATS = mapOf(
        "schufa" to 0,
        "vbank" to 0,
        "jmsBankA" to 0
    )

    fun getFirstGroup(
        id: Id<Group> = ObjectId("61320a79410347e41dbea0f9").toId(),
        username: String = "LoanBrokerGruppe01",
        password: String = DEFAULT_PASSWORD,
        jmsTopic: String = "ResponseLoanBrokerGruppe01",
        students: List<String> = DEFAULT_STUDENTS,
        stats: Map<String, Int> = ZERO_STATS,
        hasPassed: Boolean = DEFAULT_HAS_PASSED
    ) = Group(
        id = id,
        username = username,
        password = password,
        jmsTopic = jmsTopic,
        students = students,
        stats = stats,
        hasPassed = hasPassed
    )

    fun getSecondGroup(
        id: Id<Group> = ObjectId("61320a84befcde533be505c5").toId(),
        username: String = "LoanBrokerGruppe02",
        password: String = DEFAULT_PASSWORD,
        jmsTopic: String = "ResponseLoanBrokerGruppe02",
        students: List<String> = DEFAULT_STUDENTS,
        stats: Map<String, Int> = ZERO_STATS,
        hasPassed: Boolean = DEFAULT_HAS_PASSED
    ) = Group(
        id = id,
        username = username,
        password = password,
        jmsTopic = jmsTopic,
        students = students,
        stats = stats,
        hasPassed = hasPassed
    )

    fun getThirdGroup(
        id: Id<Group> = ObjectId("62449e3d944f2af727e6f1fb").toId(),
        username: String = "LoanBrokerGruppe03",
        password: String = DEFAULT_PASSWORD,
        jmsTopic: String = "ResponseLoanBrokerGruppe03",
        students: List<String> = DEFAULT_STUDENTS,
        stats: Map<String, Int> = ZERO_STATS,
        hasPassed: Boolean = DEFAULT_HAS_PASSED
    ) = Group(
        id = id,
        username = username,
        password = password,
        jmsTopic = jmsTopic,
        students = students,
        stats = stats,
        hasPassed = hasPassed
    )
}
