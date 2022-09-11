package de.hennihaus.repositories.types

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.StringColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject

fun Table.jsonb(name: String): Column<String> = registerColumn(
    name = name,
    type = JsonColumnType(),
)

class JsonColumnType : StringColumnType() {
    override fun sqlType(): String = "jsonb"

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) = super.setParameter(
        stmt = stmt,
        index = index,
        value = PGobject().apply {
            this.type = sqlType()
            this.value = value as? String
        }
    )

    override fun valueFromDB(value: Any): String {
        require(value is PGobject) {
            "Unexpected value type ${value::class}"
        }
        return value.value ?: ""
    }
}
