package de.hennihaus.testutils.containers

import de.hennihaus.repositories.tables.BankTable
import de.hennihaus.repositories.tables.ContactTable
import de.hennihaus.repositories.tables.CreditConfigurationTable
import de.hennihaus.repositories.tables.EndpointTable
import de.hennihaus.repositories.tables.ParameterTable
import de.hennihaus.repositories.tables.ResponseTable
import de.hennihaus.repositories.tables.StatisticTable
import de.hennihaus.repositories.tables.StudentTable
import de.hennihaus.repositories.tables.TaskParameterTable
import de.hennihaus.repositories.tables.TaskResponseTable
import de.hennihaus.repositories.tables.TaskTable
import de.hennihaus.repositories.tables.TeamTable
import de.hennihaus.repositories.types.JsonColumnType
import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.UUIDColumnType
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalDateTimeColumnType
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import java.sql.ResultSet

object ExposedContainer {

    private const val IMAGE_NAME = "hennihaus/bamconfigdb"
    private const val IMAGE_VERSION = "latest"
    private const val EXPOSED_PORT = 5_432

    private const val FIRST_COLUMN = 1
    private const val FIRST_ROW = 1
    private const val MARKER = "?"
    private const val CREATED_TIMESTAMP_COLUMN_SUFFIX = "_created_timestamp_with_time_zone"
    private const val UPDATED_TIMESTAMP_COLUMN_SUFFIX = "_updated_timestamp_with_time_zone"

    private const val TIMESTAMP_WITH_TIMEZONE_COLUMN_TYPE = "timestamptz"
    private const val BOOLEAN_COLUMN_TYPE = "bool"
    private const val INTEGER_COLUMN_TYPE = "int4"
    private const val SERIAL_COLUMN_TYPE = "serial"
    private const val LONG_COLUMN_TYPE = "int8"

    val INSTANCE by lazy {
        startExposedContainer()
    }

    private val state by lazy {
        listOf(
            getEntries(table = TeamTable.tableName),
            getEntries(table = StudentTable.tableName),
            getEntries(table = ContactTable.tableName),
            getEntries(table = TaskTable.tableName),
            getEntries(table = EndpointTable.tableName),
            getEntries(table = ParameterTable.tableName),
            getEntries(table = TaskParameterTable.tableName),
            getEntries(table = ResponseTable.tableName),
            getEntries(table = TaskResponseTable.tableName),
            getEntries(table = BankTable.tableName),
            getEntries(table = StatisticTable.tableName),
            getEntries(table = CreditConfigurationTable.tableName),
        )
    }

    /**
     * Necessary to execute @BeforeEach methods without return type in a single method expression
     */
    fun resetState(): Unit = state.forEach { (table, columns, arguments) ->
        resetEntity(
            table = table,
            columns = columns,
            arguments = arguments,
        )
    }

    private fun startExposedContainer() = GenericContainer<Nothing>("$IMAGE_NAME:$IMAGE_VERSION").apply {
        exposedPorts = listOf(
            EXPOSED_PORT
        )
        setWaitStrategy(Wait.forListeningPort())
        start()
    }

    private fun getEntries(table: String) = transaction {
        "SELECT * FROM $table".executeQuery(table = table)!!
    }

    private fun resetEntity(table: String, columns: List<String>, arguments: List<Pair<IColumnType, Any?>>) {
        val markers = buildMarkers(
            columns = columns.size,
            rows = arguments.size / columns.size,
        )
        transaction {
            "DELETE FROM $table".executeDelete()
            "INSERT INTO $table (${columns.joinToString()}) VALUES $markers".executeInsert(arguments = arguments)
        }
    }

    private fun String.executeQuery(table: String) = TransactionManager.current().exec(stmt = this) {
        val columns = it.buildColumns(table = table)
        val arguments = it.buildArguments(table = table)

        Triple(
            first = table,
            second = columns,
            third = arguments,
        )
    }

    private fun String.executeDelete(): Unit? = TransactionManager.current().exec(stmt = this)

    private fun String.executeInsert(arguments: List<Pair<IColumnType, Any?>>): Unit? {
        return TransactionManager.current().exec(
            stmt = this,
            args = arguments,
        )
    }

    private fun ResultSet.buildColumns(table: String): List<String> {
        return (FIRST_COLUMN..metaData.columnCount)
            .filter { column ->
                metaData.getColumnName(column) != "${table.lowercase()}$CREATED_TIMESTAMP_COLUMN_SUFFIX"
            }
            .filter { column ->
                metaData.getColumnName(column) != "${table.lowercase()}$UPDATED_TIMESTAMP_COLUMN_SUFFIX"
            }
            .map { column ->
                metaData.getColumnName(column)
            }
    }

    private fun ResultSet.buildArguments(table: String): List<Pair<IColumnType, Any?>> {
        val arguments = mutableListOf<Pair<IColumnType, Any?>>()

        while (next()) {
            arguments += (FIRST_COLUMN..metaData.columnCount)
                .filter { column ->
                    metaData.getColumnName(column) != "${table.lowercase()}$CREATED_TIMESTAMP_COLUMN_SUFFIX"
                }
                .filter { column ->
                    metaData.getColumnName(column) != "${table.lowercase()}$UPDATED_TIMESTAMP_COLUMN_SUFFIX"
                }
                .map { column ->
                    metaData.getColumnTypeName(column).toExposedColumnType() to getObject(column)
                }
        }

        return arguments.toList()
    }

    private fun buildMarkers(columns: Int, rows: Int): String = (FIRST_ROW..rows).joinToString {
        (FIRST_COLUMN..columns).joinToString(
            prefix = "(",
            postfix = ")",
            transform = { MARKER },
        )
    }

    private fun String.toExposedColumnType(): IColumnType = when (this.uppercase()) {
        UUIDColumnType().sqlType().uppercase() -> UUIDColumnType()
        TextColumnType().sqlType().uppercase() -> TextColumnType()
        BooleanColumnType().sqlType().uppercase() -> BooleanColumnType()
        JsonColumnType().sqlType().uppercase() -> TextColumnType()
        TIMESTAMP_WITH_TIMEZONE_COLUMN_TYPE.uppercase() -> KotlinLocalDateTimeColumnType()
        BOOLEAN_COLUMN_TYPE.uppercase() -> BooleanColumnType()
        INTEGER_COLUMN_TYPE.uppercase() -> IntegerColumnType()
        SERIAL_COLUMN_TYPE.uppercase() -> IntegerColumnType()
        LONG_COLUMN_TYPE.uppercase() -> LongColumnType()
        else -> throw IllegalArgumentException("Unknown column type name $this!")
    }
}
