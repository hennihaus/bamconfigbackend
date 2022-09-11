package de.hennihaus.services.callservices

import de.hennihaus.objectmothers.GithubObjectMother.getGithubConfiguration
import de.hennihaus.objectmothers.GithubObjectMother.getGithubFileConfiguration
import de.hennihaus.objectmothers.GithubObjectMother.getRatingUpdateFileRequest
import de.hennihaus.testutils.MockEngineBuilder
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GithubCallServiceTest {

    lateinit var classUnderTest: GithubCallService

    @Nested
    inner class GetFile {
        @Test
        fun `should throw a server response exception when status code = 500`() = runBlocking {
            val engine = MockEngineBuilder.getMockEngine(
                content = "",
                status = HttpStatusCode.InternalServerError,
            )
            classUnderTest = GithubCallService(
                engine = engine,
                config = getGithubConfiguration(),
            )
            val fileConfig = getGithubFileConfiguration()

            val result: ServerResponseException = shouldThrowExactly {
                classUnderTest.getFile(
                    fileConfig = fileConfig,
                )
            }

            result.response shouldHaveStatus HttpStatusCode.InternalServerError
        }

        @Test
        fun `should throw an exception when body is not parseable`() = runBlocking<Unit> {
            val engine = MockEngineBuilder.getMockEngine(
                content = "{}",
                status = HttpStatusCode.OK,
            )
            classUnderTest = GithubCallService(
                engine = engine,
                config = getGithubConfiguration(),
            )
            val fileConfig = getGithubFileConfiguration()

            shouldThrowAny {
                classUnderTest.getFile(
                    fileConfig = fileConfig,
                )
            }
        }
    }

    @Nested
    inner class UpdateFile {
        @Test
        fun `should throw a server response exception when statusCode = 500`() = runBlocking {
            val engine = MockEngineBuilder.getMockEngine(
                content = "",
                status = HttpStatusCode.InternalServerError,
            )
            classUnderTest = GithubCallService(
                engine = engine,
                config = getGithubConfiguration(),
            )
            val fileConfig = getGithubFileConfiguration()
            val file = getRatingUpdateFileRequest()

            val result: ServerResponseException = shouldThrowExactly {
                classUnderTest.updateFile(
                    fileConfig = fileConfig,
                    file = file,
                )
            }

            result.response shouldHaveStatus HttpStatusCode.InternalServerError
        }

        @Test
        fun `should throw an exception when body is not parseable`() = runBlocking<Unit> {
            val engine = MockEngineBuilder.getMockEngine(
                content = "",
                status = HttpStatusCode.InternalServerError,
            )
            classUnderTest = GithubCallService(
                engine = engine,
                config = getGithubConfiguration(),
            )
            val fileConfig = getGithubFileConfiguration()
            val file = getRatingUpdateFileRequest()

            shouldThrowAny {
                classUnderTest.updateFile(
                    fileConfig = fileConfig,
                    file = file,
                )
            }
        }
    }
}
