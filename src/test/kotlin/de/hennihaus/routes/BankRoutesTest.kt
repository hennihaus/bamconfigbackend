package de.hennihaus.routes

import de.hennihaus.models.Bank
import de.hennihaus.objectmothers.BankObjectMother.getJmsBank
import de.hennihaus.objectmothers.BankObjectMother.getSchufaBank
import de.hennihaus.objectmothers.BankObjectMother.getVBank
import de.hennihaus.objectmothers.ExceptionResponseObjectMother.INTERNAL_SERVER_ERROR_MESSAGE
import de.hennihaus.objectmothers.ExceptionResponseObjectMother.getBankNotFoundErrorResponse
import de.hennihaus.objectmothers.ExceptionResponseObjectMother.getInternalServerErrorResponse
import de.hennihaus.plugins.ExceptionResponse
import de.hennihaus.plugins.NotFoundException
import de.hennihaus.services.BankService
import de.hennihaus.services.BankServiceImpl.Companion.ID_MESSAGE
import de.hennihaus.testutils.KtorTestBuilder.testApplicationWith
import de.hennihaus.testutils.testClient
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.dsl.module

class BankRoutesTest {

    private val bankService = mockk<BankService>()

    private val mockModule = module { single { bankService } }

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class GetAllBanks {
        @Test
        fun `should return 200 and a list of banks`() = testApplicationWith(mockModule) {
            coEvery { bankService.getAllBanks() } returns listOf(
                getSchufaBank(),
                getVBank(),
                getJmsBank()
            )

            val response = testClient.get("/banks")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<List<Bank>>().shouldContainExactly(
                getSchufaBank(),
                getVBank(),
                getJmsBank()
            )
            coVerify(exactly = 1) { bankService.getAllBanks() }
        }

        @Test
        fun `should return 200 and an empty list when no banks available`() = testApplicationWith(mockModule) {
            coEvery { bankService.getAllBanks() } returns emptyList()

            val response = client.get("/banks")

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldBe """
                [
                ]
            """.trimIndent()
            coVerify(exactly = 1) { bankService.getAllBanks() }
        }

        @Test
        fun `should return 500 and an exception response on error`() = testApplicationWith(mockModule) {
            coEvery { bankService.getAllBanks() } throws Exception(INTERNAL_SERVER_ERROR_MESSAGE)

            val response = testClient.get("/banks")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ExceptionResponse>() shouldBe getInternalServerErrorResponse()
            coVerify(exactly = 1) { bankService.getAllBanks() }
        }
    }

    @Nested
    inner class GetBankByJmsQueue {
        @Test
        fun `should return 200 and a bank by jmsQueue`() = testApplicationWith(mockModule) {
            val jmsQueue = getSchufaBank().jmsQueue
            coEvery { bankService.getBankByJmsQueue(jmsQueue = any()) } returns getSchufaBank()

            val response = testClient.get("/banks/$jmsQueue")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Bank>() shouldBe getSchufaBank()
            coVerify(exactly = 1) { bankService.getBankByJmsQueue(jmsQueue = jmsQueue) }
        }

        @Test
        fun `should return 404 and not found exception response on error`() = testApplicationWith(mockModule) {
            val jmsQueue = "unknown"
            coEvery { bankService.getBankByJmsQueue(jmsQueue = any()) } throws NotFoundException(message = ID_MESSAGE)

            val response = testClient.get("/banks/$jmsQueue")

            response shouldHaveStatus HttpStatusCode.NotFound
            response.body<ExceptionResponse>() shouldBe getBankNotFoundErrorResponse()
            coVerify(exactly = 1) { bankService.getBankByJmsQueue(jmsQueue = jmsQueue) }
        }
    }

    @Nested
    inner class UpdateAllBanks {
        @Test
        fun `should return 200 and a list of updated banks`() = testApplicationWith(mockModule) {
            val testBanks = listOf(getSchufaBank(), getVBank(), getSchufaBank())
            coEvery { bankService.saveAllBanks(banks = any()) } returns testBanks

            val response = testClient.put("/banks") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testBanks)
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<List<Bank>>().shouldContainExactly(expected = testBanks)
            coVerify(exactly = 1) { bankService.saveAllBanks(banks = testBanks) }
        }

        @Test
        fun `should return 500 with invalid input`() = testApplicationWith(mockModule) {
            val invalidInput = "[{\"invalid\":\"invalid\"}]"

            val response = testClient.put("/banks") {
                contentType(type = ContentType.Application.Json)
                setBody(body = invalidInput)
            }

            response shouldHaveStatus HttpStatusCode.InternalServerError
            coVerify(exactly = 0) { bankService.saveAllBanks(banks = any()) }
        }
    }

    @Nested
    inner class UpdateBank {
        @Test
        fun `should return 200 and an updated bank`() = testApplicationWith(mockModule) {
            val testBank = getSchufaBank()
            coEvery { bankService.saveBank(bank = any()) } returns testBank

            val response = testClient.put("/banks/${testBank.jmsQueue}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testBank)
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Bank>() shouldBe testBank
            coVerify(exactly = 1) { bankService.saveBank(bank = testBank) }
        }

        @Test
        fun `should return 500 with invalid input`() = testApplicationWith(mockModule) {
            val invalidInput = "{\"invalid\":\"invalid\"}"

            val response = testClient.put("/banks/invalid") {
                contentType(type = ContentType.Application.Json)
                setBody(body = invalidInput)
            }

            response shouldHaveStatus HttpStatusCode.InternalServerError
            coVerify(exactly = 0) { bankService.saveBank(bank = any()) }
        }
    }
}
