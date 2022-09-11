package de.hennihaus.routes

import de.hennihaus.models.generated.ErrorResponse
import de.hennihaus.models.generated.ExistsResponse
import de.hennihaus.models.generated.Team
import de.hennihaus.objectmothers.ErrorResponseObjectMother.getConflictErrorResponse
import de.hennihaus.objectmothers.ErrorResponseObjectMother.getInternalServerErrorResponse
import de.hennihaus.objectmothers.ErrorResponseObjectMother.getInvalidIdErrorResponse
import de.hennihaus.objectmothers.ErrorResponseObjectMother.getTeamNotFoundErrorResponse
import de.hennihaus.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.objectmothers.TeamObjectMother.getSecondTeam
import de.hennihaus.objectmothers.TeamObjectMother.getThirdTeam
import de.hennihaus.plugins.TransactionException
import de.hennihaus.plugins.UUIDException
import de.hennihaus.services.TeamService
import de.hennihaus.services.TeamService.Companion.TEAM_NOT_FOUND_MESSAGE
import de.hennihaus.testutils.KtorTestUtils.testApplicationWith
import de.hennihaus.testutils.testClient
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.should
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
import io.ktor.server.plugins.NotFoundException
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.dsl.module
import java.util.UUID

class TeamRoutesTest {

    private val teamService = mockk<TeamService>()

    private val mockModule = module {
        single {
            teamService
        }
    }

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class GetAllTeams {
        @Test
        fun `should return 200 and a list of three teams`() = testApplicationWith(mockModule) {
            coEvery { teamService.getAllTeams() } returns listOf(
                getFirstTeam(),
                getSecondTeam(),
                getThirdTeam(),
            )

            val response = testClient.get(urlString = "/v1/teams")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<List<Team>>().shouldContainExactly(
                getFirstTeam(),
                getSecondTeam(),
                getThirdTeam(),
            )
            coVerify(exactly = 1) { teamService.getAllTeams() }
        }

        @Test
        fun `should return 200 and an empty list when no teams`() = testApplicationWith(mockModule) {
            coEvery { teamService.getAllTeams() } returns emptyList()

            val response = client.get(urlString = "/v1/teams")

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldBe """
                [
                ]
            """.trimIndent()
            coVerify(exactly = 1) { teamService.getAllTeams() }
        }

        @Test
        fun `should return 500 and an error response when exception is thrown`() = testApplicationWith(mockModule) {
            coEvery { teamService.getAllTeams() } throws IllegalStateException()

            val response = testClient.get(urlString = "/v1/teams")

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
            coVerify(exactly = 1) { teamService.getAllTeams() }
        }
    }

    @Nested
    inner class GetTeamById {
        @Test
        fun `should return 200 and a team by uuid`() = testApplicationWith(mockModule) {
            val uuid = "${getFirstTeam().uuid}"
            coEvery { teamService.getTeamById(id = any()) } returns getFirstTeam()

            val response = testClient.get(urlString = "/v1/teams/$uuid")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Team>() shouldBe getFirstTeam()
            coVerify(exactly = 1) { teamService.getTeamById(id = uuid) }
        }

        @Test
        fun `should return 404 and not found error response when exception occurs`() = testApplicationWith(mockModule) {
            val uuid = "${UUID.randomUUID()}"
            coEvery { teamService.getTeamById(id = any()) } throws NotFoundException(
                message = TEAM_NOT_FOUND_MESSAGE,
            )

            val response = testClient.get(urlString = "/v1/teams/$uuid")

            response shouldHaveStatus HttpStatusCode.NotFound
            response.body<ErrorResponse>() should {
                it.shouldBeEqualToIgnoringFields(
                    other = getTeamNotFoundErrorResponse(),
                    property = ErrorResponse::dateTime,
                )
                it.dateTime.shouldBeEqualToIgnoringFields(
                    other = getTeamNotFoundErrorResponse().dateTime,
                    property = LocalDateTime::second,
                )
            }
            coVerify(exactly = 1) { teamService.getTeamById(id = uuid) }
        }
    }

