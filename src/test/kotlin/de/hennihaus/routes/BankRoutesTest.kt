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
import de.hennihaus.testutils.KtorTestBuilder.testApplication
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
        fun `should return 200 and a list of banks`() = testApplication(mockModule = mockModule) {
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
        fun `should return 200 and an empty list when no banks available`() = testApplication(mockModule = mockModule) {
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
        fun `should return 500 and an exception response on error`() = testApplication(mockModule = mockModule) {
            coEvery { bankService.getAllBanks() } throws Exception(INTERNAL_SERVER_ERROR_MESSAGE)

            val response = testClient.get("/banks")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ExceptionResponse>() shouldBe getInternalServerErrorResponse()
            coVerify(exactly = 1) { bankService.getAllBanks() }
        }
    }

    @Nested
    inner class GetBankByJmsTopic {
        @Test
        fun `should return 200 and a bank by jmsTopic`() = testApplication(mockModule = mockModule) {
            val jmsTopic = getSchufaBank().jmsTopic
            coEvery { bankService.getBankByJmsTopic(jmsTopic = any()) } returns getSchufaBank()

            val response = testClient.get("/banks/$jmsTopic")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Bank>() shouldBe getSchufaBank()
            coVerify(exactly = 1) { bankService.getBankByJmsTopic(jmsTopic = jmsTopic) }
        }

        @Test
        fun `should return 404 and not found exception response on error`() = testApplication(mockModule = mockModule) {
            val jmsTopic = "unknown"
            coEvery { bankService.getBankByJmsTopic(jmsTopic = any()) } throws NotFoundException(message = ID_MESSAGE)

            val response = testClient.get("/banks/$jmsTopic")

            response shouldHaveStatus HttpStatusCode.NotFound
            response.body<ExceptionResponse>() shouldBe getBankNotFoundErrorResponse()
            coVerify(exactly = 1) { bankService.getBankByJmsTopic(jmsTopic = jmsTopic) }
        }
    }

    @Nested
    inner class UpdateAllBanks {
        @Test
        fun `should return 200 and a list of updated banks`() = testApplication(mockModule = mockModule) {
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
        fun `should return 500 with invalid input`() = testApplication(mockModule = mockModule) {
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
        fun `should return 200 and an updated bank`() = testApplication(mockModule = mockModule) {
            val testBank = getSchufaBank()
            coEvery { bankService.saveBank(bank = any()) } returns testBank

            val response = testClient.put("/banks/${testBank.jmsTopic}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testBank)
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Bank>() shouldBe testBank
            coVerify(exactly = 1) { bankService.saveBank(bank = testBank) }
        }

        @Test
        fun `should return 500 with invalid input`() = testApplication(mockModule = mockModule) {
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
