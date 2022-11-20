package de.hennihaus.services

import de.hennihaus.models.Pagination
import de.hennihaus.models.cursors.Cursor
import de.hennihaus.models.cursors.Direction
import org.koin.core.annotation.Single

@Single
class CursorService {

    fun <Query : Any, Item : Any> buildPagination(
        cursor: Cursor<Query>,
        positionSupplier: (Item) -> String,
        positionFallback: String,
        items: List<Item>,
        limit: Int,
    ): Pagination<Query, Item> = Pagination(
        first = cursor.buildAscendingCursor(
            position = positionFallback,
        ),
        prev = cursor.buildPrevCursor(
            positionSupplier = positionSupplier,
            items = items,
            limit = limit,
        ),
        next = cursor.buildNextCursor(
            positionSupplier = positionSupplier,
            items = items,
            limit = limit,
        ),
        last = cursor.buildDescendingCursor(
            position = positionFallback,
        ),
        items = cursor.sliceItems(
            items = items,
            limit = limit,
        ),
        query = cursor.query,
    )

    private fun <Query : Any, Item : Any> Cursor<Query>.buildPrevCursor(
        positionSupplier: (Item) -> String,
        items: List<Item>,
        limit: Int,
    ) = when {
        isLastCursor() and items.hasMoreItems(limit = limit) -> buildDescendingCursor(
            position = positionSupplier(items.drop(n = ONE_ITEM).first()),
        )
        isPrevCursor() and items.hasMoreItems(limit = limit) -> buildDescendingCursor(
            position = positionSupplier(items.drop(n = ONE_ITEM).first()),
        )
        isNextCursor() and items.isNotEmpty() -> buildDescendingCursor(
            position = positionSupplier(items.first()),
        )
        else -> null
    }

    private fun <Query : Any, Item : Any> Cursor<Query>.buildNextCursor(
        positionSupplier: (Item) -> String,
        items: List<Item>,
        limit: Int,
    ) = when {
        isFirstCursor() and items.hasMoreItems(limit = limit) -> buildAscendingCursor(
            position = positionSupplier(items.dropLast(n = ONE_ITEM).last()),
        )
        isNextCursor() and items.hasMoreItems(limit = limit) -> buildAscendingCursor(
            position = positionSupplier(items.dropLast(n = ONE_ITEM).last()),
        )
        isPrevCursor() and items.isNotEmpty() -> buildAscendingCursor(
            position = positionSupplier(items.last()),
        )
        else -> null
    }

    private fun <Item : Any> Cursor<*>.sliceItems(items: List<Item>, limit: Int): List<Item> {
        if (items.hasMoreItems(limit = limit).not()) {
            return items
        }
        return when (direction) {
            Direction.ASCENDING -> items.dropLast(
                n = ONE_ITEM,
            )
            Direction.DESCENDING -> items.drop(
                n = ONE_ITEM,
            )
        }
    }

    private fun <Query : Any> Cursor<Query>.buildAscendingCursor(position: String) = Cursor(
        position = position,
        direction = Direction.ASCENDING,
        queryHash = query.hashCode(),
        query = query,
    )

    private fun <Query : Any> Cursor<Query>.buildDescendingCursor(position: String) = Cursor(
        position = position,
        direction = Direction.DESCENDING,
        queryHash = query.hashCode(),
        query = query,
    )

    private fun List<*>.hasMoreItems(limit: Int) = size > limit

    private fun Cursor<*>.isFirstCursor() = position.isEmpty() and (direction == Direction.ASCENDING)

    private fun Cursor<*>.isPrevCursor() = position.isNotEmpty() and (direction == Direction.DESCENDING)

    private fun Cursor<*>.isNextCursor() = position.isNotEmpty() and (direction == Direction.ASCENDING)

    private fun Cursor<*>.isLastCursor() = position.isEmpty() and (direction == Direction.DESCENDING)

    companion object {
        const val ONE_ITEM = 1
    }
}