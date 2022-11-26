package de.hennihaus.models.cursors

import java.io.Serializable

data class Cursor<Query : Any>(val position: String, val direction: Direction, val query: Query) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 5613342611087870134L
    }
}

enum class Direction {
    ASCENDING,
    DESCENDING,
}
