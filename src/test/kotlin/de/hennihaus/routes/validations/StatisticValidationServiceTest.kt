package de.hennihaus.routes.validations

import de.hennihaus.bamdatamodel.objectmothers.StatisticObjectMother.getFirstTeamAsyncBankStatistic
import de.hennihaus.routes.mappers.toStatisticDTO
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StatisticValidationServiceTest {

    private val classUnderTest = StatisticValidationService()

    @Nested
    inner class ValidateBody {
        @Test
        fun `should return an empty list when statistic is valid`() = runBlocking<Unit> {
            val body = getFirstTeamAsyncBankStatistic().toStatisticDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return a list with one error when bankId is invalid uuid`() = runBlocking {
            val body = getFirstTeamAsyncBankStatistic().toStatisticDTO().copy(
                bankId = "invalidUUID",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("bankId must have valid uuid format")
        }

        @Test
        fun `should return a list with one error when teamId is invalid uuid`() = runBlocking {
            val body = getFirstTeamAsyncBankStatistic().toStatisticDTO().copy(
                teamId = "invalidUUID",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("teamId must have valid uuid format")
        }
    }
}
