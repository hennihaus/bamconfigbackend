package de.hennihaus.routes.validations

import de.hennihaus.objectmothers.CursorObjectMother
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ValidationServiceTest {

    data class TestQuery(val limit: Int)

    private val classUnderTest = object : ValidationService<Any, Any> {}

    @Nested
    inner class ValidateCursor {
        @Test
        fun `should return an empty list when cursor is valid`() {
            val cursor = CursorObjectMother.FIRST_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS

            val result: List<String> = classUnderTest.validateCursor<TestQuery>(
                cursor = cursor,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return one error when cursor is invalid`() {
            val cursor = "invalidCursor"

            val result: List<String> = classUnderTest.validateCursor<TestQuery>(
                cursor = cursor,
            )

            result shouldContainExactly listOf("request must have valid cursor")
        }
    }
}
