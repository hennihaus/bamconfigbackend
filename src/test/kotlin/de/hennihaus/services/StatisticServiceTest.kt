package de.hennihaus.services

import de.hennihaus.bamdatamodel.Statistic
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getAsyncBank
import de.hennihaus.bamdatamodel.objectmothers.StatisticObjectMother.getFirstTeamAsyncBankStatistic
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
    inner class SaveStatistics {
        @Test
        fun `should save statistics by bank id`() = runBlocking {
            coEvery { repository.saveAll(bankId = any()) } returns Unit
            val (bankId) = getAsyncBank()

            classUnderTest.saveStatistics(bankId = "$bankId")

            coVerify(exactly = 1) { repository.saveAll(bankId = bankId) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { repository.saveAll(bankId = any()) } throws Exception()
            val (bankId) = getAsyncBank()

            shouldThrowExactly<Exception> {
                classUnderTest.saveStatistics(bankId = "$bankId")
            }

            coVerify(exactly = 1) { repository.saveAll(bankId = bankId) }
        }
    }

    @Nested
    inner class DeleteStatistics {
        @Test
        fun `should delete statistics by bank id`() = runBlocking {
            coEvery { repository.deleteAll(bankId = any()) } returns Unit
            val (bankId) = getAsyncBank()

            classUnderTest.deleteStatistics(bankId = "$bankId")

            coVerify(exactly = 1) { repository.deleteAll(bankId = bankId) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { repository.deleteAll(bankId = any()) } throws Exception()
            val (bankId) = getAsyncBank()

            shouldThrowExactly<Exception> {
                classUnderTest.deleteStatistics(bankId = "$bankId")
            }

            coVerify(exactly = 1) { repository.deleteAll(bankId = bankId) }
        }
    }

    @Nested
    inner class RecreateStatistics {
        @Test
        fun `should recreate statistics when limit greater zero`() = runBlocking {
            coEvery { repository.recreateAll(limit = any()) } returns Unit
            val limit = 5L

            classUnderTest.recreateStatistics(limit = limit)

            coVerify(exactly = 1) { repository.recreateAll(limit = limit) }
        }

        @Test
        fun `should recreate statistics when limit smaller zero `() = runBlocking {
            coEvery { repository.recreateAll(limit = any()) } returns Unit
            val limit = -1L

            classUnderTest.recreateStatistics(limit = limit)

            coVerify(exactly = 1) { repository.recreateAll(limit = 0L) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { repository.recreateAll(limit = any()) } throws Exception()
            val limit = 5L

            shouldThrowExactly<Exception> {
                classUnderTest.recreateStatistics(limit = limit)
            }

            coVerify(exactly = 1) { repository.recreateAll(limit = limit) }
        }
    }

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