    @Nested
    inner class CheckUsername {
        @Test
        fun `should return 200 and true when username exists`() = testApplicationWith(mockModule) {
            val (uuid, username) = getFirstTeam()
            coEvery { teamService.checkUsername(id = any(), username = any()) } returns true

            val response = testClient.get(urlString = "/v1/teams/$uuid/check/username/$username")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<ExistsResponse>() shouldBe ExistsResponse(exists = true)
            coVerify(exactly = 1) { teamService.checkUsername(id = "$uuid", username = username) }
        }

        @Test
        fun `should return 400 and an error response when uuid is invalid`() = testApplicationWith(mockModule) {
            val uuid = "invalidUUID"
            val username = getFirstTeam().username
            coEvery { teamService.checkUsername(id = any(), username = any()) } throws UUIDException()

            val response = testClient.get(urlString = "/v1/teams/$uuid/check/username/$username")

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
            coVerify(exactly = 1) { teamService.checkUsername(id = uuid, username = username) }
        }
    }

    @Nested
    inner class CheckPassword {
        @Test
        fun `should return 200 and true when password exists`() = testApplicationWith(mockModule) {
            val (uuid, _, password) = getFirstTeam()
            coEvery { teamService.checkPassword(id = any(), password = any()) } returns true

            val response = testClient.get(urlString = "/v1/teams/$uuid/check/password/$password")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<ExistsResponse>() shouldBe ExistsResponse(exists = true)
            coVerify(exactly = 1) { teamService.checkPassword(id = "$uuid", password = password) }
        }

        @Test
        fun `should return 400 and an error response when uuid is invalid`() = testApplicationWith(mockModule) {
            val uuid = "invalidUUID"
            val password = getFirstTeam().password
            coEvery { teamService.checkPassword(id = any(), password = any()) } throws UUIDException()

            val response = testClient.get(urlString = "/v1/teams/$uuid/check/password/$password")

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
            coVerify(exactly = 1) { teamService.checkPassword(id = uuid, password = password) }
        }
    }

    @Nested
    inner class CheckJmsQueue {
        @Test
        fun `should return 200 and true when jmsQueue exists`() = testApplicationWith(mockModule) {
            val (uuid, _, _, jmsQueue) = getFirstTeam()
            coEvery { teamService.checkJmsQueue(id = any(), jmsQueue = any()) } returns true

            val response = testClient.get(urlString = "/v1/teams/$uuid/check/jmsQueue/$jmsQueue")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<ExistsResponse>() shouldBe ExistsResponse(exists = true)
            coVerify(exactly = 1) { teamService.checkJmsQueue(id = "$uuid", jmsQueue = jmsQueue) }
        }

        @Test
        fun `should return 400 and an error response when uuid is invalid`() = testApplicationWith(mockModule) {
            val uuid = "invalidUUID"
            val jmsQueue = getFirstTeam().jmsQueue
            coEvery { teamService.checkJmsQueue(id = any(), jmsQueue = any()) } throws UUIDException()

            val response = testClient.get(urlString = "/v1/teams/$uuid/check/jmsQueue/$jmsQueue")

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
            coVerify(exactly = 1) { teamService.checkJmsQueue(id = uuid, jmsQueue = jmsQueue) }
        }
    }

    @Nested
    inner class SaveTeam {
        @Test
        fun `should return 200 and a updated team`() = testApplicationWith(mockModule) {
            val testTeam = getFirstTeam()
            coEvery { teamService.saveTeam(team = any()) } returns testTeam

            val response = testClient.put(urlString = "/v1/teams/${testTeam.uuid}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testTeam)
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Team>() shouldBe testTeam
            coVerify(exactly = 1) { teamService.saveTeam(team = testTeam) }
        }

        @Test
        fun `should return 409 and an error response when transaction failed`() = testApplicationWith(mockModule) {
            val testTeam = getFirstTeam()
            coEvery { teamService.saveTeam(team = any()) } throws TransactionException()

            val response = testClient.put(urlString = "/v1/teams/invalid") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testTeam)
            }

            response shouldHaveStatus HttpStatusCode.Conflict
            response.body<ErrorResponse>() should {
                it.shouldBeEqualToIgnoringFields(
                    other = getConflictErrorResponse(),
                    property = ErrorResponse::dateTime,
                )
                it.dateTime.shouldBeEqualToIgnoringFields(
                    other = getConflictErrorResponse().dateTime,
                    property = LocalDateTime::second,
                )
            }
            coVerify(exactly = 1) { teamService.saveTeam(team = testTeam) }
        }

