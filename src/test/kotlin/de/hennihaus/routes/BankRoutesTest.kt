package de.hennihaus.routes

import de.hennihaus.models.generated.Bank
import de.hennihaus.models.generated.ErrorResponse
import de.hennihaus.objectmothers.BankObjectMother.getAsyncBank
import de.hennihaus.objectmothers.BankObjectMother.getSchufaBank
import de.hennihaus.objectmothers.BankObjectMother.getSyncBank
import de.hennihaus.objectmothers.ErrorResponseObjectMother.getBankNotFoundErrorResponse
import de.hennihaus.objectmothers.ErrorResponseObjectMother.getConflictErrorResponse
import de.hennihaus.objectmothers.ErrorResponseObjectMother.getInternalServerErrorResponse
import de.hennihaus.plugins.TransactionException
import de.hennihaus.services.BankService
import de.hennihaus.services.BankService.Companion.BANK_NOT_FOUND_MESSAGE
import de.hennihaus.testutils.KtorTestUtils.testApplicationWith
import de.hennihaus.testutils.testClient
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.dsl.module
import java.util.UUID

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
                getSyncBank(),
                getAsyncBank(),
            )

            val response = testClient.get(urlString = "/v1/banks")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<List<Bank>>().shouldContainExactly(
                getSchufaBank(),
                getSyncBank(),
                getAsyncBank(),
            )
            coVerify(exactly = 1) { bankService.getAllBanks() }
        }

        @Test
        fun `should return 200 and an empty list when no banks available`() = testApplicationWith(mockModule) {
            coEvery { bankService.getAllBanks() } returns emptyList()

            val response = testClient.get(urlString = "/v1/banks")

            response shouldHaveStatus HttpStatusCode.OK
            response.bodyAsText() shouldBe """
                [
                ]
            """.trimIndent()
            coVerify(exactly = 1) { bankService.getAllBanks() }
        }

        @Test
        fun `should return 500 and an error response when exception occurs`() = testApplicationWith(mockModule) {
            coEvery { bankService.getAllBanks() } throws IllegalStateException()

            val response = testClient.get(urlString = "/v1/banks")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ErrorResponse>().shouldBeEqualToIgnoringFields(
                other = getInternalServerErrorResponse(),
                property = ErrorResponse::dateTime,
            )
            coVerify(exactly = 1) { bankService.getAllBanks() }
        }
    }

    @Nested
    inner class GetBankById {
        @Test
        fun `should return 200 and a bank by uuid`() = testApplicationWith(mockModule) {
            val uuid = "${getSchufaBank().uuid}"
            coEvery { bankService.getBankById(id = any()) } returns getSchufaBank()

            val response = testClient.get(urlString = "/v1/banks/$uuid")

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Bank>() shouldBe getSchufaBank()
            coVerify(exactly = 1) { bankService.getBankById(id = uuid) }
        }

        @Test
        fun `should return 404 and not found error response when exception occurs`() = testApplicationWith(mockModule) {
            val uuid = "${UUID.randomUUID()}"
            coEvery { bankService.getBankById(id = any()) } throws NotFoundException(
                message = BANK_NOT_FOUND_MESSAGE,
            )

            val response = testClient.get(urlString = "/v1/banks/$uuid")

            response shouldHaveStatus HttpStatusCode.NotFound
            response.body<ErrorResponse>().shouldBeEqualToIgnoringFields(
                other = getBankNotFoundErrorResponse(),
                property = ErrorResponse::dateTime,
            )
            coVerify(exactly = 1) { bankService.getBankById(id = uuid) }
        }
    }

    @Nested
    inner class SaveBank {
        @Test
        fun `should return 200 and an updated bank`() = testApplicationWith(mockModule) {
            val testBank = getSchufaBank()
            coEvery { bankService.saveBank(bank = any()) } returns testBank

            val response = testClient.put(urlString = "/v1/banks/${testBank.name}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testBank)
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Bank>() shouldBe testBank
            coVerify(exactly = 1) { bankService.saveBank(bank = testBank) }
        }

        @Test
        fun `should return 409 and an error response when transaction failed`() = testApplicationWith(mockModule) {
            val testBank = getSchufaBank()
            coEvery { bankService.saveBank(bank = any()) } throws TransactionException()

            val response = testClient.put(urlString = "/v1/banks/${testBank.name}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testBank)
            }

            response shouldHaveStatus HttpStatusCode.Conflict
            response.body<ErrorResponse>().shouldBeEqualToIgnoringFields(
                other = getConflictErrorResponse(),
                property = ErrorResponse::dateTime,
            )
            coVerify(exactly = 1) { bankService.saveBank(bank = testBank) }
        }

        @Test
        fun `should return 500 with invalid input`() = testApplicationWith(mockModule) {
            val invalidInput = "{\"invalid\":\"invalid\"}"

            val response = testClient.put(urlString = "/v1/banks/invalid") {
                contentType(type = ContentType.Application.Json)
                setBody(body = invalidInput)
            }

            response shouldHaveStatus HttpStatusCode.InternalServerError
            coVerify(exactly = 0) { bankService.saveBank(bank = any()) }
        }
    }
}
