package de.hennihaus.configurations

import de.hennihaus.configurations.ExposedConfiguration.CONNECT_TIMEOUT_PARAM
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_DEFAULT_FETCH_SIZE
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_DEFAULT_ISOLATION_LEVEL
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_DEFAULT_REPETITION_ATTEMPTS
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_DRIVER
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_HOST
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_NAME
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_PASSWORD
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_PORT
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_PROTOCOL
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_TIMEOUT_IN_MILLISECONDS
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_TIMEOUT_WARNING_IN_MILLISECONDS
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_USER
import de.hennihaus.configurations.ExposedConfiguration.IsolationLevel
import de.hennihaus.configurations.ExposedConfiguration.REWRITE_BATCHED_INSERTS_PARAM
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val exposedModule = module {
    single {
        val protocol = getProperty<String>(key = DATABASE_PROTOCOL)
        val host = getProperty<String>(key = DATABASE_HOST)
        val port = getProperty<String>(key = DATABASE_PORT)
        val database = getProperty<String>(key = DATABASE_NAME)
        val user = getProperty<String>(key = DATABASE_USER)
        val password = getProperty<String>(key = DATABASE_PASSWORD)
        val driver = getProperty<String>(key = DATABASE_DRIVER)
        val timeoutInMilliseconds = getProperty<String>(key = DATABASE_TIMEOUT_IN_MILLISECONDS)

        Database.connect(
            url = buildString {
                append("$protocol://$host:$port/$database")
                append("?")
                append("$REWRITE_BATCHED_INSERTS_PARAM=${true}")
                append("&")
                append("$CONNECT_TIMEOUT_PARAM=$timeoutInMilliseconds")
            },
            driver = driver,
            user = user,
            password = password,
            databaseConfig = get(),
        )
    } withOptions {
        createdAtStart()
    }

    single {
        DatabaseConfig {
            val timeoutWarningInMilliseconds = getProperty<String>(key = DATABASE_TIMEOUT_WARNING_IN_MILLISECONDS)
            val defaultFetchSize = getProperty<String>(key = DATABASE_DEFAULT_FETCH_SIZE)
            val defaultRepetitionAttempts = getProperty<String>(key = DATABASE_DEFAULT_REPETITION_ATTEMPTS)
            val defaultIsolationLevel = getProperty<String>(key = DATABASE_DEFAULT_ISOLATION_LEVEL)

            this.sqlLogger = Slf4jSqlDebugLogger
            this.useNestedTransactions = false
            this.defaultFetchSize = defaultFetchSize.toInt()
            this.defaultIsolationLevel = IsolationLevel.valueOf(value = defaultIsolationLevel).isolationLevel
            this.defaultRepetitionAttempts = defaultRepetitionAttempts.toInt()
            this.warnLongQueriesDuration = timeoutWarningInMilliseconds.toLong()
        }
    } withOptions {
        createdAtStart()
    }
}

object ExposedConfiguration {
    const val ONE_REPETITION_ATTEMPT = 1

    const val DATABASE_PROTOCOL = "ktor.exposed.protocol"
    const val DATABASE_HOST = "ktor.exposed.host"
    const val DATABASE_PORT = "ktor.exposed.port"
    const val DATABASE_NAME = "ktor.exposed.database"
    const val DATABASE_USER = "ktor.exposed.user"
    const val DATABASE_PASSWORD = "ktor.exposed.password"
    const val DATABASE_DRIVER = "ktor.exposed.driver"
    const val DATABASE_TIMEOUT_IN_MILLISECONDS = "ktor.exposed.timeoutInMilliseconds"
    const val DATABASE_TIMEOUT_WARNING_IN_MILLISECONDS = "ktor.exposed.timeoutWarningInMilliseconds"
    const val DATABASE_AES_256_BIT_SALT = "ktor.exposed.aes256BitSalt"
    const val DATABASE_AES_PASSWORD = "ktor.exposed.aesPassword"
    const val DATABASE_DEFAULT_FETCH_SIZE = "ktor.exposed.defaultFetchSize"
    const val DATABASE_DEFAULT_REPETITION_ATTEMPTS = "ktor.exposed.defaultRepetitionAttempts"
    const val DATABASE_DEFAULT_ISOLATION_LEVEL = "ktor.exposed.defaultIsolationLevel"

    const val REWRITE_BATCHED_INSERTS_PARAM = "reWriteBatchedInserts"
    const val CONNECT_TIMEOUT_PARAM = "connectTimeout"

    enum class IsolationLevel(val isolationLevel: Int) {
        TRANSACTION_NONE(isolationLevel = 0),
        TRANSACTION_READ_UNCOMMITTED(isolationLevel = 1),
        TRANSACTION_READ_COMMITTED(isolationLevel = 2),
        TRANSACTION_REPEATABLE_READ(isolationLevel = 4),
        TRANSACTION_SERIALIZABLE(isolationLevel = 8),
    }
}
