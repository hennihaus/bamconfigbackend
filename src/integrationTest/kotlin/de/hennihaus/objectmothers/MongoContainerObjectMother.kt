package de.hennihaus.objectmothers

import de.hennihaus.models.Bank
import de.hennihaus.models.CreditConfiguration
import de.hennihaus.models.Group
import de.hennihaus.models.RatingLevel
import org.bson.types.ObjectId
import org.litote.kmongo.id.toId

object MongoContainerObjectMother {
    val GROUP_OBJECT_ID = ObjectId("61376f0750f6a6dfcd3b39a7")
    const val GROUP_USERNAME = "Gruppe01"
    const val GROUP_PASSWORD = "lkhNqstcxs"
    const val GROUP_JMS_TOPIC = "ResponseLoanBrokerGruppe01"
    val GROUP_STUDENTS = listOf("Büsra Alili", "Emel Göktas", "Helmut Hermann Lindel")
    val GROUP_STATS = mapOf(
        "schufa" to 0,
        "deutschebank" to 0,
        "sparkasse" to 0,
        "psdbank" to 0,
        "raiffeisen" to 0,
        "volksbank" to 0,
        "commerzbank" to 0
    )
    const val GROUP_HAS_PASSED = false

    const val BANK_JMS_TOPIC = "sparkasse"
    const val BANK_JMS_NAME = "Sparkasse"
    const val BANK_THUMBNAIL_URL =
        "https://upload.wikimedia.org/wikipedia/commons/thumb/8/83/Sparkasse.svg/2000px-Sparkasse.svg.png"
    const val BANK_IS_ASYNC = true
    const val BANK_IS_ACTIVE = true

    const val CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS = 10_000
    const val CREDIT_CONFIGURATION_MAX_AMOUNT_IN_EUROS = 50_000
    const val CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS = 6
    const val CREDIT_CONFIGURATION_MAX_TERM_IN_MONTHS = 36
    val CREDIT_CONFIGURATION_MIN_SCHUFA_RATING = RatingLevel.A
    val CREDIT_CONFIGURATION_MAX_SCHUFA_RATING = RatingLevel.P

    val TASK_OBJECT_ID = ObjectId("61503edf6354bd996d9e89a6")

    fun getFirstGroup() = Group(
        id = GROUP_OBJECT_ID.toId(),
        username = GROUP_USERNAME,
        password = GROUP_PASSWORD,
        jmsTopic = GROUP_JMS_TOPIC,
        students = GROUP_STUDENTS,
        stats = GROUP_STATS,
        hasPassed = GROUP_HAS_PASSED
    )

    fun getSparkasseBank() = Bank(
        jmsTopic = BANK_JMS_TOPIC,
        name = BANK_JMS_NAME,
        thumbnailUrl = BANK_THUMBNAIL_URL,
        isAsync = BANK_IS_ASYNC,
        isActive = BANK_IS_ACTIVE,
        creditConfiguration = CreditConfiguration(
            minAmountInEuros = CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS,
            maxAmountInEuros = CREDIT_CONFIGURATION_MAX_AMOUNT_IN_EUROS,
            minTermInMonths = CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS,
            maxTermInMonths = CREDIT_CONFIGURATION_MAX_TERM_IN_MONTHS,
            minSchufaRating = CREDIT_CONFIGURATION_MIN_SCHUFA_RATING,
            maxSchufaRating = CREDIT_CONFIGURATION_MAX_SCHUFA_RATING
        ),
        groups = listOf(
            getFirstGroup()
        )
    )
}
