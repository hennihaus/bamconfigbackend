package de.hennihaus.utils.validations

import io.konform.validation.Constraint
import io.konform.validation.ValidationBuilder

fun <T> ValidationBuilder<T>.oneOf(items: List<T>, errorMessage: String = "must exists"): Constraint<T> = addConstraint(
    errorMessage = errorMessage,
) {
    it in items
}

fun <T : Any, I : Any> ValidationBuilder<List<T>>.containsAll(
    items: List<I>,
    expectedItems: List<I>,
    fieldName: String,
): Constraint<List<T>> {
    val missing = expectedItems.filter { it !in items }

    return addConstraint(
        errorMessage = "must contain missing {0}: {1}",
        templateValues = arrayOf(
            fieldName,
            missing.joinToString(
                separator = "', '",
                prefix = "'",
                postfix = "'",
            ),
        ),
    ) {
        items.containsAll(elements = expectedItems)
    }
}

fun <T : Any, I : Any> ValidationBuilder<List<T>>.containsToMany(
    items: List<I>,
    expectedItems: List<I>,
    fieldName: String,
): Constraint<List<T>> {
    val overhead = items.filter { it !in expectedItems }

    return addConstraint(
        errorMessage = "must not contain {0}: {1}",
        templateValues = arrayOf(
            fieldName,
            overhead.joinToString(
                separator = "', '",
                prefix = "'",
                postfix = "'",
            ),
        ),
    ) {
        expectedItems.containsAll(elements = items)
    }
}
