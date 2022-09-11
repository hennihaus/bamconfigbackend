package de.hennihaus.utils

import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.plugins.TransactionException
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.exposedLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

private const val FIRST_TRANSACTION_ATTEMPT = 1
private const val TRANSACTION_ERROR_CODE = "40001"

suspend fun <T> inTransaction(
    repetitionAttempts: Int = TransactionManager.manager.defaultRepetitionAttempts,
    db: Database? = null,
    statement: suspend Transaction.() -> T,
): T {
    var lastException: ExposedSQLException? = null

    for (attempt in FIRST_TRANSACTION_ATTEMPT..repetitionAttempts) {
        try {
            return newSuspendedTransaction(
                context = Dispatchers.IO,
                db = db,
            ) {
                statement.invoke(this)
            }
        } catch (e: ExposedSQLException) {
            exposedLogger.warn("Exception while trying to execute transaction. Tries: $attempt")
            lastException = e
        }
    }

    if (repetitionAttempts == ONE_REPETITION_ATTEMPT && lastException?.sqlState == TRANSACTION_ERROR_CODE) {
        throw TransactionException(message = lastException.message)
    }
    throw lastException ?: RuntimeException("This should never happen")
}
