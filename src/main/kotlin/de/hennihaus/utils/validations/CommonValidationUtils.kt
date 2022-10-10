package de.hennihaus.utils.validations

import io.konform.validation.Constraint
import io.konform.validation.ValidationBuilder

fun <T : Any> ValidationBuilder<T>.notConst(notExpected: T): Constraint<T> = addConstraint(
    errorMessage = "must not be {0}",
    templateValues = arrayOf(notExpected.let { "'$it'" }),
) {
    if (it is String) it.filterNot { text -> text.isWhitespace() } != notExpected
    else it != notExpected
}

fun <T> ValidationBuilder<T>.unique(isUnique: Boolean, errorMessage: String = "must be unique"): Constraint<T> {
    return addConstraint(
        errorMessage = errorMessage,
    ) {
        isUnique
    }
}
