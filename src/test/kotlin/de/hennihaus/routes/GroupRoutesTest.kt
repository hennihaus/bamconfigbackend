package de.hennihaus.routes

import de.hennihaus.models.Group
import de.hennihaus.objectmothers.ExceptionResponseObjectMother.INTERNAL_SERVER_ERROR_MESSAGE
import de.hennihaus.objectmothers.ExceptionResponseObjectMother.getGroupNotFoundErrorResponse
import de.hennihaus.objectmothers.ExceptionResponseObjectMother.getInternalServerErrorResponse
import de.hennihaus.objectmothers.ExceptionResponseObjectMother.getInvalidIdErrorResponse
import de.hennihaus.objectmothers.GroupObjectMother.getFirstGroup
import de.hennihaus.objectmothers.GroupObjectMother.getSecondGroup
import de.hennihaus.objectmothers.GroupObjectMother.getThirdGroup
import de.hennihaus.plugins.ExceptionResponse
import de.hennihaus.plugins.NotFoundException
import de.hennihaus.plugins.ObjectIdException
import de.hennihaus.services.GroupService
import de.hennihaus.services.GroupServiceImpl.Companion.ID_MESSAGE
import de.hennihaus.testutils.KtorTestBuilder.testApplicationWith
import de.hennihaus.testutils.testClient
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.dsl.module

class GroupRoutesTest {

    private val groupService = mockk<GroupService>()

    private val mockModule = module { single { groupService } }

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class GetAllGroups {
        @Test
        fun `should return 200 and a list of three groups`() = testApplicationWith(mockModule) {
            coEvery { groupService.getAllGroups() } returns listOf(
                getFirstGroup(),
                getSecondGroup(),
                getThirdGroup()
            )

            val response = testClient.get("/groups")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<List<Group>>().shouldContainExactly(
                getFirstGroup(),
                getSecondGroup(),
                getThirdGroup()
            )
            coVerify(exactly = 1) { groupService.getAllGroups() }
        }

        @Test
        fun `should return 200 and an empty list when no groups`() = testApplicationWith(mockModule) {
            coEvery { groupService.getAllGroups() } returns emptyList()

            val response = client.get("/groups")

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldBe """
                [
                ]
            """.trimIndent()
            coVerify(exactly = 1) { groupService.getAllGroups() }
        }

        @Test
        fun `should return 500 and an exception response on error`() = testApplicationWith(mockModule) {
            coEvery { groupService.getAllGroups() } throws Exception(INTERNAL_SERVER_ERROR_MESSAGE)

            val response = testClient.get("/groups")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ExceptionResponse>() shouldBe getInternalServerErrorResponse()
            coVerify(exactly = 1) { groupService.getAllGroups() }
        }
    }

    @Nested
    inner class GetGroupById {
        @Test
        fun `should return 200 and a group by id`() = testApplicationWith(mockModule) {
            val id = getFirstGroup().id.toString()
            coEvery { groupService.getGroupById(id = any()) } returns getFirstGroup()

            val response = testClient.get("/groups/$id")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Group>() shouldBe getFirstGroup()
            coVerify(exactly = 1) { groupService.getGroupById(id = id) }
        }

        @Test
        fun `should return 404 and not found exception response on error`() = testApplicationWith(mockModule) {
            val id = ObjectId().toString()
            coEvery { groupService.getGroupById(id = any()) } throws NotFoundException(message = ID_MESSAGE)

            val response = testClient.get("/groups/$id")

            response shouldHaveStatus HttpStatusCode.NotFound
            response.body<ExceptionResponse>() shouldBe getGroupNotFoundErrorResponse()
            coVerify(exactly = 1) { groupService.getGroupById(id = id) }
        }
    }

    @Nested
    inner class CheckUsername {
        @Test
        fun `should return 200 and true when username exists`() = testApplicationWith(mockModule) {
            val (id, username) = getFirstGroup()
            coEvery { groupService.checkUsername(id = any(), username = any()) } returns true

            val response = testClient.get("/groups/$id/$username/username")

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldBe true.toString()
            coVerify(exactly = 1) { groupService.checkUsername(id = id.toString(), username = username) }
        }

        @Test
        fun `should return 400 and an exception when id is invalid`() = testApplicationWith(mockModule) {
            val id = "invalidId"
            val username = getFirstGroup().username
            coEvery { groupService.checkUsername(id = any(), username = any()) } throws ObjectIdException()

            val response = testClient.get("/groups/$id/$username/username")

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ExceptionResponse>() shouldBe getInvalidIdErrorResponse()
            coVerify(exactly = 1) { groupService.checkUsername(id = id, username = username) }
        }
    }

