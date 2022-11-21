package de.hennihaus.routes.mappers

import de.hennihaus.configurations.RoutesConfiguration.CURSOR_QUERY_PARAMETER
import de.hennihaus.models.cursors.Cursor
import de.hennihaus.models.cursors.Direction
import de.hennihaus.models.cursors.TeamCursor
import de.hennihaus.services.TeamService.Companion.USERNAME_POSITION_FALLBACK
import io.ktor.http.Parameters
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.Base64

fun Cursor<*>.toCursorDTO(): String = ByteArrayOutputStream(512).let {
    ObjectOutputStream(it).use { objectOutput ->
        objectOutput.writeObject(this)
    }
    Base64.getUrlEncoder().encodeToString(it.toByteArray())
}

fun Parameters.toNativeCursor(): String? = get(name = CURSOR_QUERY_PARAMETER)

fun Parameters.toTeamCursor(): TeamCursor {
    return get(name = CURSOR_QUERY_PARAMETER)
        ?.let {
            Base64.getUrlDecoder().decode(it)
        }
        ?.let {
            ObjectInputStream(ByteArrayInputStream(it)).use { objectInput ->
                objectInput.readObject() as TeamCursor
            }
        }
        ?: toStartTeamCursor()
}

private fun Parameters.toStartTeamCursor(): TeamCursor = TeamCursor(
    position = USERNAME_POSITION_FALLBACK,
    direction = Direction.ASCENDING,
    query = toTeamQuery(),
)
