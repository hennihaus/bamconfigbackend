package de.hennihaus.services

import de.hennihaus.bamdatamodel.Team
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.ASYNC_BANK_NAME
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getNonZeroStatistics
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getSecondTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getThirdTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getZeroStatistics
import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.repositories.StatisticRepository
import de.hennihaus.repositories.TeamRepository
import de.hennihaus.services.TeamService.Companion.TEAM_NOT_FOUND_MESSAGE
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldBeNull
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

    private val teamRepository = mockk<TeamRepository>()
    private val statisticRepository = mockk<StatisticRepository>()
    private val passwordLength = "10"

    private val classUnderTest = TeamService(
        teamRepository = teamRepository,
        statisticRepository = statisticRepository,
        passwordLength = passwordLength,
    )

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class GetAllTeams {
        @Test
        fun `should return a list of teams sorted by username asc`() = runBlocking {
            coEvery { teamRepository.getAll() } returns listOf(
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
                teamRepository.getAll()
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { teamRepository.getAll() } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.getAllTeams() }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { teamRepository.getAll() }
        }
    }

    @Nested
    inner class GetAllTeamIds {
        @Test
        fun `should return a list of teams ids`() = runBlocking {
            coEvery { teamRepository.getAllTeamIds() } returns listOf(
                getSecondTeam().uuid,
                getThirdTeam().uuid,
                getFirstTeam().uuid,
            )

            val result: List<UUID> = classUnderTest.getAllTeamIds()

            result.shouldContainExactly(
                getSecondTeam().uuid,
                getThirdTeam().uuid,
                getFirstTeam().uuid,
            )
            coVerifySequence {
                teamRepository.getAllTeamIds()
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { teamRepository.getAllTeamIds() } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.getAllTeamIds() }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { teamRepository.getAllTeamIds() }
        }
    }

    @Nested
    inner class TeamById {
        @Test
        fun `should return team when id is in database`() = runBlocking {
            val id = "${getFirstTeam().uuid}"
            coEvery { teamRepository.getById(id = any()) } returns getFirstTeam()

            val result: Team = classUnderTest.getTeamById(id = id)

            result shouldBe getFirstTeam()
            coVerifySequence {
                teamRepository.getById(id = UUID.fromString(id))
            }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = "${UUID.randomUUID()}"
            coEvery { teamRepository.getById(id = any()) } returns null

            val result = shouldThrowExactly<NotFoundException> {
                classUnderTest.getTeamById(id = id)
            }

            result shouldHaveMessage TEAM_NOT_FOUND_MESSAGE
            coVerify(exactly = 1) { teamRepository.getById(id = UUID.fromString(id)) }
        }
    }

    @Nested
    inner class IsUsernameUnique {
        @Test
        fun `should return false when username is already in db and ids are different`() = runBlocking {
            val (id, username) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByUsername(username = username) } returns getSecondTeam().uuid

            val result: Boolean = classUnderTest.isUsernameUnique(id = "$id", username = username)

            result.shouldBeFalse()
            coVerify(exactly = 1) { teamRepository.getTeamIdByUsername(username = username) }
        }

        @Test
        fun `should return true when username is in database and ids are equal`() = runBlocking {
            val (id, username) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByUsername(username = any()) } returns getFirstTeam().uuid

            val result: Boolean = classUnderTest.isUsernameUnique(id = "$id", username = username)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByUsername(username = username) }
        }

        @Test
        fun `should return true when username is not in database`() = runBlocking {
            val (id, username) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByUsername(username = any()) } returns null

            val result: Boolean = classUnderTest.isUsernameUnique(id = "$id", username = username)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByUsername(username = username) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, username) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByUsername(username = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.isUsernameUnique(id = "$id", username = username)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { teamRepository.getTeamIdByUsername(username = username) }
        }
    }

    @Nested
    inner class IsPasswordUnique {
        @Test
        fun `should return false when password is already in db and ids are different`() = runBlocking {
            val (id, _, password) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByPassword(password = password) } returns getSecondTeam().uuid

            val result: Boolean = classUnderTest.isPasswordUnique(id = "$id", password = password)

            result.shouldBeFalse()
            coVerify(exactly = 1) { teamRepository.getTeamIdByPassword(password = password) }
        }

        @Test
        fun `should return true when password is in database and ids are equal`() = runBlocking {
            val (id, _, password) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByPassword(password = any()) } returns getFirstTeam().uuid

            val result: Boolean = classUnderTest.isPasswordUnique(id = "$id", password = password)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByPassword(password = password) }
        }

        @Test
        fun `should return true when password is not in database`() = runBlocking {
            val (id, _, password) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByPassword(password = any()) } returns null

            val result: Boolean = classUnderTest.isPasswordUnique(id = "$id", password = password)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByPassword(password = password) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, _, password) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByPassword(password = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.isPasswordUnique(id = "$id", password = password)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { teamRepository.getTeamIdByPassword(password = password) }
        }
    }

    @Nested
    inner class IsJmsQueueUnique {
        @Test
        fun `should return false when jmsQueue is already in db and ids are different`() = runBlocking {
            val (id, _, _, jmsQueue) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByJmsQueue(jmsQueue = jmsQueue) } returns getSecondTeam().uuid

            val result: Boolean = classUnderTest.isJmsQueueUnique(id = "$id", jmsQueue = jmsQueue)

            result.shouldBeFalse()
            coVerify(exactly = 1) { teamRepository.getTeamIdByJmsQueue(jmsQueue = jmsQueue) }
        }

        @Test
        fun `should return true when jmsQueue is in database and ids are equal`() = runBlocking {
            val (id, _, _, jmsQueue) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByJmsQueue(jmsQueue = any()) } returns getFirstTeam().uuid

            val result: Boolean = classUnderTest.isJmsQueueUnique(id = "$id", jmsQueue = jmsQueue)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByJmsQueue(jmsQueue = jmsQueue) }
        }

        @Test
        fun `should return true when jmsQueue is not in database`() = runBlocking {
            val (id, _, _, jmsQueue) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByJmsQueue(jmsQueue = any()) } returns null

            val result: Boolean = classUnderTest.isJmsQueueUnique(id = "$id", jmsQueue = jmsQueue)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByJmsQueue(jmsQueue = jmsQueue) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, _, _, jmsQueue) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByJmsQueue(jmsQueue = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.isJmsQueueUnique(id = "$id", jmsQueue = jmsQueue)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { teamRepository.getTeamIdByJmsQueue(jmsQueue = jmsQueue) }
        }
    }

    @Nested
    inner class SaveTeam {
        @Test
        fun `should return and save a team with only positive statistic requests`() = runBlocking {
            val testTeam = getFirstTeam(
                statistics = getFirstTeam().statistics + (ASYNC_BANK_NAME to -1)
            )
            coEvery { teamRepository.save(entry = any(), repetitionAttempts = any()) } returns testTeam

            val result: Team = classUnderTest.saveTeam(team = testTeam)

            result shouldBe testTeam
            coVerifySequence {
                teamRepository.save(entry = getFirstTeam(), repetitionAttempts = ONE_REPETITION_ATTEMPT)
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val testTeam = getFirstTeam()
            coEvery { teamRepository.save(entry = any(), repetitionAttempts = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.saveTeam(team = testTeam)
            }

            result should beInstanceOf<Exception>()
            coVerifySequence {
                teamRepository.save(entry = getFirstTeam(), repetitionAttempts = ONE_REPETITION_ATTEMPT)
            }
        }
    }

    @Nested
    inner class GetJmsQueueById {
        @Test
        fun `should return a jmsQueue when is in database`() = runBlocking {
            val (id, _, _, jmsQueue) = getFirstTeam()
            coEvery { teamRepository.getJmsQueueById(id = any()) } returns jmsQueue

            val result: String? = classUnderTest.getJmsQueueById(
                id = "$id",
            )

            result shouldBe jmsQueue
            coVerifySequence {
                teamRepository.getJmsQueueById(id = id)
            }
        }

        @Test
        fun `should return null when jmsQueue is not in database`() = runBlocking {
            val id = getFirstTeam().uuid
            coEvery { teamRepository.getJmsQueueById(id = any()) } returns null

            val result: String? = classUnderTest.getJmsQueueById(
                id = "$id",
            )

            result.shouldBeNull()
            coVerifySequence {
                teamRepository.getJmsQueueById(id = id)
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val id = getFirstTeam().uuid
            coEvery { teamRepository.getJmsQueueById(id = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.getJmsQueueById(id = "$id")
            }

            result should beInstanceOf<Exception>()
            coVerifySequence {
                teamRepository.getJmsQueueById(id = id)
            }
        }
    }

    @Nested
    inner class DeleteTeamById {
        @Test
        fun `should delete a team by id and return true`() = runBlocking {
            val id = "${getFirstTeam().uuid}"
            coEvery { teamRepository.deleteById(id = any()) } returns true

            val result: Boolean = classUnderTest.deleteTeamById(id = id)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.deleteById(id = UUID.fromString(id)) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val id = "${getFirstTeam().uuid}"
            coEvery { teamRepository.deleteById(id = any()) } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.deleteTeamById(id = id) }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { teamRepository.deleteById(id = UUID.fromString(id)) }
        }
    }

    @Nested
    inner class ResetAllTeams {
        @Test
        fun `should reset all team statistics, hasPassed and passwords`() = runBlocking {
            coEvery { teamRepository.getAll() } returns listOf(
                getFirstTeam(statistics = getNonZeroStatistics(), hasPassed = true),
                getSecondTeam(statistics = getZeroStatistics(), hasPassed = false),
            )
            coEvery {
                teamRepository.save(
                    entry = any(), repetitionAttempts = any()
                )
            }.returns(returnValue = getFirstTeam()).andThen(returnValue = getSecondTeam())

            val result: List<Team> = classUnderTest.resetAllTeams()

            result shouldBe listOf(
                getFirstTeam(), getSecondTeam()
            )
            coVerifySequence {
                teamRepository.getAll()
                teamRepository.save(
                    entry = withArg {
                        it.shouldBeEqualToIgnoringFields(
                            other = getFirstTeam(statistics = getZeroStatistics(), hasPassed = false),
                            property = Team::password,
                        )
                        it.password shouldMatch Regex(pattern = "[a-zA-Z]{10}")
                    },
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
                teamRepository.save(
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
            coEvery { teamRepository.getAll() } returns listOf(getFirstTeam())
            coEvery { teamRepository.save(entry = any(), repetitionAttempts = any()) } throws NotFoundException(
                message = TEAM_NOT_FOUND_MESSAGE
            )

            val result = shouldThrowExactly<NotFoundException> {
                classUnderTest.resetAllTeams()
            }

            result shouldHaveMessage TEAM_NOT_FOUND_MESSAGE
            coVerifySequence {
                teamRepository.getAll()
                teamRepository.save(
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
            coEvery { statisticRepository.resetRequests(teamId = any(), repetitionAttempts = any()) } returns mockk()
            coEvery { teamRepository.getById(id = any()) } returns getFirstTeam(
                statistics = getZeroStatistics(),
                hasPassed = false,
            )

            val result: Team = classUnderTest.resetStatistics(id = id)

            result shouldBe getFirstTeam(statistics = getZeroStatistics(), hasPassed = false)
            coVerifySequence {
                statisticRepository.resetRequests(
                    teamId = UUID.fromString(id),
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
                teamRepository.getById(id = UUID.fromString(id))
            }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = "${getFirstTeam().uuid}"
            coEvery { statisticRepository.resetRequests(teamId = any(), repetitionAttempts = any()) } returns mockk()
            coEvery { teamRepository.getById(id = any()) } returns null

            val result = shouldThrowExactly<NotFoundException> {
                classUnderTest.resetStatistics(id = id)
            }

            result shouldHaveMessage TEAM_NOT_FOUND_MESSAGE
            coVerifySequence {
                statisticRepository.resetRequests(
                    teamId = UUID.fromString(id),
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
                teamRepository.getById(id = UUID.fromString(id))
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val id = "${getFirstTeam().uuid}"
            coEvery { statisticRepository.resetRequests(teamId = any(), repetitionAttempts = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.resetStatistics(id = id)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) {
                statisticRepository.resetRequests(
                    teamId = UUID.fromString(id),
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
            coVerify(exactly = 0) { teamRepository.getById(id = any()) }
        }
    }
}
