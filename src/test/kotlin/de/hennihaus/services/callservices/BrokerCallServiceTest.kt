package de.hennihaus.services.callservices

import de.hennihaus.objectmothers.BrokerObjectMother.getDeleteJobsErrorResponse
import de.hennihaus.objectmothers.BrokerObjectMother.getQueuesErrorResponse
import de.hennihaus.objectmothers.BrokerObjectMother.getTopicsErrorResponse
import de.hennihaus.objectmothers.ConfigurationObjectMother.getBrokerConfiguration
import de.hennihaus.plugins.BrokerException
import de.hennihaus.testutils.MockEngineBuilder
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.throwable.shouldHaveMessage
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

            val result: BrokerException = shouldThrowExactly {
                classUnderTest.getAllQueues()
            }

            result shouldHaveMessage getQueuesErrorResponse().error!!
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

            val result: BrokerException = shouldThrowExactly {
                classUnderTest.getAllTopics()
            }

            result shouldHaveMessage getTopicsErrorResponse().error!!
        }
    }

    @Nested
    inner class DeleteAllJobs {
        @Test
        fun `should throw an exception when request is invalid`() = runBlocking {
            val engine = MockEngineBuilder.getMockEngine(
                content = File("./src/test/resources/broker/deleteJobsErrorResponse.json").readText(),
                status = HttpStatusCode.OK
            )
            classUnderTest = BrokerCallServiceImpl(
                engine = engine,
                config = getBrokerConfiguration(),
            )

            val result: BrokerException = shouldThrowExactly {
                classUnderTest.deleteAllJobs()
            }

            result shouldHaveMessage getDeleteJobsErrorResponse().error!!
        }
    }
}
