package de.hennihaus.testutils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import de.hennihaus.models.IntegrationStep
import de.hennihaus.plugins.ErrorMessage.INTEGRATION_STEP_NOT_FOUND_MESSAGE
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.NotFoundException

private const val HTTP_STATUS_CODE_FALLBACK = -1

object HttpStatusCodeSerializer : JsonSerializer<HttpStatusCode>() {
    override fun serialize(value: HttpStatusCode?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        value
            ?.also {
                gen?.writeNumber(it.value)
            }
            ?: run {
                gen?.writeNumber(HTTP_STATUS_CODE_FALLBACK)
            }
    }
}

object HttpStatusCodeDeserializer : JsonDeserializer<HttpStatusCode>() {
    override fun deserialize(parser: JsonParser?, ctxt: DeserializationContext?): HttpStatusCode {
        return parser
            ?.text
            ?.toIntOrNull()
            ?.let { HttpStatusCode.fromValue(value = it) }
            ?: HttpStatusCode.fromValue(value = HTTP_STATUS_CODE_FALLBACK)
    }
}

object ContentTypeSerializer : JsonSerializer<ContentType>() {
    override fun serialize(value: ContentType?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        value
            ?.also {
                gen?.writeString("$it")
            }
            ?: run {
                gen?.writeString("")
            }
    }
}

object ContentTypeDeserializer : JsonDeserializer<ContentType>() {
    override fun deserialize(parser: JsonParser?, ctxt: DeserializationContext?): ContentType {
        return parser
            ?.text
            ?.let { ContentType.parse(value = it) }
            ?: ContentType.Any
    }
}

object IntegrationStepSerializer : JsonSerializer<IntegrationStep>() {
    override fun serialize(value: IntegrationStep?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        value
            ?.also {
                gen?.writeNumber(it.value)
            }
            ?: run {
                gen?.writeNull()
            }
    }
}

object IntegrationStepDeserializer : JsonDeserializer<IntegrationStep>() {
    override fun deserialize(parser: JsonParser?, ctxt: DeserializationContext?): IntegrationStep {
        return parser
            ?.text
            ?.let { IntegrationStep.values().find { step -> step.value == it.toIntOrNull() } }
            ?: throw NotFoundException(
                message = INTEGRATION_STEP_NOT_FOUND_MESSAGE,
            )
    }
}
