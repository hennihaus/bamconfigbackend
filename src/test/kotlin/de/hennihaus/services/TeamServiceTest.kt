package de.hennihaus.services

import de.hennihaus.bamdatamodel.Team
import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.ASYNC_BANK_NAME
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getExampleTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getSecondTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getThirdTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getZeroStatistics
import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.models.cursors.TeamPagination
import de.hennihaus.models.cursors.TeamQuery
import de.hennihaus.objectmothers.CursorObjectMother.getFirstTeamCursorWithNoEmptyFields
import de.hennihaus.objectmothers.PaginationObjectMother.getTeamPaginationWithNoEmptyFields
import de.hennihaus.repositories.StatisticRepository
import de.hennihaus.repositories.TeamRepository
import de.hennihaus.services.TeamService.Companion.TEAM_NOT_FOUND_MESSAGE
import de.hennihaus.services.TeamService.Companion.USERNAME_POSITION_FALLBACK
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.beInstanceOf
import io.ktor.server.plugins.NotFoundException
import io.mockk.Called
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class TeamServiceTest {

    private val teamRepository = mockk<TeamRepository>()
    private val statisticRepository = mockk<StatisticRepository>()
    private val taskService = mockk<TaskService>()
    private val githubService = mockk<GithubService>()
    private val cursorService = mockk<CursorService>()

    private val classUnderTest = TeamService(
        teamRepository = teamRepository,
        statisticRepository = statisticRepository,
        taskService = taskService,
        githubService = githubService,
        cursorService = cursorService,
    )

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class GetAllTeams {
        @Test
        fun `should return a list of teams sorted by username asc`() = runBlocking {
            coEvery { teamRepository.getAll(cursor = any()) } returns listOf(
                getSecondTeam(),
                getFirstTeam(),
            )
            every {
                cursorService.buildPagination<TeamQuery, Team>(
                    cursor = any(),
                    positionSupplier = any(),
                    positionFallback = any(),
                    items = any(),
                    limit = any(),
                )
            } returns getTeamPaginationWithNoEmptyFields()
            val cursor = getFirstTeamCursorWithNoEmptyFields()

            val result: TeamPagination = classUnderTest.getAllTeams(
                cursor = cursor,
            )

            result shouldBe getTeamPaginationWithNoEmptyFields()
            coVerifySequence {
                teamRepository.getAll(
                    cursor = cursor,
                )
                cursorService.buildPagination(
                    cursor = cursor,
                    positionSupplier = withArg {
                        it.invoke(getFirstTeam()) shouldBe getFirstTeam().username
                    },
                    positionFallback = USERNAME_POSITION_FALLBACK,
                    items = listOf(getFirstTeam(), getSecondTeam()),
                    limit = cursor.query.limit,
                )
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { teamRepository.getAll(cursor = any()) } throws Exception()
            val cursor = getFirstTeamCursorWithNoEmptyFields()

            val result = shouldThrow<Exception> {
                classUnderTest.getAllTeams(
                    cursor = cursor,
                )
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { teamRepository.getAll(cursor = cursor) }
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
    inner class IsTypeUnique {
        @Test
        fun `should return false when type is already in db and ids are different`() = runBlocking {
            val (id, type) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByType(type = any()) } returns getSecondTeam().uuid

            val result: Boolean = classUnderTest.isTypeUnique(id = "$id", type = type)

            result.shouldBeFalse()
            coVerify(exactly = 1) { teamRepository.getTeamIdByType(type = type) }
        }

        @Test
        fun `should return true when type is in database and ids are equal`() = runBlocking {
            val (id, type) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByType(type = any()) } returns getFirstTeam().uuid

            val result: Boolean = classUnderTest.isTypeUnique(id = "$id", type = type)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByType(type = type) }
        }

        @Test
        fun `should return true when type is not in database`() = runBlocking {
            val (id, type) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByType(type = any()) } returns null

            val result: Boolean = classUnderTest.isTypeUnique(id = "$id", type = type)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByType(type = type) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, type) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByType(type = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.isTypeUnique(id = "$id", type = type)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { teamRepository.getTeamIdByType(type = type) }
        }
    }

    @Nested
    inner class IsUsernameUnique {
        @Test
        fun `should return false when username is already in db and ids are different`() = runBlocking {
            val (id, _, username) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByUsername(username = username) } returns getSecondTeam().uuid

            val result: Boolean = classUnderTest.isUsernameUnique(id = "$id", username = username)

            result.shouldBeFalse()
            coVerify(exactly = 1) { teamRepository.getTeamIdByUsername(username = username) }
        }

        @Test
        fun `should return true when username is in database and ids are equal`() = runBlocking {
            val (id, _, username) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByUsername(username = any()) } returns getFirstTeam().uuid

            val result: Boolean = classUnderTest.isUsernameUnique(id = "$id", username = username)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByUsername(username = username) }
        }

        @Test
        fun `should return true when username is not in database`() = runBlocking {
            val (id, _, username) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByUsername(username = any()) } returns null

            val result: Boolean = classUnderTest.isUsernameUnique(id = "$id", username = username)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByUsername(username = username) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, _, username) = getFirstTeam()
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
            val (id, _, _, password) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByPassword(password = password) } returns getSecondTeam().uuid

            val result: Boolean = classUnderTest.isPasswordUnique(id = "$id", password = password)

            result.shouldBeFalse()
            coVerify(exactly = 1) { teamRepository.getTeamIdByPassword(password = password) }
        }

        @Test
        fun `should return true when password is in database and ids are equal`() = runBlocking {
            val (id, _, _, password) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByPassword(password = any()) } returns getFirstTeam().uuid

            val result: Boolean = classUnderTest.isPasswordUnique(id = "$id", password = password)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByPassword(password = password) }
        }

        @Test
        fun `should return true when password is not in database`() = runBlocking {
            val (id, _, _, password) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByPassword(password = any()) } returns null

            val result: Boolean = classUnderTest.isPasswordUnique(id = "$id", password = password)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByPassword(password = password) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, _, _, password) = getFirstTeam()
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
            val (id, _, _, _, jmsQueue) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByJmsQueue(jmsQueue = jmsQueue) } returns getSecondTeam().uuid

            val result: Boolean = classUnderTest.isJmsQueueUnique(id = "$id", jmsQueue = jmsQueue)

            result.shouldBeFalse()
            coVerify(exactly = 1) { teamRepository.getTeamIdByJmsQueue(jmsQueue = jmsQueue) }
        }

        @Test
        fun `should return true when jmsQueue is in database and ids are equal`() = runBlocking {
            val (id, _, _, _, jmsQueue) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByJmsQueue(jmsQueue = any()) } returns getFirstTeam().uuid

            val result: Boolean = classUnderTest.isJmsQueueUnique(id = "$id", jmsQueue = jmsQueue)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByJmsQueue(jmsQueue = jmsQueue) }
        }

        @Test
        fun `should return true when jmsQueue is not in database`() = runBlocking {
            val (id, _, _, _, jmsQueue) = getFirstTeam()
            coEvery { teamRepository.getTeamIdByJmsQueue(jmsQueue = any()) } returns null

            val result: Boolean = classUnderTest.isJmsQueueUnique(id = "$id", jmsQueue = jmsQueue)

            result.shouldBeTrue()
            coVerify(exactly = 1) { teamRepository.getTeamIdByJmsQueue(jmsQueue = jmsQueue) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, _, _, _, jmsQueue) = getFirstTeam()
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
        fun `should save and return a regular team with only positive statistic requests`() = runBlocking {
            val team = getFirstTeam(
                type = TeamType.REGULAR,
                statistics = getFirstTeam().statistics + (ASYNC_BANK_NAME to -1)
            )
            coEvery { teamRepository.save(entry = any(), repetitionAttempts = any()) } returns team

            val result: Team = classUnderTest.saveTeam(team = team)

            result shouldBe team
            coVerifySequence {
                teamRepository.save(
                    entry = getFirstTeam(),
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
            verify { listOf(taskService, githubService) wasNot Called }
        }

        @Test
        fun `should save, patch parameters, update open api and return an example team`() = runBlocking {
            val team = getExampleTeam(
                type = TeamType.EXAMPLE,
            )
            coEvery { teamRepository.save(entry = any(), repetitionAttempts = any()) } returns team
            coEvery { taskService.patchParameters(username = any(), password = any()) } returns Unit
            coEvery { githubService.updateOpenApi(team = any()) } returns Unit

            val result: Team = classUnderTest.saveTeam(team = team)

            result shouldBe team
            coVerifySequence {
                teamRepository.save(
                    entry = team,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
                taskService.patchParameters(
                    username = team.username,
                    password = team.password,
                )
                githubService.updateOpenApi(
                    team = team,
                )
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
        fun `should reset all teams`() = runBlocking {
            coEvery { teamRepository.resetAllTeams(repetitionAttempts = any()) } returns listOf(
                getFirstTeam().uuid,
                getSecondTeam().uuid,
                getThirdTeam().uuid,
            )

            val result: List<UUID> = classUnderTest.resetAllTeams()

            result shouldBe listOf(
                getFirstTeam().uuid,
                getSecondTeam().uuid,
                getThirdTeam().uuid,
            )
            coVerifySequence {
                teamRepository.resetAllTeams(repetitionAttempts = ONE_REPETITION_ATTEMPT)
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { teamRepository.resetAllTeams(repetitionAttempts = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.resetAllTeams()
            }

            result should beInstanceOf<Exception>()
            coVerifySequence {
                teamRepository.resetAllTeams(repetitionAttempts = ONE_REPETITION_ATTEMPT)
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
