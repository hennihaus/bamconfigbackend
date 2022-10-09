package de.hennihaus.utils.validations

import de.hennihaus.routes.validations.ValidationService
import io.konform.validation.Validation
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CommonValidationUtilsTest {

    data class NotConstTestResource(val value: Any)

    data class NotExistTestResource(val value: String)

    @Nested
    inner class NotConst {

        private lateinit var classUnderTest: ValidationService<NotConstTestResource>

        @Test
        fun `should return an empty list when both values are not equal`() = runBlocking<Unit> {
            classUnderTest = object : ValidationService<NotConstTestResource> {
                override suspend fun bodyValidation(body: NotConstTestResource): Validation<NotConstTestResource> {
                    return Validation {
                        NotConstTestResource::value {
                            notConst(notExpected = 1)
                        }
                    }
                }
            }
            val body = NotConstTestResource(
                value = 0
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return a list with one error when both values are equal`() = runBlocking {
            classUnderTest = object : ValidationService<NotConstTestResource> {
                override suspend fun bodyValidation(body: NotConstTestResource): Validation<NotConstTestResource> {
                    return Validation {
                        NotConstTestResource::value {
                            notConst(notExpected = 1)
                        }
                    }
                }
            }
            val body = NotConstTestResource(
                value = 1
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("value must not be '1'")
        }

        @Test
        fun `should return a list with one error when both values are equal with whitespace`() = runBlocking {
            classUnderTest = object : ValidationService<NotConstTestResource> {
                override suspend fun bodyValidation(body: NotConstTestResource): Validation<NotConstTestResource> {
                    return Validation {
                        NotConstTestResource::value {
                            notConst(notExpected = "{equal}")
                        }
                    }
                }
            }
            val body = NotConstTestResource(
                value = """
                    {
                    equal
                    }
                """.trimIndent()
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("value must not be '{equal}'")
        }
    }

    @Nested
    inner class NotExist {

        private val classUnderTest = object : ValidationService<NotExistTestResource> {
            override suspend fun bodyValidation(body: NotExistTestResource): Validation<NotExistTestResource> {
                return Validation {
                    NotExistTestResource::value {
                        notExist(exist = false)
                    }
                }
            }
        }

        @Test
        fun `should return an empty list when exist = false`() = runBlocking<Unit> {
            val body = NotExistTestResource(
                value = "irrelevant",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }
    }
}
