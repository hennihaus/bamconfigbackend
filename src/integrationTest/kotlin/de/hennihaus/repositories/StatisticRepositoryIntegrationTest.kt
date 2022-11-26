package de.hennihaus.repositories

import de.hennihaus.bamdatamodel.Statistic
import de.hennihaus.bamdatamodel.objectmothers.StatisticObjectMother.getFirstTeamAsyncBankStatistic
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getZeroStatistics
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_HOST
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_PORT
import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.objectmothers.CursorObjectMother.getFirstTeamCursorWithEmptyFields
import de.hennihaus.objectmothers.ExposedContainerObjectMother
import de.hennihaus.objectmothers.TeamQueryObjectMother.getTeamQueryWithEmptyFields
import de.hennihaus.plugins.initKoin
import de.hennihaus.repositories.StatisticRepository.Companion.ZERO_REQUESTS
import de.hennihaus.routes.validations.ValidationService.Companion.LIMIT_MAXIMUM
import de.hennihaus.testutils.containers.ExposedContainer
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAllValues
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeUnique
import io.kotest.matchers.longs.shouldBeZero
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StatisticRepositoryIntegrationTest : KoinTest {

    private val exposedContainer = ExposedContainer.INSTANCE
    private val teamRepository: TeamRepository by inject()

    private val classUnderTest: StatisticRepository by inject()

    @JvmField
    @RegisterExtension
    @Suppress("unused")
    val koinTestExtension = KoinTestExtension.create {
        initKoin(
            properties = mapOf(
                DATABASE_HOST to exposedContainer.host,
                DATABASE_PORT to exposedContainer.firstMappedPort.toString(),
            )
        )
    }

    @BeforeEach
    fun init() = ExposedContainer.resetState()

    @AfterAll
    fun cleanUp() = stopKoin()

    @Nested
    inner class IncrementRequest {
        @Test
        fun `should increase requestsCount by one when statistic with bankId and teamId available`() = runBlocking {
            val statistic = getFirstTeamAsyncBankStatistic(
                bankId = ExposedContainerObjectMother.BANK_UUID,
                teamId = ExposedContainerObjectMother.TEAM_UUID,
            )
            teamRepository.save(
                entry = getFirstTeam(statistics = getZeroStatistics()),
                repetitionAttempts = ONE_REPETITION_ATTEMPT,
            )

            val result: Statistic? = classUnderTest.incrementRequest(entry = statistic)

            result shouldBe statistic.copy(requestsCount = 1L)
        }

        @Test
        fun `should return null and not increase requestsCount when statistic not found by bankId`() = runBlocking {
            val statistic = getFirstTeamAsyncBankStatistic(
                bankId = ExposedContainerObjectMother.UNKNOWN_UUID,
                teamId = ExposedContainerObjectMother.TEAM_UUID,
            )

            val result: Statistic? = classUnderTest.incrementRequest(entry = statistic)

            teamRepository.getById(id = statistic.teamId)!!.statistics.forAllValues {
                it.shouldBeZero()
            }
            result.shouldBeNull()
        }

        @Test
        fun `should return null and not increase requestsCount when statistic not found by teamId`() = runBlocking {
            val statistic = getFirstTeamAsyncBankStatistic(
                bankId = ExposedContainerObjectMother.BANK_UUID,
                teamId = ExposedContainerObjectMother.UNKNOWN_UUID,
            )

            val result: Statistic? = classUnderTest.incrementRequest(entry = statistic)

            teamRepository.getAll(
                cursor = getFirstTeamCursorWithEmptyFields(
                    query = getTeamQueryWithEmptyFields(
                        limit = LIMIT_MAXIMUM,
                    ),
                ),
            ).forAll { team ->
                team.statistics.forAllValues {
                    it.shouldBeZero()
                }
            }
            result.shouldBeNull()
        }
    }

    @Nested
    inner class ResetRequests {
        @Test
        fun `should reset all requests to zero for a given team id`() = runBlocking<Unit> {
            val teamId = ExposedContainerObjectMother.TEAM_UUID

            val result: List<Statistic> = classUnderTest.resetRequests(
                teamId = teamId,
                repetitionAttempts = ONE_REPETITION_ATTEMPT,
            )

            result.shouldForAll {
                it.requestsCount shouldBe ZERO_REQUESTS
                it.teamId shouldBe teamId
            }
            result.shouldBeUnique()
        }

        @Test
        fun `should return an empty list when team id is unknown`() = runBlocking<Unit> {
            val teamId = ExposedContainerObjectMother.UNKNOWN_UUID

            val result: List<Statistic> = classUnderTest.resetRequests(
                teamId = teamId,
                repetitionAttempts = ONE_REPETITION_ATTEMPT,
            )

            result.shouldBeEmpty()
        }
    }
}
