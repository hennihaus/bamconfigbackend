package de.hennihaus.routes

import de.hennihaus.bamdatamodel.Statistic
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother
import de.hennihaus.bamdatamodel.objectmothers.StatisticObjectMother.getFirstTeamAsyncBankStatistic
import de.hennihaus.models.generated.rest.ErrorsDTO
import de.hennihaus.objectmothers.ErrorsObjectMother.getInvalidIdErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getInvalidLimitErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getInvalidStatisticErrors
import de.hennihaus.objectmothers.ErrorsObjectMother.getStatisticNotFoundErrors
import de.hennihaus.objectmothers.ReasonObjectMother.INVALID_STATISTIC_MESSAGE
import de.hennihaus.plugins.UUIDException
import de.hennihaus.routes.mappers.toStatisticDTO
import de.hennihaus.routes.validations.StatisticValidationService
import de.hennihaus.services.StatisticService
import de.hennihaus.services.StatisticService.Companion.STATISTIC_NOT_FOUND_MESSAGE
import de.hennihaus.testutils.KtorTestUtils.testApplicationWith
import de.hennihaus.testutils.testClient
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.patch
import io.ktor.client.request.post
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
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class StatisticRoutesTest {

    private val statisticService = mockk<StatisticService>()
    private val statisticValidationService = mockk<StatisticValidationService>()

    private val mockModule = module {
        single {
            statisticService
        }
        single {
            statisticValidationService
        }
    }

    @BeforeEach
    fun init() = clearAllMocks()

    @AfterEach
    fun tearDown() = stopKoin()

    @Nested
    inner class SaveStatistics {
        @Test
        fun `should return 204 and no content when saving statistics`() = testApplicationWith(mockModule) {
            coEvery { statisticService.saveStatistics(bankId = any()) } returns Unit
            val bankId = BankObjectMother.ASYNC_BANK_UUID

            val response = testClient.put(urlString = "/v1/statistics/$bankId")

            response shouldHaveStatus HttpStatusCode.NoContent
            response.bodyAsText().shouldBeEmpty()
            coVerify(exactly = 1) { statisticService.saveStatistics(bankId = bankId) }
        }

        @Test
        fun `should return 400 and an error response when uuid is invalid`() = testApplicationWith(mockModule) {
            coEvery { statisticService.saveStatistics(bankId = any()) } throws UUIDException()
            val bankId = "invalidUUID"

            val response = testClient.put(urlString = "/v1/statistics/$bankId")

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidIdErrors()
            coVerify(exactly = 1) { statisticService.saveStatistics(bankId = bankId) }
        }
    }

    @Nested
    inner class DeleteStatistics {
        @Test
        fun `should return 204 and no content when deleting statistics`() = testApplicationWith(mockModule) {
            coEvery { statisticService.deleteStatistics(bankId = any()) } returns Unit
            val bankId = BankObjectMother.ASYNC_BANK_UUID

            val response = testClient.delete(urlString = "/v1/statistics/$bankId")

            response shouldHaveStatus HttpStatusCode.NoContent
            response.bodyAsText().shouldBeEmpty()
            coVerify(exactly = 1) { statisticService.deleteStatistics(bankId = bankId) }
        }

        @Test
        fun `should return 400 and an error response when uuid is invalid`() = testApplicationWith(mockModule) {
            coEvery { statisticService.deleteStatistics(bankId = any()) } throws UUIDException()
            val bankId = "invalidUUID"

            val response = testClient.delete(urlString = "/v1/statistics/$bankId")

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidIdErrors()
            coVerify(exactly = 1) { statisticService.deleteStatistics(bankId = bankId) }
        }
    }

    @Nested
    inner class RecreateStatistics {
        @Test
        fun `should return 204 and no content when recreating statistics`() = testApplicationWith(mockModule) {
            coEvery { statisticService.recreateStatistics(limit = any()) } returns Unit
            val limit = 1L

            val response = testClient.post(urlString = "/v1/statistics/$limit")

            response shouldHaveStatus HttpStatusCode.NoContent
            response.bodyAsText().shouldBeEmpty()
            coVerify(exactly = 1) { statisticService.recreateStatistics(limit = limit) }
        }

        @Test
        fun `should return 400 and an error response when limit is invalid`() = testApplicationWith(mockModule) {
            val limit = "invalidLimit"

            val response = testClient.post(urlString = "/v1/statistics/$limit")

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidLimitErrors()
            coVerify(exactly = 0) { statisticService.recreateStatistics(limit = any()) }
        }
    }

    @Nested
    inner class IncrementStatistics {
        @Test
        fun `should return 200 and an incremented statistic`() = testApplicationWith(mockModule) {
            val testStatistic = getFirstTeamAsyncBankStatistic()
            coEvery { statisticValidationService.validateBody(body = any()) } returns emptyList()
            coEvery { statisticService.incrementRequest(statistic = any()) } returns testStatistic.copy(
                requestsCount = 1L
            )

            val response = testClient.patch(urlString = "/v1/statistics/increment") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testStatistic)
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Statistic>() shouldBe getFirstTeamAsyncBankStatistic(requestsCount = 1L)
            coVerifySequence {
                statisticValidationService.validateBody(
                    body = testStatistic.toStatisticDTO(),
                )
                statisticService.incrementRequest(
                    statistic = testStatistic,
                )
            }
        }

        @Test
        fun `should return 400 and an error response when request body is invalid`() = testApplicationWith(mockModule) {
            val testStatistic = getFirstTeamAsyncBankStatistic().toStatisticDTO().copy(
                bankId = "invalidUUID",
            )
            coEvery { statisticValidationService.validateBody(body = any()) } returns listOf(INVALID_STATISTIC_MESSAGE)

            val response = testClient.patch(urlString = "/v1/statistics/increment") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testStatistic)
            }

            response shouldHaveStatus HttpStatusCode.BadRequest
            response.body<ErrorsDTO>() shouldBe getInvalidStatisticErrors()
            coVerify(exactly = 1) { statisticValidationService.validateBody(body = testStatistic) }
            coVerify(exactly = 0) { statisticService.incrementRequest(statistic = any()) }
        }

        @Test
        fun `should return 404 and not found error response when exception occurs`() = testApplicationWith(mockModule) {
            val testStatistic = getFirstTeamAsyncBankStatistic()
            coEvery { statisticValidationService.validateBody(body = any()) } returns emptyList()
            coEvery { statisticService.incrementRequest(statistic = any()) } throws NotFoundException(
                message = STATISTIC_NOT_FOUND_MESSAGE,
            )

            val response = testClient.patch(urlString = "/v1/statistics/increment") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testStatistic)
            }

            response shouldHaveStatus HttpStatusCode.NotFound
            response.body<ErrorsDTO>() shouldBe getStatisticNotFoundErrors()
            coVerifySequence {
                statisticValidationService.validateBody(
                    body = testStatistic.toStatisticDTO(),
                )
                statisticService.incrementRequest(
                    statistic = testStatistic,
                )
            }
        }
    }
}
