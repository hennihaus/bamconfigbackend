package de.hennihaus.repositories.queries

import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.models.cursors.Direction
import de.hennihaus.models.cursors.TeamCursor
import de.hennihaus.models.cursors.TeamQuery
import de.hennihaus.repositories.tables.BankTable
import de.hennihaus.repositories.tables.StatisticTable
import de.hennihaus.repositories.tables.StudentTable
import de.hennihaus.repositories.tables.TeamTable
import de.hennihaus.repositories.types.ilike
import de.hennihaus.services.CursorService.Companion.ONE_ITEM
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.Min
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Sum
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andHaving
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.longLiteral
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single

@Single
class TeamQueryBuilder {

    fun buildTeamPaginationQuery(cursor: TeamCursor): Query {
        val sqlQuery = TeamTable
            .slice(columns = TeamTable.columns)
            .select(
                where = buildWheres(
                    cursor = cursor,
                ),
            )
            .orderBy(
                column = TeamTable.username,
                order = cursor.direction.toSortOrder(),
            )
            .groupBy(columns = TeamTable.columns.toTypedArray())
            .limit(n = cursor.query.limit + ONE_ITEM)

        return sqlQuery.apply {
            adjustJoins(query = cursor.query)
            adjustSlices(query = cursor.query)
            adjustWheres(query = cursor.query)
            adjustHavings(query = cursor.query)
        }
    }

    private fun buildWheres(cursor: TeamCursor): Op<Boolean> {
        return buildCursorWhere(cursor = cursor)
            .and {
                TeamTable.username ilike (cursor.query.username ?: EMPTY)
            }
            .and {
                TeamTable.jmsQueue ilike (cursor.query.jmsQueue ?: EMPTY)
            }
    }

    private fun buildCursorWhere(cursor: TeamCursor): Op<Boolean> = cursor.run {
        when {
            // first cursor
            position.isEmpty() && direction == Direction.ASCENDING -> Op.build {
                TeamTable.username greater position
            }
            // previous cursor
            position.isNotEmpty() && direction == Direction.DESCENDING -> Op.build {
                TeamTable.username less position
            }
            // next cursor
            position.isNotEmpty() && direction == Direction.ASCENDING -> Op.build {
                TeamTable.username greater position
            }
            // last cursor
            position.isEmpty() && direction == Direction.DESCENDING -> Op.build {
                TeamTable.username greater position
            }
            // no cursor fits
            else -> throw IllegalArgumentException(
                "No query expression found for cursor with position=$position and direction=$direction"
            )
        }
    }

    private fun Direction.toSortOrder() = when (this) {
        Direction.ASCENDING -> SortOrder.ASC
        Direction.DESCENDING -> SortOrder.DESC
    }

    private fun Query.adjustJoins(query: TeamQuery) {
        if (query.studentFirstname.hasValue() || query.studentLastname.hasValue()) {
            adjustColumnSet {
                innerJoin(otherTable = StudentTable)
            }
        }
        if (query.banks.hasValue() || query.hasPassed.hasValue() || query.hasRequestValue()) {
            adjustColumnSet {
                innerJoin(otherTable = StatisticTable).innerJoin(otherTable = BankTable)
            }
        }
    }

    private fun Query.adjustSlices(query: TeamQuery) {
        if (query.hasRequestValue()) {
            adjustSlice {
                slice(set.fields + StatisticTable.requestsCount.totalRequests())
            }
        }
        if (query.hasPassed is Boolean) {
            adjustSlice {
                slice(set.fields + StatisticTable.requestsCount.hasPassed())
            }
        }
    }

    private fun Query.adjustWheres(query: TeamQuery) {
        if (query.type is TeamType) {
            andWhere {
                TeamTable.type eq query.type.name
            }
        }
        if (query.password is String && query.password.isNotEmpty()) {
            andWhere {
                TeamTable.password eq query.password
            }
        }
        if (query.banks is List<String> && query.banks.isNotEmpty()) {
            andWhere {
                query.banks.map { name -> BankTable.name eq name }.reduce { acc, op -> acc or op }
            }
        }
        if (query.studentFirstname is String && query.studentFirstname.isNotEmpty()) {
            andWhere {
                StudentTable.firstname ilike query.studentFirstname
            }
        }
        if (query.studentLastname is String && query.studentLastname.isNotEmpty()) {
            andWhere {
                StudentTable.lastname ilike query.studentLastname
            }
        }
    }

    private fun Query.adjustHavings(query: TeamQuery) {
        if (query.minRequests is Long && query.minRequests > ZERO) {
            andHaving {
                StatisticTable.requestsCount.totalRequests() greaterEq query.minRequests
            }
        }
        if (query.maxRequests is Long && query.maxRequests < Long.MAX_VALUE) {
            andHaving {
                StatisticTable.requestsCount.totalRequests() lessEq query.maxRequests
            }
        }
        if (query.hasPassed is Boolean) {
            andHaving {
                StatisticTable.requestsCount.hasPassed() eq if (query.hasPassed) ONE else ZERO
            }
        }
    }

    private fun Column<Long>.totalRequests() = Expression.build {
        val expr = case()
            .When(
                cond = BankTable.isActive eq true,
                result = this@totalRequests,
            )
            .Else(
                e = longLiteral(value = ZERO),
            )

        Sum(expr = expr, columnType = LongColumnType())
    }

    private fun Column<Long>.hasPassed() = Expression.build {
        val expr = case()
            .When(
                cond = BankTable.isActive eq true and (this@hasPassed greater ZERO),
                result = longLiteral(value = ONE)
            )
            .Else(
                e = longLiteral(value = ZERO),
            )

        Min(expr = expr, columnType = LongColumnType())
    }

    private fun TeamQuery.hasRequestValue() = when {
        minRequests is Long && minRequests > ZERO -> true
        maxRequests is Long && maxRequests < Long.MAX_VALUE -> true
        else -> false
    }

    private fun String?.hasValue() = this is String && isNotEmpty()

    private fun Boolean?.hasValue() = this is Boolean

    private fun List<*>?.hasValue() = this is List<*> && isNotEmpty()

    companion object {
        private const val EMPTY = ""
        private const val ONE = 1L
        private const val ZERO = 0L
    }
}
