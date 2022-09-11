package de.hennihaus.services

import de.hennihaus.models.generated.Statistic
import de.hennihaus.models.generated.Team
import de.hennihaus.objectmothers.BankObjectMother.getAsyncBank
import de.hennihaus.objectmothers.BankObjectMother.getSchufaBank
import de.hennihaus.objectmothers.BankObjectMother.getSyncBank
import de.hennihaus.objectmothers.StatisticObjectMother.getFirstTeamAsyncBankStatistic
import de.hennihaus.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.repositories.BankRepository
import de.hennihaus.repositories.StatisticRepository
import de.hennihaus.services.StatisticService.Companion.STATISTIC_NOT_FOUND_MESSAGE
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.booleans.shouldBeFalse
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

    private val statisticRepository = mockk<StatisticRepository>()
    private val bankRepository = mockk<BankRepository>()

    private val classUnderTest = StatisticService(
        statisticRepository = statisticRepository,
        bankRepository = bankRepository,
    )

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class IncrementRequest {
        @Test
        fun `should return incremented statistic when in database`() = runBlocking {
            val statistic = getFirstTeamAsyncBankStatistic()
            coEvery { statisticRepository.incrementRequest(entry = any()) } returns statistic.copy(
                requestsCount = 1L,
            )

            val result: Statistic = classUnderTest.incrementRequest(statistic = statistic)

            result shouldBe getFirstTeamAsyncBankStatistic(requestsCount = 1L)
            coVerify(exactly = 1) { statisticRepository.incrementRequest(entry = statistic) }
        }

        @Test
        fun `should throw an exception when not in database`() = runBlocking {
            val statistic = getFirstTeamAsyncBankStatistic()
            coEvery { statisticRepository.incrementRequest(entry = any()) } returns null

            val result = shouldThrowExactly<NotFoundException> {
                classUnderTest.incrementRequest(statistic = statistic)
            }

            result shouldHaveMessage STATISTIC_NOT_FOUND_MESSAGE
            coVerify(exactly = 1) { statisticRepository.incrementRequest(entry = statistic) }
        }
    }

    @Nested
    inner class SetHasPassed {
        @BeforeEach
        fun init() {
            coEvery { bankRepository.getAll() } returns listOf(
                getSchufaBank(),
                getSyncBank(),
                getAsyncBank()
            )
        }

        @Test
        fun `should set hasPassed = false when team has zero request statistics`() = runBlocking {
            val team = getFirstTeam(
                statistics = mapOf(
                    getSchufaBank().name to 0L,
                    getSyncBank().name to 0L,
                    getAsyncBank().name to 0L,
                ),
            )

            val result: Team = classUnderTest.setHasPassed(team = team)

            result shouldBe team
            result.hasPassed.shouldBeFalse()
        }

        @Test
        fun `should set hasPassed = false when team has one async bank with zero request statistics`() = runBlocking {
            val team = getFirstTeam(
                statistics = mapOf(
                    getSchufaBank().name to 1L,
                    getSyncBank().name to 1L,
                    getAsyncBank().name to 0L,
                )
            )

            val result: Team = classUnderTest.setHasPassed(team = team)

            result shouldBe team
            result.hasPassed.shouldBeFalse()
        }

        @Test
        fun `should set hasPassed = true when team has one request for every bank`() = runBlocking {
            val team = getFirstTeam(
                statistics = mapOf(
                    getSchufaBank().name to 1L,
                    getSyncBank().name to 1L,
                    getAsyncBank().name to 1L,
                )
            )

            val result: Team = classUnderTest.setHasPassed(team = team)

            result shouldBe team.copy(hasPassed = true)
        }

        @Test
        fun `should set hasPassed = true when team has one request for sync banks and async banks are deactivated`() =
            runBlocking {
                coEvery { bankRepository.getAll() } returns listOf(
                    getSchufaBank(),
                    getSyncBank(),
                    getAsyncBank(isActive = false)
                )
                val team = getFirstTeam(
                    statistics = mapOf(
                        getSchufaBank().name to 1L,
                        getSyncBank().name to 1L,
                        getAsyncBank().name to 0L,
                    )
                )

                val result: Team = classUnderTest.setHasPassed(team = team)

                result shouldBe team.copy(hasPassed = true)
            }

        @Test
        fun `should set hasPassed = false when team has zero requests for one sync banks`() = runBlocking {
            val team = getFirstTeam(
                statistics = mapOf(
                    getSchufaBank().name to 0L,
                    getSyncBank().name to 1L,
                    getAsyncBank().name to 1L,
                )
            )

            val result: Team = classUnderTest.setHasPassed(team = team)

            result shouldBe team.copy(hasPassed = false)
        }

        @Test
        fun `should set hasPassed = true when team has one request for all banks but one is missing`() = runBlocking {
            val team = getFirstTeam(
                statistics = mapOf(
                    getSchufaBank().name to 1L,
                    getSyncBank().name to 1L,
                )
            )

            val result: Team = classUnderTest.setHasPassed(team = team)

            result shouldBe team.copy(hasPassed = true)
        }

        @Test
        fun `should set hasPassed = false when team has one bank with no requests and one is missing`() = runBlocking {
            val team = getFirstTeam(
                statistics = mapOf(
                    getSchufaBank().name to 1L,
                    getSyncBank().name to 0L,
                )
            )

            val result: Team = classUnderTest.setHasPassed(team = team)

            result shouldBe team
            result.hasPassed.shouldBeFalse()
        }
    }
}
