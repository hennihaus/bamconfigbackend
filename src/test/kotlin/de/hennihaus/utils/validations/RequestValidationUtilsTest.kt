package de.hennihaus.utils.validations

import de.hennihaus.routes.validations.ValidationService
import io.konform.validation.Validation
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.UUIDVersion
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.uuid
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.collection
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import io.ktor.http.ContentType as KtorContentType
import io.ktor.http.HttpStatusCode as KtorHttpStatusCode

class RequestValidationUtilsTest {

    data class UUIDTestResource(val value: String)

    data class URLTestResource(val value: String)

    data class EmailTestResource(val value: String)

    data class LocalDateTimeTestResource(val value: String)

    data class HttpStatusCodeTestResource(val value: Int)

    data class ContentTypeTestResource(val value: String)

    data class JsonTestResource(val value: String)

    @Nested
    inner class UUID {

        private val classUnderTest = object : ValidationService<UUIDTestResource, Any> {
            override suspend fun bodyValidation(body: UUIDTestResource): Validation<UUIDTestResource> = Validation {
                UUIDTestResource::value {
                    uuid()
                }
            }
        }

        @Test
        fun `should return an empty list when UUID is valid`() = runBlocking<Unit> {
            checkAll(genA = Arb.uuid(uuidVersion = UUIDVersion.V4)) {
                val body = UUIDTestResource(
                    value = "$it",
                )

                val result: List<String> = classUnderTest.validateBody(
                    body = body,
                )

                result.shouldBeEmpty()
            }
        }

        @Test
        fun `should return a list with one error when UUID is empty`() = runBlocking {
            val body = UUIDTestResource(
                value = "",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("value must have valid uuid format")
        }

        @Test
        fun `should return a list with one error when UUID is invalid`() = runBlocking {
            val body = UUIDTestResource(
                value = "invalidUUID",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("value must have valid uuid format")
        }
    }

    @Nested
    inner class URL {

        private val classUnderTest = object : ValidationService<URLTestResource, Any> {
            override suspend fun bodyValidation(body: URLTestResource): Validation<URLTestResource> = Validation {
                URLTestResource::value {
                    url()
                }
            }
        }

        @Test
        fun `should return an empty list when URL with domain is valid`() = runBlocking<Unit> {
            val body = URLTestResource(
                value = "https://www.hsv.de:443/path",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return an empty list when URL with tcp protocol is valid`() = runBlocking<Unit> {
            val body = URLTestResource(
                value = "tcp://hsv.de:443/path",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return an empty list when URL with ip address is valid`() = runBlocking<Unit> {
            val body = URLTestResource(
                value = "https://0.0.0.0:443/path",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return an empty list when URL is empty`() = runBlocking<Unit> {
            val body = URLTestResource(
                value = "",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return a list with one error when URL is invalid`() = runBlocking {
            val body = URLTestResource(
                value = "invalidURL",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("value must have valid url format")
        }
    }

    @Nested
    inner class LocalDateTime {

        private val classUnderTest = object : ValidationService<LocalDateTimeTestResource, Any> {
            override suspend fun bodyValidation(body: LocalDateTimeTestResource) = Validation {
                LocalDateTimeTestResource::value {
                    localDateTime()
                }
            }
        }

        @Test
        fun `should return an empty list when localDateTime is valid`() = runBlocking<Unit> {
            checkAll(genA = Arb.localDateTime()) {
                val body = LocalDateTimeTestResource(
                    value = "$it",
                )

                val result: List<String> = classUnderTest.validateBody(
                    body = body,
                )

                result.shouldBeEmpty()
            }
        }

        @Test
        fun `should return a list with one error when localDateTime is invalid`() = runBlocking {
            val body = LocalDateTimeTestResource(
                value = "invalidLocalDateTime",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("value must be ISO Local Date and Time e.g. '2011-12-03T10:15:30'")
        }
    }

    @Nested
    inner class Email {

        private val classUnderTest = object : ValidationService<EmailTestResource, Any> {
            override suspend fun bodyValidation(body: EmailTestResource): Validation<EmailTestResource> = Validation {
                EmailTestResource::value {
                    email()
                }
            }
        }

        @Test
        fun `should return an empty list when email is valid`() = runBlocking<Unit> {
            checkAll(genA = Arb.email()) {
                val body = EmailTestResource(
                    value = it,
                )

                val result: List<String> = classUnderTest.validateBody(
                    body = body,
                )

                result.shouldBeEmpty()
            }
        }

        @Test
        fun `should return a list with one error when email is invalid`() = runBlocking<Unit> {
            val body = EmailTestResource(
                value = "invalidEmail",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("value must have valid email format")
        }
    }

    @Nested
    inner class HttpStatusCode {

        private val classUnderTest = object : ValidationService<HttpStatusCodeTestResource, Any> {
            override suspend fun bodyValidation(body: HttpStatusCodeTestResource) = Validation {
                HttpStatusCodeTestResource::value {
                    httpStatusCode()
                }
            }
        }

        @Test
        fun `should return an empty list when httpStatusCode is valid`() = runBlocking<Unit> {
            checkAll(genA = Exhaustive.collection(collection = KtorHttpStatusCode.allStatusCodes)) {
                val body = HttpStatusCodeTestResource(
                    value = it.value,
                )

                val result: List<String> = classUnderTest.validateBody(
                    body = body,
                )

                result.shouldBeEmpty()
            }
        }

        @Test
        fun `should return an empty list when httpStatusCode is default unknown`() = runBlocking<Unit> {
            val body = HttpStatusCodeTestResource(
                value = RequestValidationUtils.DEFAULT_UNKNOWN_HTTP_STATUS_CODE,
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return a list with one error when httpStatusCode is not valid`() = runBlocking {
            val body = HttpStatusCodeTestResource(
                value = Int.MIN_VALUE,
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "value must be one of: '-1', '100', '101', '102', '200', '201', '202', '203', '204', '205', '206', '207', '300', '301', '302', '303', '304', '305', '306', '307', '308', '400', '401', '402', '403', '404', '405', '406', '407', '408', '409', '410', '411', '412', '413', '414', '415', '416', '417', '422', '423', '424', '426', '429', '431', '500', '501', '502', '503', '504', '505', '506', '507'",
            )
        }
    }

    @Nested
    inner class ContentType {

        private val classUnderTest = object : ValidationService<ContentTypeTestResource, Any> {
            override suspend fun bodyValidation(body: ContentTypeTestResource) = Validation {
                ContentTypeTestResource::value {
                    contentType()
                }
            }
        }

        @Test
        fun `should return an empty list when contentType is valid`() = runBlocking<Unit> {
            val body = ContentTypeTestResource(
                value = "${KtorContentType.Application.Json}",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return a list with one error when contentType is invalid`() = runBlocking<Unit> {
            val body = ContentTypeTestResource(
                value = "invalidContentType",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("value must have valid content type format")
        }
    }

    @Nested
    inner class Json {

        private val classUnderTest = object : ValidationService<JsonTestResource, Any> {
            override suspend fun bodyValidation(body: JsonTestResource) = Validation {
                JsonTestResource::value {
                    json()
                }
            }
        }

        @Test
        fun `should return an empty list when json object is valid`() = runBlocking<Unit> {
            val body = JsonTestResource(
                value = "{\"email\": \"example@com\", \"name\": \"John\"}",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return an empty list when json object is empty`() = runBlocking<Unit> {
            val body = JsonTestResource(
                value = "{}",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return an empty list when json array is valid`() = runBlocking<Unit> {
            val body = JsonTestResource(
                value = "[{\"email\": \"example@com\", \"name\": \"John\"}]",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return an empty list when json array is empty`() = runBlocking<Unit> {
            val body = JsonTestResource(
                value = "[]",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return a list with one error when json is null`() = runBlocking {
            val body = JsonTestResource(
                value = "${null}",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("value must have valid json format")
        }

        @Test
        fun `should return a list with one error when json is primitive`() = runBlocking {
            val body = JsonTestResource(
                value = "primitiveValue",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("value must have valid json format")
        }
    }
}
