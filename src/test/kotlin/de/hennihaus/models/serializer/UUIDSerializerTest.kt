package de.hennihaus.models.serializer

import io.kotest.matchers.shouldBe
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.util.UUID

class UUIDSerializerTest {

    @Serializable
    data class TestObject(@Serializable(with = UUIDSerializer::class) val uuid: UUID)

    @Test
    fun `should deserialize an object with UUID property correctly`() {
        val testJson = """{"uuid":"$TEST_UUID"}"""

        val result: TestObject = Json.decodeFromString(string = testJson)

        result shouldBe TestObject(uuid = UUID.fromString(TEST_UUID))
    }

    @Test
    fun `should serialize an object with UUID property correctly`() {
        val testObject = TestObject(uuid = UUID.fromString(TEST_UUID))

        val result: String = Json.encodeToString(value = testObject)

        result shouldBe """{"uuid":"$TEST_UUID"}"""
    }

    companion object {
        private const val TEST_UUID = "8d71b1a3-5c0f-4bf5-984c-124df4039cbf"
    }
}
