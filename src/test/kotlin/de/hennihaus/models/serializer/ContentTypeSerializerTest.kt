package de.hennihaus.models.serializer

import io.kotest.matchers.shouldBe
import io.ktor.http.ContentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class ContentTypeSerializerTest {

    @Serializable
    data class TestObject(@Serializable(with = ContentTypeSerializer::class) val contentType: ContentType)

    @Test
    fun `should deserialize an object with ContentType property correctly`() {
        val testJson = """{"contentType":"application/json"}"""

        val result: TestObject = Json.decodeFromString(string = testJson)

        result shouldBe TestObject(contentType = ContentType.Application.Json)
    }

    @Test
    fun `should serialize an object with ContentType property correctly`() {
        val testObject = TestObject(contentType = ContentType.Application.Json)

        val result: String = Json.encodeToString(value = testObject)

        result shouldBe """{"contentType":"application/json"}"""
    }
}
