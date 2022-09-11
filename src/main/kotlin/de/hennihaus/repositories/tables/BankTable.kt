package de.hennihaus.repositories.tables

import de.hennihaus.repositories.tables.BankTableDescription.BANK_IS_ACTIVE_COLUMN
import de.hennihaus.repositories.tables.BankTableDescription.BANK_IS_ASYNC_COLUMN
import de.hennihaus.repositories.tables.BankTableDescription.BANK_JMS_QUEUE_COLUMN
import de.hennihaus.repositories.tables.BankTableDescription.BANK_LAST_UPDATED_COLUMN
import de.hennihaus.repositories.tables.BankTableDescription.BANK_NAME_COLUMN
import de.hennihaus.repositories.tables.BankTableDescription.BANK_THUMBNAIL_URL_COLUMN
import de.hennihaus.repositories.tables.BankTableDescription.BANK_UUID_COLUMN
import de.hennihaus.repositories.tables.CreditConfigurationTableDescription.CREDIT_CONFIGURATION_MAX_AMOUNT_IN_EUROS_COLUMN
import de.hennihaus.repositories.tables.CreditConfigurationTableDescription.CREDIT_CONFIGURATION_MAX_SCHUFA_RATING_COLUMN
import de.hennihaus.repositories.tables.CreditConfigurationTableDescription.CREDIT_CONFIGURATION_MAX_TERM_IN_MONTHS_COLUMN
import de.hennihaus.repositories.tables.CreditConfigurationTableDescription.CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS_COLUMN
import de.hennihaus.repositories.tables.CreditConfigurationTableDescription.CREDIT_CONFIGURATION_MIN_SCHUFA_RATING_COLUMN
import de.hennihaus.repositories.tables.CreditConfigurationTableDescription.CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS_COLUMN
import de.hennihaus.repositories.tables.TaskTableDescription.TASK_UUID_COLUMN
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object BankTable : UUIDTable(columnName = BANK_UUID_COLUMN) {
    val taskId = reference(name = TASK_UUID_COLUMN, foreign = TaskTable)
    val name = text(name = BANK_NAME_COLUMN)
    val jmsQueue = text(name = BANK_JMS_QUEUE_COLUMN)
    val thumbnailUrl = text(name = BANK_THUMBNAIL_URL_COLUMN)
    val isAsync = bool(name = BANK_IS_ASYNC_COLUMN)
    val isActive = bool(name = BANK_IS_ACTIVE_COLUMN)
    val lastUpdated = timestamp(name = BANK_LAST_UPDATED_COLUMN)
}

object CreditConfigurationTable : UUIDTable(
    name = CreditConfigurationTableDescription.CREDIT_CONFIGURATION_TABLE,
    columnName = CreditConfigurationTableDescription.CREDIT_CONFIGURATION_UUID_COLUMN,
) {
    val bankId = reference(name = BANK_UUID_COLUMN, foreign = BankTable)
    val minAmountInEuros = integer(name = CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS_COLUMN)
    val maxAmountInEuros = integer(name = CREDIT_CONFIGURATION_MAX_AMOUNT_IN_EUROS_COLUMN)
    val minTermInMonths = integer(name = CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS_COLUMN)
    val maxTermInMonths = integer(name = CREDIT_CONFIGURATION_MAX_TERM_IN_MONTHS_COLUMN)
    val minSchufaRating = text(name = CREDIT_CONFIGURATION_MIN_SCHUFA_RATING_COLUMN)
    val maxSchufaRating = text(name = CREDIT_CONFIGURATION_MAX_SCHUFA_RATING_COLUMN)
}

object BankTableDescription {
    const val BANK_UUID_COLUMN = "bank_uuid"
    const val BANK_NAME_COLUMN = "bank_name"
    const val BANK_JMS_QUEUE_COLUMN = "bank_jms_queue"
    const val BANK_THUMBNAIL_URL_COLUMN = "bank_thumbnail_url"
    const val BANK_IS_ASYNC_COLUMN = "bank_is_async"
    const val BANK_IS_ACTIVE_COLUMN = "bank_is_active"
    const val BANK_LAST_UPDATED_COLUMN = "bank_updated_timestamp_with_time_zone"
}

object CreditConfigurationTableDescription {
    const val CREDIT_CONFIGURATION_TABLE = "credit_configuration"

    const val CREDIT_CONFIGURATION_UUID_COLUMN = "credit_configuration_uuid"
    const val CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS_COLUMN = "credit_configuration_min_amount_in_euros"
    const val CREDIT_CONFIGURATION_MAX_AMOUNT_IN_EUROS_COLUMN = "credit_configuration_max_amount_in_euros"
    const val CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS_COLUMN = "credit_configuration_min_term_in_months"
    const val CREDIT_CONFIGURATION_MAX_TERM_IN_MONTHS_COLUMN = "credit_configuration_max_term_in_months"
    const val CREDIT_CONFIGURATION_MIN_SCHUFA_RATING_COLUMN = "credit_configuration_min_schufa_rating"
    const val CREDIT_CONFIGURATION_MAX_SCHUFA_RATING_COLUMN = "credit_configuration_max_schufa_rating"
}
