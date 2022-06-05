package de.hennihaus.services.callservices

import de.hennihaus.objectmothers.ConfigurationObjectMother.getBrokerConfiguration
import de.hennihaus.plugins.BrokerException
import de.hennihaus.plugins.ErrorMessage
import de.hennihaus.testutils.MockEngineBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.beInstanceOf
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

class BrokerCallServiceTest {

    lateinit var classUnderTest: BrokerCallService

    @Nested
    inner class GetAllQueues {
        @Test
        fun `should throw an exception when request is invalid`() = runBlocking {
            val engine = MockEngineBuilder.getMockEngine(
                content = File("./src/test/resources/broker/getQueuesErrorResponse.json").readText(),
                status = HttpStatusCode.OK,
            )
            classUnderTest = BrokerCallServiceImpl(
                engine = engine,
                config = getBrokerConfiguration(),
            )

            val result = shouldThrow<BrokerException> { classUnderTest.getAllQueues() }

            result should beInstanceOf(BrokerException::class)
            result.message shouldNotBe ErrorMessage.BROKER_EXCEPTION_DEFAULT_MESSAGE
        }
    }

    @Nested
    inner class GetAllTopics {
        @Test
        fun `should throw an exception when request is invalid`() = runBlocking {
            val engine = MockEngineBuilder.getMockEngine(
                content = File("./src/test/resources/broker/getTopicsErrorResponse.json").readText(),
                status = HttpStatusCode.OK
            )
            classUnderTest = BrokerCallServiceImpl(
                engine = engine,
                config = getBrokerConfiguration()
            )

            val result = shouldThrow<BrokerException> { classUnderTest.getAllTopics() }

            result should beInstanceOf(BrokerException::class)
            result.message shouldNotBe ErrorMessage.BROKER_EXCEPTION_DEFAULT_MESSAGE
        }
    }

    @Nested
    inner class DeleteAllJobs {
        @Test
        fun `should throw an exception when request is invalid`() = runBlocking {
            val engine = MockEngineBuilder.getMockEngine(
                content = File("./src/test/resources/broker/DeleteJobsErrorResponse.json").readText(),
                status = HttpStatusCode.OK
            )
            classUnderTest = BrokerCallServiceImpl(
                engine = engine,
                config = getBrokerConfiguration(),
            )

            val result = shouldThrow<BrokerException> { classUnderTest.deleteAllJobs() }

            result should beInstanceOf(BrokerException::class)
            result.message shouldNotBe ErrorMessage.BROKER_EXCEPTION_DEFAULT_MESSAGE
        }
    }
}
