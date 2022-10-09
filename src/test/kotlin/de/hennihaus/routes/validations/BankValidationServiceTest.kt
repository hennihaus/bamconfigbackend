package de.hennihaus.routes.validations

import de.hennihaus.bamdatamodel.RatingLevel
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getAsyncBank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getDefaultTeams
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getSchufaBank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getSyncBank
import de.hennihaus.bamdatamodel.objectmothers.CreditConfigurationObjectMother.getCreditConfigurationWithNoEmptyFields
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getSecondTeam
import de.hennihaus.objectmothers.UUIDObjectMother
import de.hennihaus.routes.mappers.toBankDTO
import de.hennihaus.routes.mappers.toTeamDTO
import de.hennihaus.routes.validations.BankValidationService.Companion.BANK_MUST_BE_ACTIVE
import de.hennihaus.routes.validations.BankValidationService.Companion.CREDIT_CONFIGURATION_MIN_AMOUNT_IN_EUROS
import de.hennihaus.routes.validations.BankValidationService.Companion.CREDIT_CONFIGURATION_MIN_TERM_IN_MONTHS
import de.hennihaus.services.TeamService
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyAll
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class BankValidationServiceTest {

    private val team = mockk<TeamService>()

    private val classUnderTest = BankValidationService(
        team = team,
    )

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class ValidateBody {
        @BeforeEach
        fun init() {
            coEvery { team.getAllTeamIds() } returns getDefaultTeams().map { it.uuid }
        }

        @Test
        fun `should return an empty list when schufa bank is valid`() = runBlocking {
            val body = getSchufaBank().toBankDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
            coVerifyAll {
                team.getAllTeamIds()
            }
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
        fun `should return a list with one error when team elements are not unique`() = runBlocking {
            val bank = getSchufaBank().toBankDTO()
            val body = bank.copy(
                teams = bank.teams + bank.teams[0],
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("teams all items must be unique")
        }

        @Test
        fun `should return a list with one error when one team has an invalid uuid`() = runBlocking {
            val bank = getSchufaBank().toBankDTO()
            val body = bank.copy(
                teams = bank.teams + listOf(
                    getFirstTeam().toTeamDTO().copy(
                        uuid = "invalidUUID",
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("teams[3].uuid must have valid uuid format")
        }

        @Test
        fun `should return a list with one error when an old team is missing in synchronous bank `() {
            runBlocking {
                coEvery { team.getAllTeamIds() } returns listOf(getFirstTeam().uuid, getSecondTeam().uuid)
                val bank = getSyncBank().toBankDTO()
                val body = bank.copy(
                    teams = listOf(
                        getFirstTeam().toTeamDTO(),
                    ),
                )

                val result: List<String> = classUnderTest.validateBody(
                    body = body,
                )

                result shouldContainExactly listOf("teams must contain missing uuids: '${getSecondTeam().uuid}'")
                coVerify(exactly = 1) { team.getAllTeamIds() }
            }
        }

        @Test
        fun `should return a list with one error when one team is not in synchronous bank `() = runBlocking {
            coEvery { team.getAllTeamIds() } returns listOf(getFirstTeam().uuid)
            val bank = getSyncBank().toBankDTO()
            val body = bank.copy(
                teams = listOf(
                    getFirstTeam().toTeamDTO(),
                    getSecondTeam().toTeamDTO(),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("teams must not contain uuids: '${getSecondTeam().uuid}'")
        }

        @Test
        fun `should return a list with one error when one team not exists and bank is async`() = runBlocking {
            val bank = getAsyncBank().toBankDTO()
            val body = bank.copy(
                teams = listOf(
                    getFirstTeam(uuid = UUID.fromString(UUIDObjectMother.TEST_UUID_1)).toTeamDTO(),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("teams[0].uuid must exists")
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
