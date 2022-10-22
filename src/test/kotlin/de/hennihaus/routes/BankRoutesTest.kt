package de.hennihaus.routes

import de.hennihaus.bamdatamodel.Bank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getAsyncBank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getSchufaBank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getSyncBank
import de.hennihaus.models.generated.rest.ErrorsDTO
import de.hennihaus.objectmothers.ErrorsObjectMother.getBankNotFoundErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getConflictErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getInternalServerErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getInvalidBankErrors
import de.hennihaus.objectmothers.ReasonObjectMother.INVALID_BANK_MESSAGE
import de.hennihaus.plugins.TransactionException
import de.hennihaus.routes.mappers.toBankDTO
import de.hennihaus.routes.validations.BankValidationService
import de.hennihaus.services.BankService
import de.hennihaus.services.BankService.Companion.BANK_NOT_FOUND_MESSAGE
import de.hennihaus.testutils.KtorTestUtils.testApplicationWith
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
import io.mockk.coVerifySequence
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.util.UUID

class BankRoutesTest {

    private val bankService = mockk<BankService>()
    private val bankValidationService = mockk<BankValidationService>()

    private val mockModule = module {
        single {
            bankService
        }
        single {
            bankValidationService
        }
    }

    @BeforeEach
    fun init() = clearAllMocks()

    @AfterEach
    fun tearDown() = stopKoin()

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
            response.bodyAsText() shouldBe "[ ]"
            coVerify(exactly = 1) { bankService.getAllBanks() }
        }

        @Test
        fun `should return 500 and an error response when exception occurs`() = testApplicationWith(mockModule) {
            coEvery { bankService.getAllBanks() } throws IllegalStateException()

            val response = testClient.get(urlString = "/v1/banks")

            response shouldHaveStatus HttpStatusCode.InternalServerError
            response.body<ErrorsDTO>() shouldBe getInternalServerErrors()
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
            response.body<ErrorsDTO>() shouldBe getBankNotFoundErrors()
            coVerify(exactly = 1) { bankService.getBankById(id = uuid) }
        }
    }

    @Nested
    inner class PatchBank {
        @Test
        fun `should return 200 and a patched bank`() = testApplicationWith(mockModule) {
            val testBank = getSchufaBank()
            coEvery { bankValidationService.validateBody(body = any()) } returns emptyList()
            coEvery { bankService.patchBank(id = any(), bank = any()) } returns testBank

            val response = testClient.patch(urlString = "/v1/banks/${testBank.uuid}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testBank)
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Bank>() shouldBe testBank
            coVerifySequence {
                bankValidationService.validateBody(body = testBank.toBankDTO())
                bankService.patchBank(id = "${testBank.uuid}", bank = testBank)
            }
        }

        @Test
        fun `should return 400 and an error response when request body is invalid`() = testApplicationWith(mockModule) {
            val testBank = getSchufaBank().toBankDTO().copy(
                uuid = "invalidUUID",
            )
            coEvery { bankValidationService.validateBody(body = any()) } returns listOf(INVALID_BANK_MESSAGE)

            val response = testClient.patch(urlString = "/v1/banks/${testBank.uuid}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testBank)
            }

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidBankErrors()
            coVerify(exactly = 1) {
                bankValidationService.validateBody(body = testBank)
            }
            coVerify(exactly = 0) {
                bankService.patchBank(id = any(), bank = any())
            }
        }

        @Test
        fun `should return 409 and an error response when transaction failed`() = testApplicationWith(mockModule) {
            val testBank = getSchufaBank()
            coEvery { bankValidationService.validateBody(body = any()) } returns emptyList()
            coEvery { bankService.patchBank(id = any(), bank = any()) } throws TransactionException()

            val response = testClient.patch(urlString = "/v1/banks/${testBank.uuid}") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testBank)
            }

            response shouldHaveStatus HttpStatusCode.Conflict
            response.body<ErrorsDTO>() shouldBe getConflictErrors()
            coVerifySequence {
                bankValidationService.validateBody(body = testBank.toBankDTO())
                bankService.patchBank(id = any(), bank = testBank)
            }
        }
    }
}
