package de.hennihaus.routes

import de.hennihaus.bamdatamodel.Team
import de.hennihaus.bamdatamodel.objectmothers.PaginationObjectMother.PREVIOUS_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.configurations.RoutesConfiguration.CURSOR_QUERY_PARAMETER
import de.hennihaus.configurations.RoutesConfiguration.DEFAULT_LIMIT_PARAMETER
import de.hennihaus.configurations.RoutesConfiguration.LIMIT_QUERY_PARAMETER
import de.hennihaus.models.cursors.TeamQuery
import de.hennihaus.models.generated.rest.ErrorsDTO
import de.hennihaus.models.generated.rest.UniqueDTO
import de.hennihaus.objectmothers.CursorObjectMother.getFirstTeamCursorWithEmptyFields
import de.hennihaus.objectmothers.CursorObjectMother.getFirstTeamCursorWithNoEmptyFields
import de.hennihaus.objectmothers.CursorObjectMother.getPreviousTeamCursorWithNoEmptyFields
import de.hennihaus.objectmothers.ErrorsObjectMother.getConflictErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getInternalServerErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getInvalidCursorErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getInvalidIdErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getInvalidQueryErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getInvalidTeamErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getTeamNotFoundErrors
import de.hennihaus.objectmothers.PaginationObjectMother.getPaginationDTOWithEmptyFields
import de.hennihaus.objectmothers.PaginationObjectMother.getTeamPaginationDTOWithEmptyFields
import de.hennihaus.objectmothers.PaginationObjectMother.getTeamPaginationDTOWithNoEmptyFields
import de.hennihaus.objectmothers.PaginationObjectMother.getTeamPaginationWithEmptyFields
import de.hennihaus.objectmothers.PaginationObjectMother.getTeamPaginationWithNoEmptyFields
import de.hennihaus.objectmothers.ReasonObjectMother.INVALID_CURSOR_MESSAGE
import de.hennihaus.objectmothers.ReasonObjectMother.INVALID_QUERY_MESSAGE
import de.hennihaus.objectmothers.ReasonObjectMother.INVALID_TEAM_MESSAGE
import de.hennihaus.objectmothers.TeamQueryObjectMother.getTeamQueryWithEmptyFields
import de.hennihaus.objectmothers.TeamQueryObjectMother.getTeamQueryWithNoEmptyFields
import de.hennihaus.plugins.TransactionException
import de.hennihaus.plugins.UUIDException
import de.hennihaus.routes.TeamRoutes.BANKS_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.HAS_PASSED_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.JMS_QUEUE_PARAMETER
import de.hennihaus.routes.TeamRoutes.MAX_REQUESTS_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.MIN_REQUESTS_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.PASSWORD_PARAMETER
import de.hennihaus.routes.TeamRoutes.STUDENT_FIRSTNAME_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.STUDENT_LASTNAME_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.TYPE_QUERY_PARAMETER
import de.hennihaus.routes.TeamRoutes.USERNAME_PARAMETER
import de.hennihaus.routes.mappers.toTeamDTO
import de.hennihaus.routes.mappers.toTeamQueryDTO
import de.hennihaus.routes.validations.TeamValidationService
import de.hennihaus.services.TeamService
import de.hennihaus.services.TeamService.Companion.TEAM_NOT_FOUND_MESSAGE
import de.hennihaus.testutils.KtorTestUtils.testApplicationWith
import de.hennihaus.testutils.testClient
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.nulls.shouldNotBeNull
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
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.util.UUID
import de.hennihaus.models.generated.rest.TeamsDTO as TeamPaginationDTO

class TeamRoutesTest {

    private val teamService = mockk<TeamService>()
    private val teamValidationService = mockk<TeamValidationService>()

    private val mockModule = module {
        single {
            teamService
        }
        single {
            teamValidationService
        }
    }

    @BeforeEach
    fun init() {
        clearAllMocks()

        coEvery { teamValidationService.validateUrl(query = any()) } returns emptyList()
    }

    @AfterEach
    fun tearDown() = stopKoin()

