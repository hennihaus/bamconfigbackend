package de.hennihaus.utils.validations

import de.hennihaus.routes.validations.ValidationService
import io.konform.validation.Validation
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CollectionValidationUtilsTest {

    data class OneOfTestResource(val value: String)

    @Nested
    inner class OneOf {

        private val classUnderTest = object : ValidationService<OneOfTestResource, Any> {
            override suspend fun bodyValidation(body: OneOfTestResource): Validation<OneOfTestResource> {
                return Validation {
                    OneOfTestResource::value {
                        oneOf(
                            items = listOf(
                                "firstItem",
                                "secondItem",
                            ),
                        )
                    }
                }
            }
        }

        @Test
        fun `should return an empty list when element is in item list`() = runBlocking<Unit> {
            val body = OneOfTestResource(
                value = "firstItem",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return a list with one error when element is not in item list`() = runBlocking {
            val body = OneOfTestResource(
                value = "unknownItem",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("value must exists")
        }
    }
}
