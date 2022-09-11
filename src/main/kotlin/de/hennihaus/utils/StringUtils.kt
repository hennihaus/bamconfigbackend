package de.hennihaus.utils

import de.hennihaus.plugins.UUIDException
import java.util.UUID

inline fun <T> String.toUUID(op: (id: UUID) -> T): T {
    return runCatching { UUID.fromString(this) }
        .getOrElse { throw UUIDException() }
        .let { op(it) }
}
