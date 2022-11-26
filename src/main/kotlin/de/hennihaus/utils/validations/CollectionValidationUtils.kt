package de.hennihaus.utils.validations

import io.konform.validation.Constraint
import io.konform.validation.ValidationBuilder

fun <T> ValidationBuilder<T>.oneOf(items: List<T>, errorMessage: String = "must exists"): Constraint<T> = addConstraint(
    errorMessage = errorMessage,
) {
    it in items
}
