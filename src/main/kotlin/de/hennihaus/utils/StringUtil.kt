package de.hennihaus.utils

import de.hennihaus.plugins.ObjectIdException
import org.bson.types.ObjectId

suspend fun <T> String.toObjectId(op: suspend (id: ObjectId) -> T): T {
    return runCatching { ObjectId(this) }
        .getOrElse { throw ObjectIdException() }
        .let { op(it) }
}
