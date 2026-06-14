package de.hennihaus.routes

import de.hennihaus.models.Task
import de.hennihaus.models.generated.rest.ErrorsDTO
import de.hennihaus.models.generated.rest.UniqueDTO
import de.hennihaus.objectmothers.ErrorsObjectMother.getConflictErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getInternalServerErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getInvalidBankErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getInvalidIdErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getTaskNotFoundErrors
import de.hennihaus.objectmothers.ReasonObjectMother.INVALID_TASK_MESSAGE
import de.hennihaus.objectmothers.TaskObjectMother.getAsynchronousBankTask
import de.hennihaus.objectmothers.TaskObjectMother.getSchufaTask
import de.hennihaus.objectmothers.TaskObjectMother.getSynchronousBankTask
import de.hennihaus.plugins.TransactionException
import de.hennihaus.plugins.UUIDException
import de.hennihaus.routes.mappers.toTaskDTO
import de.hennihaus.routes.validations.TaskValidationService
import de.hennihaus.services.TaskService
import de.hennihaus.services.TaskService.Companion.TASK_NOT_FOUND_MESSAGE
import de.hennihaus.testutils.KtorTestUtils.testApplicationWith
import de.hennihaus.testutils.testClient
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.plugins.NotFoundException
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.util.UUID

class TaskRoutesTest {

    private val taskService = mockk<TaskService>()
    private val taskValidationService = mockk<TaskValidationService>()

    private val mockModule = module {
        single {
            taskService
        }
        single {
            taskValidationService
        }
    }

    @BeforeEach
    fun init() = clearAllMocks()

    @AfterEach
    fun tearDown() = stopKoin()

    @Nested
    inner class GetAllTasks {
        @Test
        fun `should return 200 and a list of tasks`() = testApplicationWith(mockModule) {
            coEvery { taskService.getAllTasks() } returns listOf(
                getSchufaTask(),
                getSynchronousBankTask(),
                getAsynchronousBankTask(),
            )

            val response = testClient.get(urlString = "/v1/tasks")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<List<Task>>().shouldContainExactly(
                getSchufaTask(),
                getSynchronousBankTask(),
                getAsynchronousBankTask(),
            )
            coVerify(exactly = 1) { taskService.getAllTasks() }
        }

        @Test
        fun `should return 200 and an empty list when no tasks available`() = testApplicationWith(mockModule) {
            coEvery { taskService.getAllTasks() } returns emptyList()

            val response = testClient.get(urlString = "/v1/tasks")

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldBe "[ ]"
            coVerify(exactly = 1) { taskService.getAllTasks() }
        }

        @Test
        fun `should return 500 and an error when exception occurs`() = testApplicationWith(mockModule) {
            coEvery { taskService.getAllTasks() } throws IllegalStateException()

            val response = testClient.get(urlString = "/v1/tasks")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ErrorsDTO>() shouldBe getInternalServerErrors()
            coVerify(exactly = 1) { taskService.getAllTasks() }
        }
    }

    @Nested
    inner class GetTaskById {
        @Test
        fun `should return 200 and a task by uuid`() = testApplicationWith(mockModule) {
            val uuid = "${getSchufaTask().uuid}"
            coEvery { taskService.getTaskById(id = any()) } returns getSchufaTask()

            val response = testClient.get(urlString = "/v1/tasks/$uuid")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Task>() shouldBe getSchufaTask()
            coVerify(exactly = 1) { taskService.getTaskById(id = uuid) }
        }

        @Test
        fun `should return 404 and not found error response when exception occurs`() = testApplicationWith(mockModule) {
            val uuid = "${UUID.randomUUID()}"
            coEvery { taskService.getTaskById(id = any()) } throws NotFoundException(message = TASK_NOT_FOUND_MESSAGE)

            val response = testClient.get(urlString = "/v1/tasks/$uuid")

            response shouldHaveStatus HttpStatusCode.NotFound
            response.body<ErrorsDTO>() shouldBe getTaskNotFoundErrors()
            coVerify(exactly = 1) { taskService.getTaskById(id = uuid) }
        }
    }

