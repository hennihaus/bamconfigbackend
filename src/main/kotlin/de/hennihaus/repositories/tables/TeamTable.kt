package de.hennihaus.repositories.tables

import de.hennihaus.configurations.ExposedConfiguration.DATABASE_AES_256_BIT_SALT
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_AES_PASSWORD
import de.hennihaus.plugins.PropertyNotFoundException
import de.hennihaus.repositories.tables.BankTableDescription.BANK_UUID_COLUMN
import de.hennihaus.repositories.tables.StatisticTableDescription.STATISTIC_ID_COLUMN
import de.hennihaus.repositories.tables.StatisticTableDescription.STATISTIC_LAST_UPDATED_COLUMN
import de.hennihaus.repositories.tables.StatisticTableDescription.STATISTIC_REQUEST_COUNT_COLUMN
import de.hennihaus.repositories.tables.StudentTableDescription.STUDENT_FIRSTNAME_COLUMN
import de.hennihaus.repositories.tables.StudentTableDescription.STUDENT_LASTNAME_COLUMN
import de.hennihaus.repositories.tables.StudentTableDescription.STUDENT_LAST_UPDATED_COLUMN
import de.hennihaus.repositories.tables.StudentTableDescription.STUDENT_UUID_COLUMN
import de.hennihaus.repositories.tables.TeamTableDescription.TEAM_JMS_QUEUE_COLUMN
import de.hennihaus.repositories.tables.TeamTableDescription.TEAM_LAST_UPDATED_COLUMN
import de.hennihaus.repositories.tables.TeamTableDescription.TEAM_PASSWORD_COLUMN
import de.hennihaus.repositories.tables.TeamTableDescription.TEAM_PASSWORD_VARCHAR_LENGTH
import de.hennihaus.repositories.tables.TeamTableDescription.TEAM_USERNAME_COLUMN
import de.hennihaus.repositories.tables.TeamTableDescription.TEAM_UUID_COLUMN
import org.jetbrains.exposed.crypt.Algorithms
import org.jetbrains.exposed.crypt.encryptedVarchar
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.koin.java.KoinJavaComponent.getKoin

object TeamTable : UUIDTable(columnName = TEAM_UUID_COLUMN) {
    val username = text(name = TEAM_USERNAME_COLUMN)
    val password = encryptedVarchar(
        name = TEAM_PASSWORD_COLUMN,
        cipherTextLength = TEAM_PASSWORD_VARCHAR_LENGTH,
        encryptor = Algorithms.AES_256_PBE_GCM(
            salt = getKoin().getProperty(key = DATABASE_AES_256_BIT_SALT)
                ?: throw PropertyNotFoundException(key = DATABASE_AES_256_BIT_SALT),
            password = getKoin().getProperty(key = DATABASE_AES_PASSWORD)
                ?: throw PropertyNotFoundException(key = DATABASE_AES_PASSWORD),
        ),
    )
    val jmsQueue = text(name = TEAM_JMS_QUEUE_COLUMN)
    val lastUpdated = timestamp(name = TEAM_LAST_UPDATED_COLUMN)
}

object StudentTable : UUIDTable(columnName = STUDENT_UUID_COLUMN) {
    val teamId = reference(name = TEAM_UUID_COLUMN, foreign = TeamTable)
    val firstname = text(name = STUDENT_FIRSTNAME_COLUMN)
    val lastname = text(name = STUDENT_LASTNAME_COLUMN)
    val lastUpdated = timestamp(name = STUDENT_LAST_UPDATED_COLUMN)
}

object StatisticTable : LongIdTable(columnName = STATISTIC_ID_COLUMN) {
    val bankId = reference(name = BANK_UUID_COLUMN, foreign = BankTable)
    val teamId = reference(name = TEAM_UUID_COLUMN, foreign = TeamTable)
    val requestsCount = long(name = STATISTIC_REQUEST_COUNT_COLUMN)
    val lastUpdated = timestamp(name = STATISTIC_LAST_UPDATED_COLUMN)
}

object TeamTableDescription {
    const val TEAM_UUID_COLUMN = "team_uuid"
    const val TEAM_USERNAME_COLUMN = "team_username"
    const val TEAM_PASSWORD_COLUMN = "team_password"
    const val TEAM_JMS_QUEUE_COLUMN = "team_jms_queue"
    const val TEAM_LAST_UPDATED_COLUMN = "team_updated_timestamp_with_time_zone"

    const val TEAM_PASSWORD_VARCHAR_LENGTH = 112
}

object StudentTableDescription {
    const val STUDENT_UUID_COLUMN = "student_uuid"
    const val STUDENT_FIRSTNAME_COLUMN = "student_firstname"
    const val STUDENT_LASTNAME_COLUMN = "student_lastname"
    const val STUDENT_LAST_UPDATED_COLUMN = "student_updated_timestamp_with_time_zone"
}

object StatisticTableDescription {
    const val STATISTIC_ID_COLUMN = "statistic_id"
    const val STATISTIC_REQUEST_COUNT_COLUMN = "statistic_requests_count"
    const val STATISTIC_LAST_UPDATED_COLUMN = "statistic_updated_timestamp_with_time_zone"
}
