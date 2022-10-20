package de.hennihaus.routes.mappers

import de.hennihaus.models.generated.rest.UniqueDTO

fun Boolean.toUniqueDTO() = UniqueDTO(isUnique = this)
