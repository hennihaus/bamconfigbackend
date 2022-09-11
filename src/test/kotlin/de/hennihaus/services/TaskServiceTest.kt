package de.hennihaus.services

import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.models.generated.IntegrationStep
import de.hennihaus.models.generated.Task
import de.hennihaus.objectmothers.TaskObjectMother.getAsynchronousBankTask
import de.hennihaus.objectmothers.TaskObjectMother.getSchufaTask
import de.hennihaus.objectmothers.TaskObjectMother.getSynchronousBankTask
import de.hennihaus.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.objectmothers.TeamObjectMother.getSecondTeam
import de.hennihaus.objectmothers.TeamObjectMother.getThirdTeam
import de.hennihaus.repositories.TaskRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
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

class TaskServiceTest {

    private val repository = mockk<TaskRepository>()
    private val statistic = mockk<StatisticService>()
    private val github = mockk<GithubService>()

    private val classUnderTest = TaskService(
        repository = repository,
        statistic = statistic,
        github = github,
    )

    @BeforeEach
    fun init() {
        clearAllMocks()
        coEvery { statistic.setHasPassed(team = any()) }
            .returns(returnValue = getFirstTeam())
            .andThen(returnValue = getSecondTeam())
            .andThen(returnValue = getThirdTeam())
            .andThen(returnValue = getFirstTeam())
            .andThen(returnValue = getSecondTeam())
            .andThen(returnValue = getThirdTeam())
            .andThen(returnValue = getFirstTeam())
            .andThen(returnValue = getSecondTeam())
            .andThen(returnValue = getThirdTeam())
    }

