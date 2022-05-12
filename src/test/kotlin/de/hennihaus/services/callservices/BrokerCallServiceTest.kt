package de.hennihaus.services.callservices

import de.hennihaus.plugins.BrokerException
import de.hennihaus.plugins.ErrorMessage
import de.hennihaus.testutils.MockClientBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

class BrokerCallServiceTest {

    lateinit var engine: MockEngine
    lateinit var client: HttpClient
    lateinit var classUnderTest: BrokerCallService

    @Nested
    inner class GetAllQueues {
        @Test
        fun `should throw an exception when request is invalid`() = runBlocking {
            engine = MockClientBuilder.getMockEngine(
                content = File("./src/test/resources/broker/getQueuesErrorResponse.json").readText(),
                status = HttpStatusCode.OK
            )
            client = MockClientBuilder.getMockClient(engine = engine, contentType = ContentType.Application.Json)
            classUnderTest = BrokerCallServiceImpl(client = client)

            val result = shouldThrow<BrokerException> { classUnderTest.getAllQueues() }

            result should beInstanceOf(BrokerException::class)
            result.message shouldNotBe ErrorMessage.BROKER_EXCEPTION_DEFAULT_MESSAGE
        }
    }

    @Nested
    inner class GetAllTopics {
        @Test
        fun `should throw an exception when request is invalid`() = runBlocking {
            engine = MockClientBuilder.getMockEngine(
                content = File("./src/test/resources/broker/getTopicsErrorResponse.json").readText(),
                status = HttpStatusCode.OK
            )
            client = MockClientBuilder.getMockClient(engine = engine, contentType = ContentType.Application.Json)
            classUnderTest = BrokerCallServiceImpl(client = client)

            val result = shouldThrow<BrokerException> { classUnderTest.getAllTopics() }

            result should beInstanceOf(BrokerException::class)
            result.message shouldNotBe ErrorMessage.BROKER_EXCEPTION_DEFAULT_MESSAGE
        }
    }

    @Nested
    inner class DeleteAllJobs {
        @Test
        fun `should throw an exception when request is invalid`() = runBlocking {
            engine = MockClientBuilder.getMockEngine(
                content = File("./src/test/resources/broker/DeleteJobsErrorResponse.json").readText(),
                status = HttpStatusCode.OK
            )
            client = MockClientBuilder.getMockClient(engine = engine, contentType = ContentType.Application.Json)
            classUnderTest = BrokerCallServiceImpl(client = client)

            val result = shouldThrow<BrokerException> { classUnderTest.deleteAllJobs() }

            result should beInstanceOf(BrokerException::class)
            result.message shouldNotBe ErrorMessage.BROKER_EXCEPTION_DEFAULT_MESSAGE
        }
    }
}
