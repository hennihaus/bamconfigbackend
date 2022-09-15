package de.hennihaus.services

import de.hennihaus.models.generated.Statistic
import de.hennihaus.objectmothers.StatisticObjectMother.getFirstTeamAsyncBankStatistic
import de.hennihaus.repositories.StatisticRepository
import de.hennihaus.services.StatisticService.Companion.STATISTIC_NOT_FOUND_MESSAGE
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.ktor.server.plugins.NotFoundException
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StatisticServiceTest {

    private val repository = mockk<StatisticRepository>()

    private val classUnderTest = StatisticService(
        repository = repository,
    )

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class IncrementRequest {
        @Test
        fun `should return incremented statistic when in database`() = runBlocking {
            val statistic = getFirstTeamAsyncBankStatistic()
            coEvery { repository.incrementRequest(entry = any()) } returns statistic.copy(
                requestsCount = 1L,
            )

            val result: Statistic = classUnderTest.incrementRequest(statistic = statistic)

            result shouldBe getFirstTeamAsyncBankStatistic(requestsCount = 1L)
            coVerify(exactly = 1) { repository.incrementRequest(entry = statistic) }
        }

        @Test
        fun `should throw an exception when not in database`() = runBlocking {
            val statistic = getFirstTeamAsyncBankStatistic()
            coEvery { repository.incrementRequest(entry = any()) } returns null

            val result = shouldThrowExactly<NotFoundException> {
                classUnderTest.incrementRequest(statistic = statistic)
            }

            result shouldHaveMessage STATISTIC_NOT_FOUND_MESSAGE
            coVerify(exactly = 1) { repository.incrementRequest(entry = statistic) }
        }
    }
}
