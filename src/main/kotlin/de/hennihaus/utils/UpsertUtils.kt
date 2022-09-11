package de.hennihaus.utils

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager

fun <T : Table> T.upsert(
    conflictColumns: List<Column<*>>,
    excludedColumnsFromUpdate: List<Column<*>> = emptyList(),
    upsert: T.(InsertStatement<Number>) -> Unit,
) = UpsertStatement<Number>(
    table = this,
    conflictColumns = conflictColumns,
    excludedColumnsFromUpdate = excludedColumnsFromUpdate,
).apply {
    upsert(this)
    execute(transaction = TransactionManager.current())
}

fun <T : Table, E> T.batchUpsert(
    data: Collection<E>,
    shouldReturnGeneratedValues: Boolean = false,
    conflictColumns: List<Column<*>>,
    excludedColumnsFromUpdate: List<Column<*>> = emptyList(),
    upsert: T.(BatchInsertStatement, E) -> Unit,
) {
    if (data.isEmpty()) return

    BatchUpsertStatement(
        table = this,
        shouldReturnGeneratedValues = shouldReturnGeneratedValues,
        conflictColumns = conflictColumns,
        excludedColumnsFromUpdate = excludedColumnsFromUpdate,
    ).apply {
        data.forEach {
            addBatch()
            upsert(this, it)
        }
        execute(transaction = TransactionManager.current())
    }
}

class UpsertStatement<Key : Any>(
    table: Table,
    private val conflictColumns: List<Column<*>>,
    private val excludedColumnsFromUpdate: List<Column<*>>,
) : InsertStatement<Key>(table = table) {

    override fun prepareSQL(transaction: Transaction) = buildString {
        append(super.prepareSQL(transaction = transaction))
        append(
            transaction.buildUpsertStatement(
                table = table,
                insertColumns = this@UpsertStatement.values.keys.toList(),
                conflictColumns = conflictColumns,
                excludedColumnsFromUpdate = excludedColumnsFromUpdate,
            )
        )
    }
}

class BatchUpsertStatement(
    table: Table,
    shouldReturnGeneratedValues: Boolean,
    private val conflictColumns: List<Column<*>>,
    private val excludedColumnsFromUpdate: List<Column<*>>,
) : BatchInsertStatement(table = table, shouldReturnGeneratedValues = shouldReturnGeneratedValues) {

    override fun prepareSQL(transaction: Transaction) = buildString {
        append(super.prepareSQL(transaction = transaction))
        append(
            transaction.buildUpsertStatement(
                table = table,
                insertColumns = this@BatchUpsertStatement.values.keys.toList(),
                conflictColumns = conflictColumns,
                excludedColumnsFromUpdate = excludedColumnsFromUpdate,
            )
        )
    }
}

private fun Transaction.buildUpsertStatement(
    table: Table,
    insertColumns: List<Column<*>>,
    conflictColumns: List<Column<*>>,
    excludedColumnsFromUpdate: List<Column<*>>,
) = buildString {
    if (conflictColumns.isNotEmpty()) {
        append(" ")
        append(buildOnConflictPart(conflictColumns = conflictColumns))
        append(" ")
        append(
            buildDoUpdateSetPart(
                table = table,
                insertColumns = insertColumns,
                excludedColumnsFromUpdate = excludedColumnsFromUpdate,
            )
        )
    }
}

private fun Transaction.buildDoUpdateSetPart(
    table: Table,
    insertColumns: List<Column<*>>,
    excludedColumnsFromUpdate: List<Column<*>>
): String {
    return "DO UPDATE SET " + table.columns
        .filter {
            it.name in insertColumns.map { column -> column.name }
        }
        .filter {
            it.name !in excludedColumnsFromUpdate.map { column -> column.name }
        }
        .joinToString {
            "${identity(column = it)} = EXCLUDED.${identity(column = it)}"
        }
}

private fun Transaction.buildOnConflictPart(conflictColumns: List<Column<*>>) = buildString {
    append("ON CONFLICT")
    append(" ")
    append("(")
    append(conflictColumns.joinToString { identity(column = it) })
    append(")")
}
