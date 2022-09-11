package de.hennihaus.services

import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.models.generated.Team
import de.hennihaus.objectmothers.BankObjectMother.ASYNC_BANK_NAME
import de.hennihaus.objectmothers.BankObjectMother.SCHUFA_BANK_NAME
import de.hennihaus.objectmothers.BankObjectMother.SYNC_BANK_NAME
import de.hennihaus.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.objectmothers.TeamObjectMother.getNonZeroStatistics
import de.hennihaus.objectmothers.TeamObjectMother.getSecondTeam
import de.hennihaus.objectmothers.TeamObjectMother.getThirdTeam
import de.hennihaus.objectmothers.TeamObjectMother.getZeroStatistics
import de.hennihaus.repositories.TeamRepository
import de.hennihaus.services.TeamService.Companion.TEAM_NOT_FOUND_MESSAGE
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.beInstanceOf
import io.ktor.server.plugins.NotFoundException
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class TeamServiceTest {

    private val repository = mockk<TeamRepository>()
    private val statistic = mockk<StatisticService>()
    private val passwordLength = "10"

    private val classUnderTest = TeamService(
        repository = repository,
        statistic = statistic,
        passwordLength = passwordLength,
    )

    @BeforeEach
    fun init() {
        clearAllMocks()
        coEvery { statistic.setHasPassed(team = any()) }
            .returns(returnValue = getFirstTeam())
            .andThen(returnValue = getSecondTeam())
            .andThen(returnValue = getThirdTeam())
    }

    @Nested
    inner class GetAllTeams {
        @Test
        fun `should return a list of teams sorted by username asc`() = runBlocking {
            coEvery { repository.getAll() } returns listOf(
                getSecondTeam(),
                getThirdTeam(),
                getFirstTeam(),
            )

            val result: List<Team> = classUnderTest.getAllTeams()

            result.shouldContainExactly(
                getFirstTeam(),
                getSecondTeam(),
                getThirdTeam(),
            )
            coVerifySequence {
                repository.getAll()
                statistic.setHasPassed(team = getFirstTeam())
                statistic.setHasPassed(team = getSecondTeam())
                statistic.setHasPassed(team = getThirdTeam())
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { repository.getAll() } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.getAllTeams() }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getAll() }
            coVerify(exactly = 0) { statistic.setHasPassed(team = any()) }
        }
    }

    @Nested
    inner class TeamById {
        @Test
        fun `should return tean when id is in database`() = runBlocking {
            val id = "${getFirstTeam().uuid}"
            coEvery { repository.getById(id = any()) } returns getFirstTeam()

            val result: Team = classUnderTest.getTeamById(id = id)

            result shouldBe getFirstTeam()
            coVerifySequence {
                repository.getById(id = UUID.fromString(id))
                statistic.setHasPassed(team = getFirstTeam())
            }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = "${UUID.randomUUID()}"
            coEvery { repository.getById(id = any()) } returns null

            val result = shouldThrowExactly<NotFoundException> {
                classUnderTest.getTeamById(id = id)
            }

            result shouldHaveMessage TEAM_NOT_FOUND_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = UUID.fromString(id)) }
            coVerify(exactly = 0) { statistic.setHasPassed(team = any()) }
        }
    }

    @Nested
    inner class CheckUsername {
        @Test
        fun `should return true when username is already in db and ids are different`() = runBlocking {
            val (id, username) = getFirstTeam()
            coEvery { repository.getTeamByUsername(username = username) } returns getSecondTeam()

            val result: Boolean = classUnderTest.checkUsername(id = "$id", username = username)

            result.shouldBeTrue()
            coVerify(exactly = 1) { repository.getTeamByUsername(username = username) }
        }

        @Test
        fun `should return false when username is in database and ids are equal`() = runBlocking {
            val (id, username) = getFirstTeam()
            coEvery { repository.getTeamByUsername(username = any()) } returns getFirstTeam()

            val result: Boolean = classUnderTest.checkUsername(id = "$id", username = username)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getTeamByUsername(username = username) }
        }

        @Test
        fun `should return false when username is not in database`() = runBlocking {
            val (id, username) = getFirstTeam()
            coEvery { repository.getTeamByUsername(username = any()) } returns null

            val result: Boolean = classUnderTest.checkUsername(id = "$id", username = username)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getTeamByUsername(username = username) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, username) = getFirstTeam()
            coEvery { repository.getTeamByUsername(username = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.checkUsername(id = "$id", username = username)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getTeamByUsername(username = username) }
        }
    }

    @Nested
    inner class CheckPassword {
        @Test
        fun `should return true when password is already in db and ids are different`() = runBlocking {
            val (id, _, password) = getFirstTeam()
            coEvery { repository.getTeamByPassword(password = password) } returns getSecondTeam()

            val result: Boolean = classUnderTest.checkPassword(id = "$id", password = password)

            result.shouldBeTrue()
            coVerify(exactly = 1) { repository.getTeamByPassword(password = password) }
        }

        @Test
        fun `should return false when password is in database and ids are equal`() = runBlocking {
            val (id, _, password) = getFirstTeam()
            coEvery { repository.getTeamByPassword(password = any()) } returns getFirstTeam()

            val result: Boolean = classUnderTest.checkPassword(id = "$id", password = password)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getTeamByPassword(password = password) }
        }

        @Test
        fun `should return false when password is not in database`() = runBlocking {
            val (id, _, password) = getFirstTeam()
            coEvery { repository.getTeamByPassword(password = any()) } returns null

            val result: Boolean = classUnderTest.checkPassword(id = "$id", password = password)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getTeamByPassword(password = password) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, _, password) = getFirstTeam()
            coEvery { repository.getTeamByPassword(password = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.checkPassword(id = "$id", password = password)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getTeamByPassword(password = password) }
        }
    }

    @Nested
    inner class CheckJmsQueue {
        @Test
        fun `should return true when jmsQueue is already in db and ids are different`() = runBlocking {
            val (id, _, _, jmsQueue) = getFirstTeam()
            coEvery { repository.getTeamByJmsQueue(jmsQueue = jmsQueue) } returns getSecondTeam()

            val result: Boolean = classUnderTest.checkJmsQueue(id = "$id", jmsQueue = jmsQueue)

            result.shouldBeTrue()
            coVerify(exactly = 1) { repository.getTeamByJmsQueue(jmsQueue = jmsQueue) }
        }

        @Test
        fun `should return false when jmsQueue is in database and ids are equal`() = runBlocking {
            val (id, _, _, jmsQueue) = getFirstTeam()
            coEvery { repository.getTeamByJmsQueue(jmsQueue = any()) } returns getFirstTeam()

            val result: Boolean = classUnderTest.checkJmsQueue(id = "$id", jmsQueue = jmsQueue)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getTeamByJmsQueue(jmsQueue = jmsQueue) }
        }

        @Test
        fun `should return false when jmsQueue is not in database`() = runBlocking {
            val (id, _, _, jmsQueue) = getFirstTeam()
            coEvery { repository.getTeamByJmsQueue(jmsQueue = any()) } returns null

            val result: Boolean = classUnderTest.checkJmsQueue(id = "$id", jmsQueue = jmsQueue)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getTeamByJmsQueue(jmsQueue = jmsQueue) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, _, _, jmsQueue) = getFirstTeam()
            coEvery { repository.getTeamByJmsQueue(jmsQueue = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.checkJmsQueue(id = "$id", jmsQueue = jmsQueue)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getTeamByJmsQueue(jmsQueue = jmsQueue) }
        }
    }

    @Nested
    inner class SaveTeam {
        @Test
        fun `should return and save a team`() = runBlocking {
            val testTeam = getFirstTeam()
            coEvery { repository.save(entry = any(), repetitionAttempts = any()) } returns testTeam

            val result: Team = classUnderTest.saveTeam(team = testTeam)

            result shouldBe testTeam
            coVerifySequence {
                statistic.setHasPassed(team = getFirstTeam())
                repository.save(entry = getFirstTeam(), repetitionAttempts = ONE_REPETITION_ATTEMPT)
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val testTeam = getFirstTeam()
            coEvery { statistic.setHasPassed(team = any()) } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.saveTeam(team = testTeam) }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { statistic.setHasPassed(team = getFirstTeam()) }
            coVerify(exactly = 0) { repository.save(entry = any(), repetitionAttempts = any()) }
        }
    }

    @Nested
    inner class DeleteTeamById {
        @Test
        fun `should delete a team by id and return true`() = runBlocking {
            val id = "${getFirstTeam().uuid}"
            coEvery { repository.deleteById(id = any()) } returns true

            val result: Boolean = classUnderTest.deleteTeamById(id = id)

            result.shouldBeTrue()
            coVerify(exactly = 1) { repository.deleteById(id = UUID.fromString(id)) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val id = "${getFirstTeam().uuid}"
            coEvery { repository.deleteById(id = any()) } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.deleteTeamById(id = id) }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.deleteById(id = UUID.fromString(id)) }
        }
    }

    @Nested
    inner class ResetAllTeams {
        @Test
        fun `should reset all team statistics, hasPassed and passwords`() = runBlocking {
            coEvery { repository.getAll() } returns listOf(
                getFirstTeam(statistics = getNonZeroStatistics(), hasPassed = true),
                getSecondTeam(statistics = getZeroStatistics(), hasPassed = false),
            )
            coEvery { repository.save(entry = any(), repetitionAttempts = any()) }
                .returns(returnValue = getFirstTeam())
                .andThen(returnValue = getSecondTeam())

            val result: List<Team> = classUnderTest.resetAllTeams()

            result shouldBe listOf(
                getFirstTeam(),
                getSecondTeam()
            )
            coVerifySequence {
                repository.getAll()
                repository.save(
                    entry = withArg {
                        it.shouldBeEqualToIgnoringFields(
                            other = getFirstTeam(statistics = getZeroStatistics(), hasPassed = false),
                            property = Team::password,
                        )
                        it.password shouldMatch Regex(pattern = "[a-zA-Z]{10}")
                    },
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
                repository.save(
                    entry = withArg {
                        it.shouldBeEqualToIgnoringFields(
                            other = getSecondTeam(statistics = getZeroStatistics(), hasPassed = false),
                            property = Team::password,
                        )
                        it.password shouldMatch Regex(pattern = "[a-zA-Z]{10}")
                    },
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { repository.getAll() } returns listOf(getFirstTeam())
            coEvery { repository.save(entry = any(), repetitionAttempts = any()) } throws NotFoundException(
                message = TEAM_NOT_FOUND_MESSAGE
            )

            val result = shouldThrowExactly<NotFoundException> {
                classUnderTest.resetAllTeams()
            }

            result shouldHaveMessage TEAM_NOT_FOUND_MESSAGE
            coVerifySequence {
                repository.getAll()
                repository.save(
                    entry = withArg {
                        it.shouldBeEqualToIgnoringFields(
                            other = getFirstTeam(),
                            property = Team::password,
                        )
                        it.password shouldMatch Regex(pattern = "[a-zA-Z]{10}")
                    },
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
        }
    }

    @Nested
    inner class ResetStatistics {
        @Test
        fun `should return team and reset all statistics for a team id`() = runBlocking {
            val id = "${getFirstTeam().uuid}"
            coEvery { repository.getById(id = any()) } returns getFirstTeam(
                statistics = mapOf(
                    SCHUFA_BANK_NAME to 1,
                    SYNC_BANK_NAME to 1,
                    ASYNC_BANK_NAME to 2,
                ),
                hasPassed = true,
            )
            coEvery { repository.save(entry = any(), repetitionAttempts = any()) } returns getFirstTeam(
                statistics = getZeroStatistics(),
                hasPassed = false,
            )
            coEvery { statistic.setHasPassed(team = any()) } returns getFirstTeam(
                hasPassed = false,
            )

            val result: Team = classUnderTest.resetStatistics(id = id)

            result shouldBe getFirstTeam(statistics = getZeroStatistics(), hasPassed = false)
            coVerifySequence {
                repository.getById(id = UUID.fromString(id))
                repository.save(
                    entry = getFirstTeam(
                        statistics = getZeroStatistics(),
                        hasPassed = true
                    ),
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
                statistic.setHasPassed(
                    team = getFirstTeam(
                        statistics = getZeroStatistics(),
                        hasPassed = false,
                    ),
                )
            }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = "${getFirstTeam().uuid}"
            coEvery { repository.getById(id = any()) } returns null

            val result = shouldThrowExactly<NotFoundException> {
                classUnderTest.resetStatistics(id = id)
            }

            result shouldHaveMessage TEAM_NOT_FOUND_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = UUID.fromString(id)) }
            coVerify(exactly = 0) { statistic.setHasPassed(team = any()) }
            coVerify(exactly = 0) { repository.save(entry = any(), repetitionAttempts = any()) }
        }
    }
}
