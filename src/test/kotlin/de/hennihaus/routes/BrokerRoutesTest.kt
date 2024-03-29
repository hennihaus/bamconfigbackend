package de.hennihaus.routes

import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getAsyncBank
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getSecondTeam
import de.hennihaus.models.generated.rest.ErrorsDTO
import de.hennihaus.objectmothers.ErrorsObjectMother.getConflictErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getInternalServerErrors
import de.hennihaus.plugins.TransactionException
import de.hennihaus.services.BrokerService
import de.hennihaus.services.TeamService
import de.hennihaus.testutils.KtorTestUtils.testApplicationWith
import de.hennihaus.testutils.testClient
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.mockk.Called
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

class BrokerRoutesTest {

    private val brokerService = mockk<BrokerService>()
    private val teamService = mockk<TeamService>()

    private val mockModule = module {
        single { brokerService }
        single { teamService }
    }

    @BeforeEach
    fun init() = clearAllMocks()

    @AfterEach
    fun tearDown() = stopKoin()

    @Nested
    inner class ResetBroker {
        @Test
        fun `should return 204 and no content when reset was successful`() = testApplicationWith(mockModule) {
            coEvery { teamService.resetAllTeams() } returns listOf(
                getFirstTeam().uuid,
                getSecondTeam().uuid,
            )
            coEvery { brokerService.resetBroker() } returns Unit

            val response = testClient.delete(urlString = "/v1/activemq")

            response shouldHaveStatus HttpStatusCode.NoContent
            response.bodyAsText().shouldBeEmpty()
            coVerifySequence {
                teamService.resetAllTeams()
                brokerService.resetBroker()
            }
        }

        @Test
        fun `should return 409 and an error response when transaction failed`() = testApplicationWith(mockModule) {
            coEvery { teamService.resetAllTeams() } throws TransactionException()

            val response = testClient.delete(urlString = "/v1/activemq")

            response shouldHaveStatus HttpStatusCode.Conflict
            response.body<ErrorsDTO>() shouldBe getConflictErrors()
            coVerify(exactly = 1) { teamService.resetAllTeams() }
            coVerify { brokerService wasNot Called }
        }

        @Test
        fun `should return 500 and an error response when error occurs`() = testApplicationWith(mockModule) {
            coEvery { teamService.resetAllTeams() } returns emptyList()
            coEvery { brokerService.resetBroker() } throws IllegalStateException()

            val response = testClient.delete(urlString = "/v1/activemq")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ErrorsDTO>() shouldBe getInternalServerErrors()
            coVerifySequence {
                teamService.resetAllTeams()
                brokerService.resetBroker()
            }
        }
    }

    @Nested
    inner class DeleteQueueByName {
        @Test
        fun `should return 204 and no content when delete was successful`() = testApplicationWith(mockModule) {
            val name = getAsyncBank().name
            coEvery { brokerService.deleteQueueByName(name = any()) } returns Unit

            val response = testClient.delete(urlString = "/v1/activemq/$name")

            response shouldHaveStatus HttpStatusCode.NoContent
            response.bodyAsText().shouldBeEmpty()
            coVerify(exactly = 1) { brokerService.deleteQueueByName(name = name) }
        }

        @Test
        fun `should return 500 and an error response when exception occurs`() = testApplicationWith(mockModule) {
            val name = getAsyncBank().name
            coEvery { brokerService.deleteQueueByName(name = any()) } throws IllegalStateException()

            val response = testClient.delete(urlString = "/v1/activemq/$name")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ErrorsDTO>() shouldBe getInternalServerErrors()
            coVerify(exactly = 1) { brokerService.deleteQueueByName(name = name) }
        }
    }
}
