package de.hennihaus.models

import de.hennihaus.models.cursors.Cursor

data class Pagination<Query : Any, Item : Any>(
    val first: Cursor<Query>,
    val prev: Cursor<Query>?,
    val next: Cursor<Query>?,
    val last: Cursor<Query>,
    val query: Query,
    val items: List<Item>,
)
