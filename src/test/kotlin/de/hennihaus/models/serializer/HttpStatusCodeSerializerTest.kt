package de.hennihaus.models.serializer

import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class HttpStatusCodeSerializerTest {

    @Serializable
    data class TestObject(@Serializable(with = HttpStatusCodeSerializer::class) val httpStatusCode: HttpStatusCode)

    @Test
    fun `should deserialize an object with known HttpStatusCode property correctly`() {
        val testJson = """{"httpStatusCode":200}"""

        val result: TestObject = Json.decodeFromString(string = testJson)

        result shouldBe TestObject(httpStatusCode = HttpStatusCode.OK)
    }

    @Test
    fun `should deserialize an object with unknown HttpStatusCode property correctly`() {
        val testJson = """{"httpStatusCode":-1}"""

        val result: TestObject = Json.decodeFromString(string = testJson)

        result shouldBe TestObject(httpStatusCode = HttpStatusCode.fromValue(value = -1))
    }

    @Test
    fun `should serialize an object with known HttpStatusCode property correctly`() {
        val testObject = TestObject(httpStatusCode = HttpStatusCode.OK)

        val result: String = Json.encodeToString(value = testObject)

        result shouldBe """{"httpStatusCode":200}"""
    }

    @Test
    fun `should serialize an object with unknown HttpStatusCode property correctly`() {
        val testObject = TestObject(httpStatusCode = HttpStatusCode.fromValue(value = -1))

        val result: String = Json.encodeToString(value = testObject)

        result shouldBe """{"httpStatusCode":-1}"""
    }
}
