package de.hennihaus.utils

import de.hennihaus.plugins.ErrorMessage
import de.hennihaus.plugins.UUIDException
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class StringUtilsTest {

    @Nested
    inner class ToUUID {
        @Test
        fun `should convert a string to an UUID`() = runBlocking {
            val string = TEST_UUID_STRING

            val result: UUID = string.toUUID { it }

            result shouldBe UUID.fromString(TEST_UUID_STRING)
        }

        @Test
        fun `should call passed operation with UUID`() = runBlocking {
            mockkObject(StringUtilsTest)
            every { testOperation(any()) } returns UUID.fromString(TEST_UUID_STRING)

            TEST_UUID_STRING.toUUID { testOperation(it) }

            verify(exactly = 1) { testOperation(id = UUID.fromString(TEST_UUID_STRING)) }
        }

        @Test
        fun `should throw an UUIDException when string is no valid UUID`() = runBlocking {
            val string = "invalidUUID"

            val result = shouldThrowExactly<UUIDException> { string.toUUID { it } }

            result shouldHaveMessage ErrorMessage.UUID_EXCEPTION_MESSAGE
        }
    }

    companion object {
        private const val TEST_UUID_STRING = "8d71b1a3-5c0f-4bf5-984c-124df4039cbf"

        fun testOperation(id: UUID) = id
    }
}
