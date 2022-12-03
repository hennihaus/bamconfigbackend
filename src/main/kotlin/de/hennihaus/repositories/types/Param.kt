package de.hennihaus.repositories.types

import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.QueryParameter
import org.jetbrains.exposed.sql.UUIDColumnType
import java.util.UUID

fun uuidParam(value: UUID): Expression<UUID> = QueryParameter(
    value = value,
    sqlType = UUIDColumnType(),
)
