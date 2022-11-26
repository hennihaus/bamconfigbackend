package de.hennihaus.repositories.types

import org.jetbrains.exposed.sql.ComparisonOp
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.ExpressionWithColumnType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.QueryParameter

infix fun <T : String?> ExpressionWithColumnType<T>.ilike(pattern: String): Op<Boolean> = InsensitiveLikeOp(
    expr1 = this,
    expr2 = QueryParameter(
        value = pattern,
        sqlType = columnType,
    ),
)

private class InsensitiveLikeOp(expr1: Expression<*>, expr2: Expression<*>) : ComparisonOp(
    expr1 = expr1,
    expr2 = expr2,
    opSign = "~*",
)