        @Test
        fun `should return 500 with invalid input`() = testApplicationWith(mockModule) {
            val invalidInput = "{\"invalid\":\"invalid\"}"

            val response = testClient.put(urlString = "/v1/teams/invalid") {
                contentType(type = ContentType.Application.Json)
                setBody(body = invalidInput)
            }

            response shouldHaveStatus HttpStatusCode.InternalServerError
            coVerify(exactly = 0) { teamService.saveTeam(team = any()) }
        }
    }

    @Nested
    inner class DeleteTeamById {
        @Test
        fun `should return 204 when successfully deleted a team by uuid`() = testApplicationWith(mockModule) {
            val uuid = "${getFirstTeam().uuid}"
            coEvery { teamService.deleteTeamById(id = any()) } returns mockk()

            val response = testClient.delete(urlString = "/v1/teams/$uuid")

            response shouldHaveStatus HttpStatusCode.NoContent
            response.bodyAsText() shouldBe ""
            coVerify(exactly = 1) { teamService.deleteTeamById(id = uuid) }
        }

        @Test
        fun `should return 500 and not found error response when exception occurs`() = testApplicationWith(mockModule) {
            val uuid = "${getFirstTeam().uuid}"
            coEvery { teamService.deleteTeamById(id = any()) } throws IllegalStateException()

            val response = testClient.delete(urlString = "/v1/teams/$uuid")

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
            coVerify(exactly = 1) { teamService.deleteTeamById(id = uuid) }
        }
    }

    @Nested
    inner class ResetStatistics {
        @Test
        fun `should return 200 and return team with zero statistics`() = testApplicationWith(mockModule) {
            val uuid = "${getFirstTeam().uuid}"
            coEvery { teamService.resetStatistics(id = any()) } returns getFirstTeam()

            val response = testClient.delete(urlString = "/v1/teams/$uuid/statistics")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Team>() shouldBe getFirstTeam()
            coVerify(exactly = 1) { teamService.resetStatistics(id = uuid) }
        }

        @Test
        fun `should return 404 and not found error response when exception occurs`() = testApplicationWith(mockModule) {
            val uuid = "${getFirstTeam().uuid}"
            coEvery { teamService.resetStatistics(id = any()) } throws NotFoundException(
                message = TEAM_NOT_FOUND_MESSAGE,
            )

            val response = testClient.delete(urlString = "/v1/teams/$uuid/statistics")

            response shouldHaveStatus HttpStatusCode.NotFound
            response.body<ErrorResponse>() should {
                it.shouldBeEqualToIgnoringFields(
                    other = getTeamNotFoundErrorResponse(),
                    property = ErrorResponse::dateTime,
                )
                it.dateTime.shouldBeEqualToIgnoringFields(
                    other = getTeamNotFoundErrorResponse().dateTime,
                    property = LocalDateTime::second,
                )
            }
            coVerify(exactly = 1) { teamService.resetStatistics(id = uuid) }
        }

        @Test
        fun `should return 409 and an error response when transaction failed`() = testApplicationWith(mockModule) {
            val uuid = "${getFirstTeam().uuid}"
            coEvery { teamService.resetStatistics(id = any()) } throws TransactionException()

            val response = testClient.delete(urlString = "/v1/teams/$uuid/statistics")

            response shouldHaveStatus HttpStatusCode.Conflict
            response.body<ErrorResponse>() should {
                it.shouldBeEqualToIgnoringFields(
                    other = getConflictErrorResponse(),
                    property = ErrorResponse::dateTime,
                )
                it.dateTime.shouldBeEqualToIgnoringFields(
                    other = getConflictErrorResponse().dateTime,
                    property = LocalDateTime::second,
                )
            }
            coVerify(exactly = 1) { teamService.resetStatistics(id = uuid) }
        }
    }
}
