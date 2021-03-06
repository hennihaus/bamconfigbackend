package de.hennihaus.utils

import de.hennihaus.plugins.ObjectIdException
import org.bson.types.ObjectId

inline fun <T> String.toObjectId(op: (id: ObjectId) -> T): T {
    return runCatching { ObjectId(this) }
        .getOrElse { throw ObjectIdException() }
        .let { op(it) }
}
