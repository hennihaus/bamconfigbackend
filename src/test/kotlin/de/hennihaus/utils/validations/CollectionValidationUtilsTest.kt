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

    data class ContainsAllTestResource(val values: List<String>)

    data class ContainsToManyTestResource(val values: List<String>)

    @Nested
    inner class OneOf {

        private val classUnderTest = object : ValidationService<OneOfTestResource> {
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

    @Nested
    inner class ContainsAll {

        private lateinit var classUnderTest: ValidationService<ContainsAllTestResource>

        @Test
        fun `should return an empty list when all items are in expected items`() = runBlocking<Unit> {
            classUnderTest = object : ValidationService<ContainsAllTestResource> {
                override suspend fun bodyValidation(body: ContainsAllTestResource) = Validation {
                    ContainsAllTestResource::values {
                        containsAll(
                            items = listOf("first", "second"),
                            expectedItems = listOf("first", "second"),
                            fieldName = "values",
                        )
                    }
                }
            }
            val body = ContainsAllTestResource(
                values = emptyList(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return an empty list when all items are empty`() = runBlocking<Unit> {
            classUnderTest = object : ValidationService<ContainsAllTestResource> {
                override suspend fun bodyValidation(body: ContainsAllTestResource) = Validation {
                    ContainsAllTestResource::values {
                        containsAll(
                            items = emptyList(),
                            expectedItems = emptyList(),
                            fieldName = "values",
                        )
                    }
                }
            }
            val body = ContainsAllTestResource(
                values = emptyList(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return an empty list when items has more items than expected items`() = runBlocking<Unit> {
            classUnderTest = object : ValidationService<ContainsAllTestResource> {
                override suspend fun bodyValidation(body: ContainsAllTestResource) = Validation {
                    ContainsAllTestResource::values {
                        containsAll(
                            items = listOf("first", "second"),
                            expectedItems = listOf("first"),
                            fieldName = "values",
                        )
                    }
                }
            }
            val body = ContainsAllTestResource(
                values = emptyList(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return a list with one error when not all items are in expected items`() = runBlocking {
            classUnderTest = object : ValidationService<ContainsAllTestResource> {
                override suspend fun bodyValidation(body: ContainsAllTestResource) = Validation {
                    ContainsAllTestResource::values {
                        containsAll(
                            items = listOf("first"),
                            expectedItems = listOf("first", "second"),
                            fieldName = "values",
                        )
                    }
                }
            }
            val body = ContainsAllTestResource(
                values = emptyList(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("values must contain missing values: 'second'")
        }
    }

    @Nested
    inner class ContainsMany {

        private lateinit var classUnderTest: ValidationService<ContainsToManyTestResource>

        @Test
        fun `should return an empty list when all expected items are in items`() = runBlocking<Unit> {
            classUnderTest = object : ValidationService<ContainsToManyTestResource> {
                override suspend fun bodyValidation(body: ContainsToManyTestResource) = Validation {
                    ContainsToManyTestResource::values {
                        containsToMany(
                            items = listOf("first", "second"),
                            expectedItems = listOf("first", "second"),
                            fieldName = "values",
                        )
                    }
                }
            }
            val body = ContainsToManyTestResource(
                values = emptyList(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return an empty list when all items are empty`() = runBlocking<Unit> {
            classUnderTest = object : ValidationService<ContainsToManyTestResource> {
                override suspend fun bodyValidation(body: ContainsToManyTestResource) = Validation {
                    ContainsToManyTestResource::values {
                        containsToMany(
                            items = emptyList(),
                            expectedItems = emptyList(),
                            fieldName = "values",
                        )
                    }
                }
            }
            val body = ContainsToManyTestResource(
                values = emptyList(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return an empty list when expected items has more items than items`() = runBlocking<Unit> {
            classUnderTest = object : ValidationService<ContainsToManyTestResource> {
                override suspend fun bodyValidation(body: ContainsToManyTestResource) = Validation {
                    ContainsToManyTestResource::values {
                        containsToMany(
                            items = listOf("first"),
                            expectedItems = listOf("first", "second"),
                            fieldName = "values",
                        )
                    }
                }
            }
            val body = ContainsToManyTestResource(
                values = emptyList(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return a list with one error when not all expected items are in items`() = runBlocking {
            classUnderTest = object : ValidationService<ContainsToManyTestResource> {
                override suspend fun bodyValidation(body: ContainsToManyTestResource) = Validation {
                    ContainsToManyTestResource::values {
                        containsToMany(
                            items = listOf("first", "second"),
                            expectedItems = listOf("first"),
                            fieldName = "values",
                        )
                    }
                }
            }
            val body = ContainsToManyTestResource(
                values = emptyList(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("values must not contain values: 'second'")
        }
    }
}
