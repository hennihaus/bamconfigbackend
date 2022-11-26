package de.hennihaus.routes.mappers

import de.hennihaus.models.Pagination
import de.hennihaus.models.generated.rest.PaginationDTO

fun Pagination<*, *>.toPaginationDTO() = PaginationDTO(
    first = first.toCursorDTO(),
    prev = prev?.toCursorDTO(),
    next = next?.toCursorDTO(),
    last = last.toCursorDTO(),
)
