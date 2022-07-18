package de.hennihaus.routes

import de.hennihaus.models.Task
import de.hennihaus.models.rest.ErrorResponse
import de.hennihaus.objectmothers.ErrorResponseObjectMother.getInternalServerErrorResponse
import de.hennihaus.objectmothers.ErrorResponseObjectMother.getInvalidIdErrorResponse
import de.hennihaus.objectmothers.ErrorResponseObjectMother.getTaskNotFoundErrorResponse
import de.hennihaus.objectmothers.TaskObjectMother.getAsynchronousBankTask
import de.hennihaus.objectmothers.TaskObjectMother.getSchufaTask
import de.hennihaus.objectmothers.TaskObjectMother.getSynchronousBankTask
import de.hennihaus.plugins.ObjectIdException
import de.hennihaus.services.TaskService
import de.hennihaus.services.TaskServiceImpl.Companion.TASK_NOT_FOUND_MESSAGE
import de.hennihaus.testutils.KtorTestUtils.testApplicationWith
import de.hennihaus.testutils.testClient
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.should
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
import kotlinx.datetime.LocalDateTime
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
            response.bodyAsText() shouldBe """
                [
                ]
            """.trimIndent()
            coVerify(exactly = 1) { taskService.getAllTasks() }
        }

        @Test
        fun `should return 500 and an error when exception occurs`() = testApplicationWith(mockModule) {
            coEvery { taskService.getAllTasks() } throws IllegalStateException()

            val response = testClient.get(urlString = "/v1/tasks")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ErrorResponse>() should {
                it.shouldBeEqualToIgnoringFields(
                    other = getInternalServerErrorResponse(),
                    property = ErrorResponse::dateTime,
                )
                it.dateTime.shouldBeEqualToIgnoringFields(
                    other = getInternalServerErrorResponse().dateTime,
                    property = LocalDateTime::second,
                )
            }
            coVerify(exactly = 1) { taskService.getAllTasks() }
        }
    }

    @Nested
    inner class GetTaskById {
        @Test
        fun `should return 200 and a task by id`() = testApplicationWith(mockModule) {
            val id = getSchufaTask().id.toString()
            coEvery { taskService.getTaskById(id = any()) } returns getSchufaTask()

            val response = testClient.get(urlString = "/v1/tasks/$id")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Task>() shouldBe getSchufaTask()
            coVerify(exactly = 1) { taskService.getTaskById(id = id) }
        }

        @Test
        fun `should return 404 and not found error response when exception occurs`() = testApplicationWith(mockModule) {
            val id = ObjectId().toString()
            coEvery { taskService.getTaskById(id = any()) } throws NotFoundException(message = TASK_NOT_FOUND_MESSAGE)

            val response = testClient.get(urlString = "/v1/tasks/$id")

            response shouldHaveStatus HttpStatusCode.NotFound
            response.body<ErrorResponse>() should {
                it.shouldBeEqualToIgnoringFields(
                    other = getTaskNotFoundErrorResponse(),
                    property = ErrorResponse::dateTime,
                )
                it.dateTime.shouldBeEqualToIgnoringFields(
                    other = getTaskNotFoundErrorResponse().dateTime,
                    property = LocalDateTime::second,
                )
            }
            coVerify(exactly = 1) { taskService.getTaskById(id = id) }
        }
    }

    @Nested
    inner class PatchTask {
        @Test
        fun `should return 200 and a patched task`() = testApplicationWith(mockModule) {
            val testTask = getSchufaTask()
            coEvery { taskService.patchTask(id = any(), task = any()) } returns testTask

            val response = testClient.patch(urlString = "/v1/tasks/${testTask.id}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testTask)
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Task>() shouldBe testTask
            coVerify(exactly = 1) { taskService.patchTask(id = testTask.id.toString(), task = testTask) }
        }

        @Test
        fun `should return 400 and error response when id is invalid`() = testApplicationWith(mockModule) {
            val id = "invalidId"
            val testTask = getSchufaTask()
            coEvery { taskService.patchTask(id = any(), task = any()) } throws ObjectIdException()

            val response = testClient.patch(urlString = "/v1/tasks/$id") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testTask)
            }

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorResponse>() should {
                it.shouldBeEqualToIgnoringFields(
                    other = getInvalidIdErrorResponse(),
                    property = ErrorResponse::dateTime,
                )
                it.dateTime.shouldBeEqualToIgnoringFields(
                    other = getInvalidIdErrorResponse().dateTime,
                    property = LocalDateTime::second,
                )
            }
            coVerify(exactly = 1) { taskService.patchTask(id = id, task = testTask) }
        }
    }
}
