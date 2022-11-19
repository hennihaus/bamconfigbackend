package de.hennihaus.utils.validations

import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.hennihaus.models.cursors.Cursor
import de.hennihaus.utils.validations.RequestValidationUtils.DEFAULT_UNKNOWN_HTTP_STATUS_CODE
import de.hennihaus.utils.validations.RequestValidationUtils.HTTP_PROTOCOL
import de.hennihaus.utils.validations.RequestValidationUtils.TCP_PROTOCOL
import io.konform.validation.Constraint
import io.konform.validation.ValidationBuilder
import io.konform.validation.jsonschema.enum
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import org.apache.commons.validator.routines.EmailValidator
import org.apache.commons.validator.routines.UrlValidator
import java.util.Base64
import java.util.UUID

object RequestValidationUtils {
    const val DEFAULT_UNKNOWN_HTTP_STATUS_CODE = -1
    const val TCP_PROTOCOL = "tcp"
    const val HTTP_PROTOCOL = "http"
}

fun ValidationBuilder<String>.uuid(): Constraint<String> = addConstraint(
    errorMessage = "must have valid uuid format",
) {
    runCatching { UUID.fromString(it) }.map { true }.getOrElse { false }
}

fun ValidationBuilder<String>.url(): Constraint<String> = addConstraint(
    errorMessage = "must have valid url format",
) {
    it.replaceFirst(oldValue = TCP_PROTOCOL, newValue = HTTP_PROTOCOL).let { url ->
        UrlValidator.getInstance().isValid(url) or url.isEmpty()
    }
}

fun ValidationBuilder<String>.email(): Constraint<String> = addConstraint(
    errorMessage = "must have valid email format",
) {
    EmailValidator.getInstance(true).isValid(it)
}

fun ValidationBuilder<Int>.httpStatusCode(): Constraint<Int> = enum(
    allowed = (listOf(DEFAULT_UNKNOWN_HTTP_STATUS_CODE) + HttpStatusCode.allStatusCodes.map { it.value })
        .toTypedArray(),
)

fun ValidationBuilder<String>.contentType(): Constraint<String> = addConstraint(
    errorMessage = "must have valid content type format",
) {
    runCatching { ContentType.parse(value = it) }.map { true }.getOrElse { false }
}

fun ValidationBuilder<String>.json(): Constraint<String> = addConstraint(
    errorMessage = "must have valid json format",
) {
    runCatching { jacksonObjectMapper().readTree(it) }.map { it !is NullNode }.getOrElse { false }
}

fun <Query : Any> ValidationBuilder<String>.cursor(): Constraint<String> = addConstraint(
    errorMessage = "must have valid cursor",
) {
    runCatching { jacksonObjectMapper().readValue<Cursor<Query>>(src = Base64.getDecoder().decode(it)) }
        .map { true }
        .getOrElse { false }
}
