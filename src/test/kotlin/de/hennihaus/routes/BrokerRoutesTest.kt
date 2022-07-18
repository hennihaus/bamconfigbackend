package de.hennihaus.routes

import de.hennihaus.models.rest.ErrorResponse
import de.hennihaus.objectmothers.BankObjectMother.getJmsBank
import de.hennihaus.objectmothers.ErrorResponseObjectMother.getInternalServerErrorResponse
import de.hennihaus.objectmothers.GroupObjectMother.getFirstGroup
import de.hennihaus.objectmothers.GroupObjectMother.getSecondGroup
import de.hennihaus.services.BrokerService
import de.hennihaus.services.GroupService
import de.hennihaus.testutils.KtorTestUtils.testApplicationWith
import de.hennihaus.testutils.testClient
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.dsl.module

class BrokerRoutesTest {

    private val brokerService = mockk<BrokerService>()
    private val groupService = mockk<GroupService>()

    private val mockModule = module {
        single { brokerService }
        single { groupService }
    }

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class ResetBroker {
        @Test
        fun `should return 204 and no content when reset was successful`() = testApplicationWith(mockModule) {
            coEvery { groupService.resetAllGroups() } returns listOf(
                getFirstGroup(),
                getSecondGroup(),
            )
            coEvery { brokerService.resetBroker() } returns Unit

            val response = testClient.delete(urlString = "/v1/activemq")

            response shouldHaveStatus HttpStatusCode.NoContent
            response.bodyAsText().shouldBeEmpty()
            coVerifySequence {
                groupService.resetAllGroups()
                brokerService.resetBroker()
            }
        }

        @Test
        fun `should return 500 and an error response when error occurs`() = testApplicationWith(mockModule) {
            coEvery { groupService.resetAllGroups() } returns emptyList()
            coEvery { brokerService.resetBroker() } throws IllegalStateException()

            val response = testClient.delete(urlString = "/v1/activemq")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ErrorResponse>() shouldBe getInternalServerErrorResponse()
            coVerifySequence {
                groupService.resetAllGroups()
                brokerService.resetBroker()
            }
        }
    }

    @Nested
    inner class DeleteQueueByName {
        @Test
        fun `should return 204 and no content when delete was successful`() = testApplicationWith(mockModule) {
            val name = getJmsBank().name
            coEvery { brokerService.deleteQueueByName(name = any()) } returns Unit

            val response = testClient.delete(urlString = "/v1/activemq/$name")

            response shouldHaveStatus HttpStatusCode.NoContent
            response.bodyAsText().shouldBeEmpty()
            coVerify(exactly = 1) { brokerService.deleteQueueByName(name = name) }
        }

        @Test
        fun `should return 500 and an error response when exception occurs`() = testApplicationWith(mockModule) {
            val name = getJmsBank().name
            coEvery { brokerService.deleteQueueByName(name = any()) } throws IllegalStateException()

            val response = testClient.delete(urlString = "/v1/activemq/$name")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ErrorResponse>() shouldBe getInternalServerErrorResponse()
            coVerify(exactly = 1) { brokerService.deleteQueueByName(name = name) }
        }
    }
}