    @Nested
    inner class CheckPassword {
        @Test
        fun `should return 200 and true when password exists`() = testApplicationWith(mockModule) {
            val (id, _, password) = getFirstGroup()
            coEvery { groupService.checkPassword(id = any(), password = any()) } returns true

            val response = testClient.get("/groups/$id/$password/password")

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldBe true.toString()
            coVerify(exactly = 1) { groupService.checkPassword(id = id.toString(), password = password) }
        }

        @Test
        fun `should return 400 and an exception when id is invalid`() = testApplicationWith(mockModule) {
            val id = "invalidId"
            val password = getFirstGroup().password
            coEvery { groupService.checkPassword(id = any(), password = any()) } throws ObjectIdException()

            val response = testClient.get("/groups/$id/$password/password")

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ExceptionResponse>() shouldBe getInvalidIdErrorResponse()
            coVerify(exactly = 1) { groupService.checkPassword(id = id, password = password) }
        }
    }

    @Nested
    inner class CheckJmsQueue {
        @Test
        fun `should return 200 and true when jmsQueue exists`() = testApplicationWith(mockModule) {
            val (id, _, _, jmsQueue) = getFirstGroup()
            coEvery { groupService.checkJmsQueue(id = any(), jmsQueue = any()) } returns true

            val response = testClient.get("/groups/$id/$jmsQueue/jmsQueue")

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldBe true.toString()
            coVerify(exactly = 1) { groupService.checkJmsQueue(id = id.toString(), jmsQueue = jmsQueue) }
        }

        @Test
        fun `should return 400 and an exception when id is invalid`() = testApplicationWith(mockModule) {
            val id = "invalidId"
            val jmsQueue = getFirstGroup().jmsQueue
            coEvery { groupService.checkJmsQueue(id = any(), jmsQueue = any()) } throws ObjectIdException()

            val response = testClient.get("/groups/$id/$jmsQueue/jmsQueue")

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ExceptionResponse>() shouldBe getInvalidIdErrorResponse()
            coVerify(exactly = 1) { groupService.checkJmsQueue(id = id, jmsQueue = jmsQueue) }
        }
    }

    @Nested
    inner class UpdateGroup {
        @Test
        fun `should return 200 and a updated group`() = testApplicationWith(mockModule) {
            val testGroup = getFirstGroup()
            coEvery { groupService.saveGroup(group = any()) } returns testGroup

            val response = testClient.put("/groups/${testGroup.id}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testGroup)
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Group>() shouldBe testGroup
            coVerify(exactly = 1) { groupService.saveGroup(group = testGroup) }
        }

        @Test
        fun `should return 500 with invalid input`() = testApplicationWith(mockModule) {
            val invalidInput = "{\"invalid\":\"invalid\"}"

            val response = testClient.put("/groups/invalid") {
                contentType(type = ContentType.Application.Json)
                setBody(body = invalidInput)
            }

            response shouldHaveStatus HttpStatusCode.InternalServerError
            coVerify(exactly = 0) { groupService.saveGroup(group = any()) }
        }
    }

    @Nested
    inner class DeleteGroupById {
        @Test
        fun `should return 204 when successfully deleted a group by id`() = testApplicationWith(mockModule) {
            val id = getFirstGroup().id.toString()
            coEvery { groupService.deleteGroupById(id = any()) } returns Unit

            val response = testClient.delete("/groups/$id")

            response shouldHaveStatus HttpStatusCode.NoContent
            response.bodyAsText() shouldBe ""
            coVerify(exactly = 1) { groupService.deleteGroupById(id = id) }
        }

        @Test
        fun `should return 500 and not found exception response on error`() = testApplicationWith(mockModule) {
            val id = getFirstGroup().id.toString()
            coEvery { groupService.deleteGroupById(id = any()) } throws Exception(INTERNAL_SERVER_ERROR_MESSAGE)

            val response = testClient.delete("/groups/$id")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ExceptionResponse>() shouldBe getInternalServerErrorResponse()
            coVerify(exactly = 1) { groupService.deleteGroupById(id = id) }
        }
    }

    @Nested
    inner class ResetStats {
        @Test
        fun `should return 200 and return group with zero stats`() = testApplicationWith(mockModule) {
            val id = getFirstGroup().id.toString()
            coEvery { groupService.resetStats(id = any()) } returns getFirstGroup()

            val response = testClient.delete("/groups/$id/stats")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Group>() shouldBe getFirstGroup()
            coVerify(exactly = 1) { groupService.resetStats(id = id) }
        }

        @Test
        fun `should return 404 and not found exception response on error`() = testApplicationWith(mockModule) {
            val id = getFirstGroup().id.toString()
            coEvery { groupService.resetStats(id = any()) } throws NotFoundException(message = ID_MESSAGE)

            val response = testClient.delete("/groups/$id/stats")

            response shouldHaveStatus HttpStatusCode.NotFound
            response.body<ExceptionResponse>() shouldBe getGroupNotFoundErrorResponse()
            coVerify(exactly = 1) { groupService.resetStats(id = id) }
        }
    }
}
