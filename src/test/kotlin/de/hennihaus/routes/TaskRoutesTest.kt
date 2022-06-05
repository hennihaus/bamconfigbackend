package de.hennihaus.routes

import de.hennihaus.models.Task
import de.hennihaus.objectmothers.ExceptionResponseObjectMother.INTERNAL_SERVER_ERROR_MESSAGE
import de.hennihaus.objectmothers.ExceptionResponseObjectMother.getInternalServerErrorResponse
import de.hennihaus.objectmothers.ExceptionResponseObjectMother.getInvalidIdErrorResponse
import de.hennihaus.objectmothers.ExceptionResponseObjectMother.getTaskNotFoundErrorResponse
import de.hennihaus.objectmothers.TaskObjectMother.getAsynchronousBankTask
import de.hennihaus.objectmothers.TaskObjectMother.getSchufaTask
import de.hennihaus.objectmothers.TaskObjectMother.getSynchronousBankTask
import de.hennihaus.plugins.ExceptionResponse
import de.hennihaus.plugins.ObjectIdException
import de.hennihaus.services.TaskService
import de.hennihaus.services.TaskServiceImpl.Companion.ID_MESSAGE
import de.hennihaus.testutils.KtorTestBuilder.testApplicationWith
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
import io.mockk.mockk
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.dsl.module

class TaskRoutesTest {

    private val taskService = mockk<TaskService>()

    private val mockModule = module { single { taskService } }

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class GetAllTasks {
        @Test
        fun `should return 200 and a list of tasks`() = testApplicationWith(mockModule) {
            coEvery { taskService.getAllTasks() } returns listOf(
                getSchufaTask(),
                getSynchronousBankTask(),
                getAsynchronousBankTask()
            )

            val response = testClient.get("/tasks")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<List<Task>>().shouldContainExactly(
                getSchufaTask(),
                getSynchronousBankTask(),
                getAsynchronousBankTask()
            )
            coVerify(exactly = 1) { taskService.getAllTasks() }
        }

        @Test
        fun `should return 200 and an empty list when no tasks available`() = testApplicationWith(mockModule) {
            coEvery { taskService.getAllTasks() } returns emptyList()

            val response = client.get("/tasks")

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldBe """
                [
                ]
            """.trimIndent()
            coVerify(exactly = 1) { taskService.getAllTasks() }
        }

        @Test
        fun `should return 500 and an exception response on error`() = testApplicationWith(mockModule) {
            coEvery { taskService.getAllTasks() } throws Exception(INTERNAL_SERVER_ERROR_MESSAGE)

            val response = testClient.get("/tasks")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ExceptionResponse>() shouldBe getInternalServerErrorResponse()
            coVerify(exactly = 1) { taskService.getAllTasks() }
        }
    }

    @Nested
    inner class GetTaskById {
        @Test
        fun `should return 200 and a task by id`() = testApplicationWith(mockModule) {
            val id = getSchufaTask().id.toString()
            coEvery { taskService.getTaskById(id = any()) } returns getSchufaTask()

            val response = testClient.get("/tasks/$id")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Task>() shouldBe getSchufaTask()
            coVerify(exactly = 1) { taskService.getTaskById(id = id) }
        }

        @Test
        fun `should return 404 and not found exception response on error`() = testApplicationWith(mockModule) {
            val id = ObjectId().toString()
            coEvery { taskService.getTaskById(id = any()) } throws NotFoundException(message = ID_MESSAGE)

            val response = testClient.get("/tasks/$id")

            response shouldHaveStatus HttpStatusCode.NotFound
            response.body<ExceptionResponse>() shouldBe getTaskNotFoundErrorResponse()
            coVerify(exactly = 1) { taskService.getTaskById(id = id) }
        }
    }

    @Nested
    inner class PatchTask {
        @Test
        fun `should return 200 and a patched task`() = testApplicationWith(mockModule) {
            val testTask = getSchufaTask()
            coEvery { taskService.patchTask(id = any(), task = any()) } returns testTask

            val response = testClient.patch("/tasks/${testTask.id}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testTask)
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Task>() shouldBe testTask
            coVerify(exactly = 1) { taskService.patchTask(id = testTask.id.toString(), task = testTask) }
        }

        @Test
        fun `should return 400 when id is invalid`() = testApplicationWith(mockModule) {
            val id = "invalidId"
            val testTask = getSchufaTask()
            coEvery { taskService.patchTask(id = any(), task = any()) } throws ObjectIdException()

            val response = testClient.patch("/tasks/$id") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testTask)
            }

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ExceptionResponse>() shouldBe getInvalidIdErrorResponse()
            coVerify(exactly = 1) { taskService.patchTask(id = id, task = testTask) }
        }
    }
}
