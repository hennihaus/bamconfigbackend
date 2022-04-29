package de.hennihaus.utils

import de.hennihaus.plugins.ObjectIdException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test

class StringUtilsTest {

    @Test
    fun `should convert a string to an ObjectId`() = runBlocking {
        val string = TEST_ID_STRING

        val result: ObjectId = string.toObjectId { it }

        result shouldBe ObjectId(string)
    }

    @Test
    fun `should call passed operation with ObjectId`() = runBlocking {
        mockkObject(StringUtilsTest)
        every { testOperation(any()) } returns ObjectId(TEST_ID_STRING)

        TEST_ID_STRING.toObjectId { testOperation(it) }

        verify(exactly = 1) { testOperation(id = ObjectId(TEST_ID_STRING)) }
    }

    @Test
    fun `should thrown an ObjectIdException when string is no valid ObjectId`() = runBlocking {
        val string = "invalidObjectId"

        val result = shouldThrow<ObjectIdException> { string.toObjectId { it } }

        result should beInstanceOf<ObjectIdException>()
    }

    companion object {
        private const val TEST_ID_STRING = "62482a4fed56b10980aceafa"

        fun testOperation(id: ObjectId) = id
    }
}
