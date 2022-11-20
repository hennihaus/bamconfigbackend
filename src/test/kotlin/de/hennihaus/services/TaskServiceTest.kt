package de.hennihaus.services

import de.hennihaus.bamdatamodel.objectmothers.CreditConfigurationObjectMother.getCreditConfigurationWithNoEmptyFields
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.models.IntegrationStep
import de.hennihaus.models.Task
import de.hennihaus.objectmothers.ParameterObjectMother.getAmountInEurosParameter
import de.hennihaus.objectmothers.ParameterObjectMother.getPasswordParameter
import de.hennihaus.objectmothers.ParameterObjectMother.getTermInMonthsParameter
import de.hennihaus.objectmothers.ParameterObjectMother.getUsernameParameter
import de.hennihaus.objectmothers.TaskObjectMother.getAsynchronousBankTask
import de.hennihaus.objectmothers.TaskObjectMother.getSchufaTask
import de.hennihaus.objectmothers.TaskObjectMother.getSynchronousBankTask
import de.hennihaus.repositories.TaskRepository
import de.hennihaus.services.TaskService.Companion.AMOUNT_IN_EUROS_PARAMETER
import de.hennihaus.services.TaskService.Companion.PARAMETER_NOT_FOUND_MESSAGE
import de.hennihaus.services.TaskService.Companion.PASSWORD_PARAMETER
import de.hennihaus.services.TaskService.Companion.TERM_IN_MONTHS_PARAMETER
import de.hennihaus.services.TaskService.Companion.USERNAME_PARAMETER
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
    private val github = mockk<GithubService>()

    private val classUnderTest = TaskService(
        repository = repository,
        github = github,
    )

    @BeforeEach
    fun init() = clearAllMocks()

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
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { repository.getAll() } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.getAllTasks() }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getAll() }
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
    inner class PatchParameters {
        @Test
        fun `should update username and password parameter correctly`() = runBlocking {
            coEvery {
                repository.updateParameter(
                    name = any(),
                    example = any(),
                    repetitionAttempts = any(),
                )
            } returnsMany listOf(
                getUsernameParameter(),
                getPasswordParameter(),
            )
            val (_, _, username, password) = getFirstTeam()

            classUnderTest.patchParameters(
                username = username,
                password = password,
            )

            coVerifySequence {
                repository.updateParameter(
                    name = USERNAME_PARAMETER,
                    example = username,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
                repository.updateParameter(
                    name = PASSWORD_PARAMETER,
                    example = password,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
        }

        @Test
        fun `should throw an exception but update username when password parameter not found`() = runBlocking {
            coEvery {
                repository.updateParameter(
                    name = any(),
                    example = any(),
                    repetitionAttempts = any(),
                )
            } returnsMany listOf(
                getUsernameParameter(),
                null,
            )
            val username = getFirstTeam().username
            val password = "unknownPassword"

            val result: NotFoundException = shouldThrowExactly {
                classUnderTest.patchParameters(
                    username = username,
                    password = password,
                )
            }

            result shouldHaveMessage "$PARAMETER_NOT_FOUND_MESSAGE by $PASSWORD_PARAMETER"
        }

        @Test
        fun `should throw an exception but update password when username parameter not found`() = runBlocking {
            coEvery {
                repository.updateParameter(
                    name = any(),
                    example = any(),
                    repetitionAttempts = any(),
                )
            } returnsMany listOf(
                null,
                getPasswordParameter(),
            )
            val username = "unknownUsername"
            val password = getFirstTeam().password

            val result: NotFoundException = shouldThrowExactly {
                classUnderTest.patchParameters(
                    username = username,
                    password = password,
                )
            }

            result shouldHaveMessage "$PARAMETER_NOT_FOUND_MESSAGE by $USERNAME_PARAMETER"
        }

        @Test
        fun `should update amountInEuros and termInMonths parameter correctly`() = runBlocking {
            coEvery {
                repository.updateParameter(
                    name = any(),
                    example = any(),
                    repetitionAttempts = any(),
                )
            } returnsMany listOf(
                getAmountInEurosParameter(),
                getTermInMonthsParameter(),
            )
            val (minAmountInEuros, maxAmountInEuros, minTermInMonths, maxTermInMonths) = getCreditConfigurationWithNoEmptyFields()

            classUnderTest.patchParameters(
                minAmountInEuros = minAmountInEuros,
                maxAmountInEuros = maxAmountInEuros,
                minTermInMonths = minTermInMonths,
                maxTermInMonths = maxTermInMonths,
            )

            coVerifySequence {
                repository.updateParameter(
                    name = AMOUNT_IN_EUROS_PARAMETER,
                    example = "${30_000}",
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
                repository.updateParameter(
                    name = TERM_IN_MONTHS_PARAMETER,
                    example = "${21}",
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
        }

        @Test
        fun `should throw an exception but update amountInEuros when termInMonths parameter not found`() = runBlocking {
            coEvery {
                repository.updateParameter(
                    name = any(),
                    example = any(),
                    repetitionAttempts = any(),
                )
            } returnsMany listOf(
                getAmountInEurosParameter(),
                null,
            )
            val (minAmountInEuros, maxAmountInEuros, minTermInMonths, maxTermInMonths) = getCreditConfigurationWithNoEmptyFields()

            val result: NotFoundException = shouldThrowExactly {
                classUnderTest.patchParameters(
                    minAmountInEuros = minAmountInEuros,
                    maxAmountInEuros = maxAmountInEuros,
                    minTermInMonths = minTermInMonths,
                    maxTermInMonths = maxTermInMonths,
                )
            }

            result shouldHaveMessage "$PARAMETER_NOT_FOUND_MESSAGE by $TERM_IN_MONTHS_PARAMETER"
        }

        @Test
        fun `should throw an exception but update termInMonths when amountInEuros parameter not found`() = runBlocking {
            coEvery {
                repository.updateParameter(
                    name = any(),
                    example = any(),
                    repetitionAttempts = any(),
                )
            } returnsMany listOf(
                null,
                getTermInMonthsParameter(),
            )
            val (minAmountInEuros, maxAmountInEuros, minTermInMonths, maxTermInMonths) = getCreditConfigurationWithNoEmptyFields()

            val result: NotFoundException = shouldThrowExactly {
                classUnderTest.patchParameters(
                    minAmountInEuros = minAmountInEuros,
                    maxAmountInEuros = maxAmountInEuros,
                    minTermInMonths = minTermInMonths,
                    maxTermInMonths = maxTermInMonths,
                )
            }

            result shouldHaveMessage "$PARAMETER_NOT_FOUND_MESSAGE by $AMOUNT_IN_EUROS_PARAMETER"
        }
    }

    @Nested
    inner class GetAllParametersById {
        @Test
        fun `should return a list of parameterIds`() = runBlocking {
            val id = getSchufaTask().uuid
            coEvery { repository.getAllParametersById(id = any()) } returns getSchufaTask().parameters.map {
                it.uuid
            }

            val result: List<UUID> = classUnderTest.getAllParametersById(
                id = "$id",
            )

            result shouldContainExactly getSchufaTask().parameters.map { it.uuid }
            coVerifySequence {
                repository.getAllParametersById(id = id)
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val id = getSchufaTask().uuid
            coEvery { repository.getAllParametersById(id = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.getAllParametersById(id = "$id")
            }

            result should beInstanceOf<Exception>()
            coVerifySequence {
                repository.getAllParametersById(id = id)
            }
        }
    }

    @Nested
    inner class GetAllResponsesById {
        @Test
        fun `should return a list of responseIds`() = runBlocking {
            val id = getSchufaTask().uuid
            coEvery { repository.getAllResponsesById(id = any()) } returns getSchufaTask().responses.map {
                it.uuid
            }

            val result: List<UUID> = classUnderTest.getAllResponsesById(
                id = "$id",
            )

            result shouldContainExactly getSchufaTask().responses.map { it.uuid }
            coVerifySequence {
                repository.getAllResponsesById(id = id)
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val id = getSchufaTask().uuid
            coEvery { repository.getAllResponsesById(id = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.getAllResponsesById(id = "$id")
            }

            result should beInstanceOf<Exception>()
            coVerifySequence {
                repository.getAllResponsesById(id = id)
            }
        }
    }

    @Nested
    inner class IsTitleUnique {
        @Test
        fun `should return false when title is already in db and ids are different`() = runBlocking {
            val (id, title) = getSchufaTask()
            coEvery { repository.getTaskIdByTitle(title = title) } returns getAsynchronousBankTask().uuid

            val result: Boolean = classUnderTest.isTitleUnique(id = "$id", title = title)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getTaskIdByTitle(title = title) }
        }

        @Test
        fun `should return true when title is in database and ids are equal`() = runBlocking {
            val (id, title) = getSchufaTask()
            coEvery { repository.getTaskIdByTitle(title = any()) } returns getSchufaTask().uuid

            val result: Boolean = classUnderTest.isTitleUnique(id = "$id", title = title)

            result.shouldBeTrue()
            coVerify(exactly = 1) { repository.getTaskIdByTitle(title = title) }
        }

        @Test
        fun `should return true when title is not in database`() = runBlocking {
            val (id, title) = getSchufaTask()
            coEvery { repository.getTaskIdByTitle(title = any()) } returns null

            val result: Boolean = classUnderTest.isTitleUnique(id = "$id", title = title)

            result.shouldBeTrue()
            coVerify(exactly = 1) { repository.getTaskIdByTitle(title = title) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, title) = getSchufaTask()
            coEvery { repository.getTaskIdByTitle(title = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.isTitleUnique(id = "$id", title = title)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getTaskIdByTitle(title = title) }
        }
    }
}