    @Nested
    inner class IsTitleUnique {
        @Test
        fun `should return 200 and true when title is unique`() = testApplicationWith(mockModule) {
            val (uuid, title) = getSchufaTask()
            coEvery { taskService.isTitleUnique(id = any(), title = any()) } returns true

            val response = testClient.get(urlString = "/v1/tasks/$uuid/unique/title/$title")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<UniqueDTO>() shouldBe UniqueDTO(isUnique = true)
            coVerify(exactly = 1) { taskService.isTitleUnique(id = "$uuid", title = title) }
        }

        @Test
        fun `should return 400 and an error response when uuid is invalid`() = testApplicationWith(mockModule) {
            val uuid = "invalidUUID"
            val title = getSchufaTask().title
            coEvery { taskService.isTitleUnique(id = any(), title = any()) } throws UUIDException()

            val response = testClient.get(urlString = "/v1/tasks/$uuid/unique/title/$title")

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidIdErrors()
            coVerify(exactly = 1) { taskService.isTitleUnique(id = uuid, title = title) }
        }
    }

    @Nested
    inner class PatchTask {
        @Test
        fun `should return 200 and a patched task`() = testApplicationWith(mockModule) {
            val testTask = getSchufaTask()
            coEvery { taskValidationService.validateBody(body = any()) } returns emptyList()
            coEvery { taskService.patchTask(id = any(), task = any()) } returns testTask

            val response = testClient.patch(urlString = "/v1/tasks/${testTask.uuid}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testTask)
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Task>() shouldBe testTask
            coVerifySequence {
                taskValidationService.validateBody(body = testTask.toTaskDTO())
                taskService.patchTask(id = "${testTask.uuid}", task = testTask)
            }
        }

        @Test
        fun `should return 400 and error response request body is invalid`() = testApplicationWith(mockModule) {
            val testTask = getSchufaTask().toTaskDTO().copy(
                uuid = "invalidUUID"
            )
            coEvery { taskValidationService.validateBody(body = any()) } returns listOf(INVALID_TASK_MESSAGE)

            val response = testClient.patch(urlString = "/v1/tasks/${testTask.uuid}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testTask)
            }

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidBankErrors()
            coVerify(exactly = 1) {
                taskValidationService.validateBody(body = testTask)
            }
            coVerify(exactly = 0) {
                taskService.patchTask(id = any(), task = any())
            }
        }

        @Test
        fun `should return 400 and error response when uuid is invalid`() = testApplicationWith(mockModule) {
            val uuid = "invalidUUID"
            val testTask = getSchufaTask()
            coEvery { taskValidationService.validateBody(body = any()) } returns emptyList()
            coEvery { taskService.patchTask(id = any(), task = any()) } throws UUIDException()

            val response = testClient.patch(urlString = "/v1/tasks/$uuid") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testTask)
            }

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidIdErrors()
            coVerifySequence {
                taskValidationService.validateBody(body = testTask.toTaskDTO())
                taskService.patchTask(id = uuid, task = testTask)
            }
        }

        @Test
        fun `should return 409 and an error response when transaction failed`() = testApplicationWith(mockModule) {
            val testTask = getSchufaTask()
            coEvery { taskValidationService.validateBody(body = any()) } returns emptyList()
            coEvery { taskService.patchTask(id = any(), task = any()) } throws TransactionException()

            val response = testClient.patch(urlString = "/v1/tasks/${testTask.uuid}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testTask)
            }

            response shouldHaveStatus HttpStatusCode.Conflict
            response.body<ErrorsDTO>() shouldBe getConflictErrors()
            coVerifySequence {
                taskValidationService.validateBody(body = testTask.toTaskDTO())
                taskService.patchTask(id = "${testTask.uuid}", task = testTask)
            }
        }
    }
}