    @Nested
    inner class GetAllTasks {
        @Test
        fun `should return a list of tasks sorted by step asc`() = runBlocking {
            coEvery { repository.getAll() } returns listOf(
                getSynchronousBankTask(integrationStep = IntegrationStep.SYNC_BANK_STEP),
                getAsynchronousBankTask(integrationStep = IntegrationStep.ASYNC_BANK_STEP),
                getSchufaTask(integrationStep = IntegrationStep.SCHUFA_STEP),
            )

            val response: List<Task> = classUnderTest.getAllTasks()

            response.shouldContainExactly(
                getSchufaTask(integrationStep = IntegrationStep.SCHUFA_STEP),
                getSynchronousBankTask(integrationStep = IntegrationStep.SYNC_BANK_STEP),
                getAsynchronousBankTask(integrationStep = IntegrationStep.ASYNC_BANK_STEP),
            )
            coVerifySequence {
                repository.getAll()
                statistic.setHasPassed(team = getFirstTeam())
                statistic.setHasPassed(team = getSecondTeam())
                statistic.setHasPassed(team = getThirdTeam())
                statistic.setHasPassed(team = getFirstTeam())
                statistic.setHasPassed(team = getSecondTeam())
                statistic.setHasPassed(team = getThirdTeam())
                statistic.setHasPassed(team = getFirstTeam())
                statistic.setHasPassed(team = getSecondTeam())
                statistic.setHasPassed(team = getThirdTeam())
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { repository.getAll() } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.getAllTasks() }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getAll() }
            coVerify(exactly = 0) { statistic.setHasPassed(team = any()) }
        }
    }

    @Nested
    inner class GetTaskById {
        @Test
        fun `should return task when id is in database`() = runBlocking {
            val id = "${getAsynchronousBankTask().uuid}"
            coEvery { repository.getById(id = any()) } returns getAsynchronousBankTask()

            val result: Task = classUnderTest.getTaskById(id = id)

            result shouldBe getAsynchronousBankTask()
            coVerifySequence {
                repository.getById(id = UUID.fromString(id))
                statistic.setHasPassed(team = getFirstTeam())
                statistic.setHasPassed(team = getSecondTeam())
                statistic.setHasPassed(team = getThirdTeam())
            }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = "${getSchufaTask().uuid}"
            coEvery { repository.getById(id = any()) } returns null

            val result = shouldThrowExactly<NotFoundException> {
                classUnderTest.getTaskById(id = id)
            }

            result shouldHaveMessage TaskService.TASK_NOT_FOUND_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = UUID.fromString(id)) }
            coVerify(exactly = 0) { statistic.setHasPassed(team = any()) }
        }
    }

    @Nested
    inner class PatchTask {
        @Test
        fun `should just update six fields from a task`() = runBlocking {
            val id = "${getAsynchronousBankTask().uuid}"
            val task = getSchufaTask(
                title = getAsynchronousBankTask().title,
                description = getAsynchronousBankTask().description,
                isOpenApiVerbose = getAsynchronousBankTask().isOpenApiVerbose,
                contact = getAsynchronousBankTask().contact,
                parameters = getAsynchronousBankTask().parameters,
                responses = getAsynchronousBankTask().responses,
            )
            coEvery { repository.getById(id = any()) } returns getAsynchronousBankTask()
            coEvery { repository.save(entry = any(), repetitionAttempts = any()) } returns getAsynchronousBankTask()
            coEvery { github.updateOpenApi(task = any()) } returns Unit

            val result: Task = classUnderTest.patchTask(id = id, task = task)

            result shouldBe getAsynchronousBankTask()
            coVerifySequence {
                repository.getById(id = UUID.fromString(id))
                github.updateOpenApi(task = getAsynchronousBankTask())
                repository.save(entry = getAsynchronousBankTask(), repetitionAttempts = ONE_REPETITION_ATTEMPT)
                statistic.setHasPassed(team = getFirstTeam())
                statistic.setHasPassed(team = getSecondTeam())
                statistic.setHasPassed(team = getThirdTeam())
            }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = "${getSchufaTask().uuid}"
            val task = getSynchronousBankTask(
                title = getSchufaTask().title,
                description = getSchufaTask().description,
                isOpenApiVerbose = getSchufaTask().isOpenApiVerbose,
                contact = getSchufaTask().contact,
                parameters = getSchufaTask().parameters,
                responses = getSchufaTask().responses,
            )
            coEvery { repository.getById(id = any()) } returns null

            val result = shouldThrowExactly<NotFoundException> { classUnderTest.patchTask(id = id, task = task) }

            result shouldHaveMessage TaskService.TASK_NOT_FOUND_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = UUID.fromString(id)) }
            coVerify(exactly = 0) { github.updateOpenApi(task = any()) }
            coVerify(exactly = 0) { repository.save(entry = any(), repetitionAttempts = any()) }
        }
    }

    @Nested
    inner class CheckTitle {
        @Test
        fun `should return true when title is already in db and ids are different`() = runBlocking {
            val (id, title) = getSchufaTask()
            coEvery { repository.getTaskByTitle(title = title) } returns getAsynchronousBankTask()

            val result: Boolean = classUnderTest.checkTitle(id = "$id", title = title)

            result.shouldBeTrue()
            coVerify(exactly = 1) { repository.getTaskByTitle(title = title) }
        }

        @Test
        fun `should return false when title is in database and ids are equal`() = runBlocking {
            val (id, title) = getSchufaTask()
            coEvery { repository.getTaskByTitle(title = any()) } returns getSchufaTask()

            val result: Boolean = classUnderTest.checkTitle(id = "$id", title = title)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getTaskByTitle(title = title) }
        }

        @Test
        fun `should return false when title is not in database`() = runBlocking {
            val (id, title) = getSchufaTask()
            coEvery { repository.getTaskByTitle(title = any()) } returns null

            val result: Boolean = classUnderTest.checkTitle(id = "$id", title = title)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getTaskByTitle(title = title) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, title) = getSchufaTask()
            coEvery { repository.getTaskByTitle(title = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.checkTitle(id = "$id", title = title)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getTaskByTitle(title = title) }
        }
    }
}
