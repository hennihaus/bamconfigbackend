package de.hennihaus.routes.validations

import de.hennihaus.bamdatamodel.RatingLevel
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getAsyncBank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getSchufaBank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getSyncBank
import de.hennihaus.bamdatamodel.objectmothers.CreditConfigurationObjectMother.getCreditConfigurationWithNoEmptyFields
import de.hennihaus.routes.mappers.toBankDTO
import de.hennihaus.routes.validations.BankValidationService.Companion.BANK_MUST_BE_ACTIVE
import de.hennihaus.routes.validations.BankValidationService.Companion.CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS
import de.hennihaus.routes.validations.BankValidationService.Companion.CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.clearAllMocks
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BankValidationServiceTest {

    private val classUnderTest = BankValidationService()

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class ValidateBody {
        @Test
        fun `should return an empty list when schufa bank is valid`() = runBlocking<Unit> {
            val body = getSchufaBank().toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return an empty list when synchronous bank is valid`() = runBlocking<Unit> {
            val body = getSyncBank().toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return an empty list when asynchronous bank is valid`() = runBlocking<Unit> {
            val body = getAsyncBank().toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return a list with one error when uuid is invalid`() = runBlocking {
            val body = getSchufaBank().toBankDTO().copy(
                uuid = "invalidUUID",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("uuid must have valid uuid format")
        }

        @Test
        fun `should return a list with one error when thumbnailUrl is invalid`() = runBlocking {
            val body = getSchufaBank().toBankDTO().copy(
                thumbnailUrl = "invalidUrl",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("thumbnailUrl must have valid url format")
        }

        @Test
        fun `should return a list with one error when updatedAt is invalid`() = runBlocking {
            val body = getSchufaBank().toBankDTO().copy(
                updatedAt = "invalidLocalDateTime",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("updatedAt must be ISO Local Date and Time e.g. '2011-12-03T10:15:30'")
        }

        @Test
        fun `should return a list with one error when isActive = false and isAsync = false`() = runBlocking {
            val body = getSchufaBank(isActive = false, isAsync = false).toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("isActive must be '$BANK_MUST_BE_ACTIVE'")
        }

        @Test
        fun `should return a list with one error when asynchronous bank has no credit config`() = runBlocking {
            val body = getAsyncBank(creditConfiguration = null).toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("creditConfiguration is required")
        }

        @Test
        fun `should return a list with one error when minAmountInEuros is negative`() = runBlocking {
            val creditConfiguration = getCreditConfigurationWithNoEmptyFields(
                minAmountInEuros = CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS.dec(),
            )
            val body = getAsyncBank(creditConfiguration = creditConfiguration).toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "creditConfiguration.minAmountInEuros must be at least '$CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS'",
            )
        }

        @Test
        fun `should return a list with one error when maxAmountInEuros is negative`() = runBlocking {
            val creditConfiguration = getCreditConfigurationWithNoEmptyFields(
                maxAmountInEuros = CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS.dec(),
            )
            val body = getAsyncBank(creditConfiguration = creditConfiguration).toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "creditConfiguration.maxAmountInEuros must be at least '$CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS'",
            )
        }

        @Test
        fun `should return a list with one error when maxAmount smaller minAmount`() = runBlocking {
            val creditConfiguration = getCreditConfigurationWithNoEmptyFields(
                minAmountInEuros = 1,
                maxAmountInEuros = 0,
            )
            val body = getAsyncBank(creditConfiguration = creditConfiguration).toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("creditConfiguration.maxAmountInEuros must be at least '1'")
        }

        @Test
        fun `should return a list with one error when minTermInMonths is negative`() = runBlocking {
            val creditConfiguration = getCreditConfigurationWithNoEmptyFields(
                minTermInMonths = CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS.dec(),
            )
            val body = getAsyncBank(creditConfiguration = creditConfiguration).toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "creditConfiguration.minTermInMonths must be at least '$CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS'",
            )
        }

        @Test
        fun `should return a list with one error when maxTermInMonths is negative`() = runBlocking {
            val creditConfiguration = getCreditConfigurationWithNoEmptyFields(
                maxTermInMonths = CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS.dec(),
            )
            val body = getAsyncBank(creditConfiguration = creditConfiguration).toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "creditConfiguration.maxTermInMonths must be at least '$CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS'",
            )
        }

        @Test
        fun `should return a list with one error when maxTerm smaller minTerm`() = runBlocking {
            val creditConfiguration = getCreditConfigurationWithNoEmptyFields(
                minTermInMonths = 1,
                maxTermInMonths = 0,
            )
            val body = getAsyncBank(creditConfiguration = creditConfiguration).toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("creditConfiguration.maxTermInMonths must be at least '1'")
        }

        @Test
        fun `should return a list with one error when minSchufaRating has no enum value`() = runBlocking {
            val creditConfiguration = getAsyncBank().toBankDTO().creditConfiguration!!.copy(
                minSchufaRating = "unknown",
            )
            val body = getAsyncBank().toBankDTO().copy(creditConfiguration = creditConfiguration)

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "creditConfiguration.minSchufaRating must be one of: 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'N', 'O', 'P'",
            )
        }

        @Test
        fun `should return a list with one error when maxSchufaRating has no enum value`() = runBlocking {
            val creditConfiguration = getAsyncBank().toBankDTO().creditConfiguration!!.copy(
                maxSchufaRating = "unknown",
            )
            val body = getAsyncBank().toBankDTO().copy(creditConfiguration = creditConfiguration)

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "creditConfiguration.maxSchufaRating must be one of: 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'N', 'O', 'P'",
            )
        }

        @Test
        fun `should return an empty list when minSchufaRating and maxSchufaRating are equal`() = runBlocking<Unit> {
            val creditConfiguration = getCreditConfigurationWithNoEmptyFields(
                minSchufaRating = RatingLevel.A,
                maxSchufaRating = RatingLevel.A,
            )
            val body = getAsyncBank(creditConfiguration = creditConfiguration).toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return a list with one error when maxRating smaller minRating`() = runBlocking {
            val creditConfiguration = getCreditConfigurationWithNoEmptyFields(
                minSchufaRating = RatingLevel.B,
                maxSchufaRating = RatingLevel.A,
            )
            val body = getAsyncBank(creditConfiguration = creditConfiguration).toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("creditConfiguration.maxSchufaRating must be at least 'B'")
        }
    }
}
