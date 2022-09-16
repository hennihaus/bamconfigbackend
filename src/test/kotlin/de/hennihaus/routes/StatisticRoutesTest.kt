package de.hennihaus.routes

import de.hennihaus.models.generated.ErrorResponse
import de.hennihaus.models.generated.Statistic
import de.hennihaus.objectmothers.ErrorResponseObjectMother.getStatisticNotFoundErrorResponse
import de.hennihaus.objectmothers.StatisticObjectMother.getFirstTeamAsyncBankStatistic
import de.hennihaus.services.StatisticService
import de.hennihaus.services.StatisticService.Companion.STATISTIC_NOT_FOUND_MESSAGE
import de.hennihaus.testutils.KtorTestUtils.testApplicationWith
import de.hennihaus.testutils.testClient
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
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

class StatisticRoutesTest {

    private val statisticService = mockk<StatisticService>()

    private val mockModule = module {
        single {
            statisticService
        }
    }

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class IncrementStatistics {
        @Test
        fun `should return 200 and an incremented statistic`() = testApplicationWith(mockModule) {
            val testStatistic = getFirstTeamAsyncBankStatistic()
            coEvery { statisticService.incrementRequest(statistic = any()) } returns testStatistic.copy(
                requestsCount = 1L
            )

            val response = testClient.patch(urlString = "/v1/statistics/increment") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testStatistic)
            }

            response shouldHaveStatus HttpStatusCode.OK
            response.body<Statistic>() shouldBe getFirstTeamAsyncBankStatistic(requestsCount = 1L)
            coVerify(exactly = 1) { statisticService.incrementRequest(statistic = testStatistic) }
        }

        @Test
        fun `should return 404 and not found error response when exception occurs`() = testApplicationWith(mockModule) {
            val testStatistic = getFirstTeamAsyncBankStatistic()
            coEvery { statisticService.incrementRequest(statistic = any()) } throws NotFoundException(
                message = STATISTIC_NOT_FOUND_MESSAGE,
            )

            val response = testClient.patch(urlString = "/v1/statistics/increment") {
                contentType(type = ContentType.Application.Json)
                setBody(body = testStatistic)
            }

            response shouldHaveStatus HttpStatusCode.NotFound
            response.body<ErrorResponse>().shouldBeEqualToIgnoringFields(
                other = getStatisticNotFoundErrorResponse(),
                property = ErrorResponse::dateTime,
            )
            coVerify(exactly = 1) { statisticService.incrementRequest(statistic = testStatistic) }
        }
    }
}