    @Nested
    inner class GetAllTeams {
        @BeforeEach
        fun init() {
            every { teamValidationService.validateCursor<TeamQuery>(cursor = any()) } returns emptyList()
            coEvery { teamService.getAllTeams(cursor = any()) } returns getTeamPaginationWithNoEmptyFields()
        }

        @Test
        fun `should return 200 and team pagination with all fields set`() = testApplicationWith(mockModule) {
            coEvery { teamService.getAllTeams(cursor = any()) } returns getTeamPaginationWithNoEmptyFields()

            val response = testClient.get(urlString = "/v1/teams")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<TeamPaginationDTO>() shouldBe getTeamPaginationDTOWithNoEmptyFields()
            coVerifySequence {
                teamValidationService.validateUrl(
                    query = getTeamQueryWithEmptyFields(limit = DEFAULT_LIMIT_PARAMETER).toTeamQueryDTO(),
                )
                teamService.getAllTeams(
                    cursor = getFirstTeamCursorWithNoEmptyFields(
                        query = getTeamQueryWithEmptyFields(limit = DEFAULT_LIMIT_PARAMETER),
                    ),
                )
            }
            verify(exactly = 0) { teamValidationService.validateCursor<TeamQuery>(cursor = any()) }
        }

        @Test
        fun `should return 200 and team pagination with min fields and no teams`() = testApplicationWith(mockModule) {
            coEvery { teamService.getAllTeams(cursor = any()) } returns getTeamPaginationWithEmptyFields(
                prev = null,
                next = null,
                items = emptyList(),
            )

            val response = testClient.get(urlString = "/v1/teams")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<TeamPaginationDTO>() shouldBe getTeamPaginationDTOWithEmptyFields(
                pagination = getPaginationDTOWithEmptyFields(
                    prev = null,
                    next = null,
                ),
                items = emptyList(),
            )
            coVerifySequence {
                teamValidationService.validateUrl(
                    query = getTeamQueryWithEmptyFields(limit = DEFAULT_LIMIT_PARAMETER).toTeamQueryDTO(),
                )
                teamService.getAllTeams(
                    cursor = getFirstTeamCursorWithEmptyFields(
                        query = getTeamQueryWithEmptyFields(limit = DEFAULT_LIMIT_PARAMETER),
                    ),
                )
            }
            verify(exactly = 0) { teamValidationService.validateCursor<TeamQuery>(cursor = any()) }
        }

        @Test
        fun `should return 200 and use cursor instead of query parameters`() = testApplicationWith(mockModule) {
            val cursor = PREVIOUS_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS
            val query = getTeamQueryWithEmptyFields()

            val response = testClient.get(urlString = "/v1/teams") {
                url {
                    parameters.append(name = CURSOR_QUERY_PARAMETER, value = cursor)
                    parameters.append(name = LIMIT_QUERY_PARAMETER, value = "${query.limit}")
                }
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<TeamPaginationDTO>().shouldNotBeNull()
            coVerifySequence {
                teamValidationService.validateUrl(query = getTeamQueryWithEmptyFields().toTeamQueryDTO())
                teamValidationService.validateCursor<TeamQuery>(cursor = cursor)
                teamService.getAllTeams(cursor = getPreviousTeamCursorWithNoEmptyFields())
            }
        }

        @Test
        fun `should return 200 and use query parameter when no cursor available`() = testApplicationWith(mockModule) {
            val query = getTeamQueryWithNoEmptyFields()

            val response = testClient.get(urlString = "/v1/teams") {
                url {
                    parameters.append(name = LIMIT_QUERY_PARAMETER, value = "${query.limit}")
                    parameters.append(name = TYPE_QUERY_PARAMETER, value = "${query.type}")
                    parameters.append(name = USERNAME_PARAMETER, value = "${query.username}")
                    parameters.append(name = PASSWORD_PARAMETER, value = "${query.password}")
                    parameters.append(name = JMS_QUEUE_PARAMETER, value = "${query.jmsQueue}")
                    parameters.append(name = HAS_PASSED_QUERY_PARAMETER, value = "${query.hasPassed}")
                    parameters.append(name = MIN_REQUESTS_QUERY_PARAMETER, value = "${query.minRequests}")
                    parameters.append(name = MAX_REQUESTS_QUERY_PARAMETER, value = "${query.maxRequests}")
                    parameters.append(name = STUDENT_FIRSTNAME_QUERY_PARAMETER, value = "${query.studentFirstname}")
                    parameters.append(name = STUDENT_LASTNAME_QUERY_PARAMETER, value = "${query.studentLastname}")
                    parameters.appendAll(name = BANKS_QUERY_PARAMETER, values = query.banks!!)
                }
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<TeamPaginationDTO>().shouldNotBeNull()
            coVerifySequence {
                teamValidationService.validateUrl(query = getTeamQueryWithNoEmptyFields().toTeamQueryDTO())
                teamService.getAllTeams(cursor = getFirstTeamCursorWithNoEmptyFields())
            }
            verify(exactly = 0) { teamValidationService.validateCursor<TeamQuery>(cursor = any()) }
        }

        @Test
        fun `should return 400 and error response with invalid query parameter`() = testApplicationWith(mockModule) {
            coEvery { teamValidationService.validateUrl(query = any()) } returns listOf(
                INVALID_QUERY_MESSAGE,
            )
            val limit = -1

            val response = testClient.get(urlString = "/v1/teams") {
                url {
                    parameters.append(name = LIMIT_QUERY_PARAMETER, value = "$limit")
                }
            }

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidQueryErrors()
            coVerify(exactly = 1) {
                teamValidationService.validateUrl(
                    query = getTeamQueryWithEmptyFields(limit = limit).toTeamQueryDTO(),
                )
            }
            coVerify(exactly = 0) { teamService.getAllTeams(cursor = any()) }
        }

        @Test
        fun `should return 400 and error response with invalid cursor parameter`() = testApplicationWith(mockModule) {
            every { teamValidationService.validateCursor<TeamQuery>(cursor = any()) } returns listOf(
                INVALID_CURSOR_MESSAGE,
            )
            val cursor = "invalidCursor"

            val response = testClient.get(urlString = "v1/teams") {
                url {
                    parameters.append(name = CURSOR_QUERY_PARAMETER, value = cursor)
                }
            }

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidCursorErrors()
            coVerify(exactly = 1) { teamValidationService.validateCursor<TeamQuery>(cursor = cursor) }
            coVerify(exactly = 0) { teamService.getAllTeams(cursor = any()) }
        }

        @Test
        fun `should return 500 and an error response when exception is thrown`() = testApplicationWith(mockModule) {
            coEvery { teamService.getAllTeams(cursor = any()) } throws IllegalStateException()

            val response = testClient.get(urlString = "/v1/teams")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ErrorsDTO>() shouldBe getInternalServerErrors()
            coVerify(exactly = 1) { teamService.getAllTeams(cursor = any()) }
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
            response.body<ErrorsDTO>() shouldBe getTeamNotFoundErrors()
            coVerify(exactly = 1) { teamService.getTeamById(id = uuid) }
        }
    }

    @Nested
    inner class IsUsernameUnique {
        @Test
        fun `should return 200 and true when username is unique`() = testApplicationWith(mockModule) {
            val (uuid, _, username) = getFirstTeam()
            coEvery { teamService.isUsernameUnique(id = any(), username = any()) } returns true

            val response = testClient.get(urlString = "/v1/teams/$uuid/unique/username/$username")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<UniqueDTO>() shouldBe UniqueDTO(isUnique = true)
            coVerify(exactly = 1) { teamService.isUsernameUnique(id = "$uuid", username = username) }
        }

        @Test
        fun `should return 400 and an error response when uuid is invalid`() = testApplicationWith(mockModule) {
            val uuid = "invalidUUID"
            val username = getFirstTeam().username
            coEvery { teamService.isUsernameUnique(id = any(), username = any()) } throws UUIDException()

            val response = testClient.get(urlString = "/v1/teams/$uuid/unique/username/$username")

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidIdErrors()
            coVerify(exactly = 1) { teamService.isUsernameUnique(id = uuid, username = username) }
        }
    }

    @Nested
    inner class IsPasswordUnique {
        @Test
        fun `should return 200 and true when password is unique`() = testApplicationWith(mockModule) {
            val (uuid, _, _, password) = getFirstTeam()
            coEvery { teamService.isPasswordUnique(id = any(), password = any()) } returns true

            val response = testClient.get(urlString = "/v1/teams/$uuid/unique/password/$password")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<UniqueDTO>() shouldBe UniqueDTO(isUnique = true)
            coVerify(exactly = 1) { teamService.isPasswordUnique(id = "$uuid", password = password) }
        }

        @Test
        fun `should return 400 and an error response when uuid is invalid`() = testApplicationWith(mockModule) {
            val uuid = "invalidUUID"
            val password = getFirstTeam().password
            coEvery { teamService.isPasswordUnique(id = any(), password = any()) } throws UUIDException()

            val response = testClient.get(urlString = "/v1/teams/$uuid/unique/password/$password")

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidIdErrors()
            coVerify(exactly = 1) { teamService.isPasswordUnique(id = uuid, password = password) }
        }
    }

    @Nested
    inner class IsJmsQueueUnique {
        @Test
        fun `should return 200 and true when jmsQueue is unique`() = testApplicationWith(mockModule) {
            val (uuid, _, _, _, jmsQueue) = getFirstTeam()
            coEvery { teamService.isJmsQueueUnique(id = any(), jmsQueue = any()) } returns true

            val response = testClient.get(urlString = "/v1/teams/$uuid/unique/jmsQueue/$jmsQueue")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<UniqueDTO>() shouldBe UniqueDTO(isUnique = true)
            coVerify(exactly = 1) { teamService.isJmsQueueUnique(id = "$uuid", jmsQueue = jmsQueue) }
        }

        @Test
        fun `should return 400 and an error response when uuid is invalid`() = testApplicationWith(mockModule) {
            val uuid = "invalidUUID"
            val jmsQueue = getFirstTeam().jmsQueue
            coEvery { teamService.isJmsQueueUnique(id = any(), jmsQueue = any()) } throws UUIDException()

            val response = testClient.get(urlString = "/v1/teams/$uuid/unique/jmsQueue/$jmsQueue")

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidIdErrors()
            coVerify(exactly = 1) { teamService.isJmsQueueUnique(id = uuid, jmsQueue = jmsQueue) }
        }
    }

    @Nested
    inner class SaveTeam {
        @Test
        fun `should return 200 and a updated team`() = testApplicationWith(mockModule) {
            val testTeam = getFirstTeam()
            coEvery { teamValidationService.validateBody(body = any()) } returns emptyList()
            coEvery { teamService.saveTeam(team = any()) } returns testTeam

            val response = testClient.put(urlString = "/v1/teams/${testTeam.uuid}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testTeam)
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Team>() shouldBe testTeam
            coVerifySequence {
                teamValidationService.validateUrl(query = any())
                teamValidationService.validateBody(body = testTeam.toTeamDTO())
                teamService.saveTeam(team = testTeam)
            }
        }

        @Test
        fun `should return 400 and an error response when request body is invalid`() = testApplicationWith(mockModule) {
            val testTeam = getFirstTeam().toTeamDTO().copy(
                uuid = "invalidUUID",
            )
            coEvery { teamValidationService.validateBody(body = any()) } returns listOf(INVALID_TEAM_MESSAGE)

            val response = testClient.put(urlString = "/v1/teams/${testTeam.uuid}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testTeam)
            }

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidTeamErrors()
            coVerify(exactly = 1) {
                teamValidationService.validateBody(body = testTeam)
            }
            coVerify(exactly = 0) {
                teamService.saveTeam(team = any())
            }
        }

        @Test
        fun `should return 409 and an error response when transaction failed`() = testApplicationWith(mockModule) {
            val testTeam = getFirstTeam()
            coEvery { teamValidationService.validateBody(body = any()) } returns emptyList()
            coEvery { teamService.saveTeam(team = any()) } throws TransactionException()

            val response = testClient.put(urlString = "/v1/teams/${testTeam.uuid}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testTeam)
            }

            response shouldHaveStatus HttpStatusCode.Conflict
            response.body<ErrorsDTO>() shouldBe getConflictErrors()
            coVerifySequence {
                teamValidationService.validateUrl(query = any())
                teamValidationService.validateBody(body = testTeam.toTeamDTO())
                teamService.saveTeam(team = testTeam)
            }
        }
    }

    @Nested
    inner class DeleteTeamById {
        @Test
        fun `should return 204 when successfully deleted a team by uuid`() = testApplicationWith(mockModule) {
            val uuid = "${getFirstTeam().uuid}"
            coEvery { teamService.deleteTeamById(id = any()) } returns true

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
            response.body<ErrorsDTO>() shouldBe getInternalServerErrors()
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
            response.body<ErrorsDTO>() shouldBe getTeamNotFoundErrors()
            coVerify(exactly = 1) { teamService.resetStatistics(id = uuid) }
        }

        @Test
        fun `should return 409 and an error response when transaction failed`() = testApplicationWith(mockModule) {
            val uuid = "${getFirstTeam().uuid}"
            coEvery { teamService.resetStatistics(id = any()) } throws TransactionException()

            val response = testClient.delete(urlString = "/v1/teams/$uuid/statistics")

            response shouldHaveStatus HttpStatusCode.Conflict
            response.body<ErrorsDTO>() shouldBe getConflictErrors()
            coVerify(exactly = 1) { teamService.resetStatistics(id = uuid) }
        }
    }
}
