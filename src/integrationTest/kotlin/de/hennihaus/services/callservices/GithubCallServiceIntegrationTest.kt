package de.hennihaus.services.callservices

import de.hennihaus.configurations.GithubConfiguration
import de.hennihaus.configurations.GithubFileConfiguration
import de.hennihaus.configurations.GithubFileConfiguration.Companion.BANK_FILE_CONFIG
import de.hennihaus.configurations.GithubFileConfiguration.Companion.SCHUFA_FILE_CONFIG
import de.hennihaus.models.generated.github.GetFileResponse
import de.hennihaus.models.generated.github.UpdateFileResponse
import de.hennihaus.objectmothers.GithubObjectMother.getInvalidGithubFileConfiguration
import de.hennihaus.objectmothers.GithubObjectMother.getRatingUpdateFileRequest
import de.hennihaus.plugins.initKoin
import de.hennihaus.testutils.GithubTestUtils.getCurrentSha
import de.hennihaus.testutils.KoinTestUtils.KOIN_TEST_PROPERTIES
import de.hennihaus.testutils.propertiesAsMap
import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GithubCallServiceIntegrationTest : KoinTest {

    private val classUnderTest: GithubCallService by inject()

    @JvmField
    @RegisterExtension
    @Suppress("unused")
    val koinTestInstance = KoinTestExtension.create {
        initKoin(
            properties = propertiesAsMap(
                fileName = KOIN_TEST_PROPERTIES,
            ),
        )
    }

    @AfterAll
    fun cleanUp() = stopKoin()

    @Nested
    inner class GetFile {
        @Test
        fun `should return file response when schufa repo is called`() = runBlocking {
            val fileConfig: GithubFileConfiguration by inject(
                qualifier = named(name = SCHUFA_FILE_CONFIG),
            )

            val result: GetFileResponse = classUnderTest.getFile(
                fileConfig = fileConfig,
            )

            result should {
                it.type shouldBe "file"
                it.sha.shouldNotBeEmpty()
                it.content.shouldNotBeEmpty()
            }
        }

        @Test
        fun `should return file response when bank repo is called`() = runBlocking {
            val fileConfig: GithubFileConfiguration by inject(
                qualifier = named(name = BANK_FILE_CONFIG),
            )

            val result: GetFileResponse = classUnderTest.getFile(
                fileConfig = fileConfig,
            )

            result should {
                it.type shouldBe "file"
                it.sha.shouldNotBeEmpty()
                it.content.shouldNotBeEmpty()
            }
        }

        @Test
        fun `should throw a client request exception when file config is invalid`() = runBlocking {
            val fileConfig = getInvalidGithubFileConfiguration()

            val result: ClientRequestException = shouldThrowExactly {
                classUnderTest.getFile(
                    fileConfig = fileConfig,
                )
            }

            result.response shouldHaveStatus HttpStatusCode.NotFound
        }
    }

    @Nested
    inner class UpdateFile {

        private lateinit var schufaSha: String
        private lateinit var bankSha: String

        @BeforeEach
        fun init() = runBlocking {
            val githubConfig by inject<GithubConfiguration>()
            val schufaFileConfig by inject<GithubFileConfiguration>(qualifier = named(name = SCHUFA_FILE_CONFIG))
            val bankFileConfig by inject<GithubFileConfiguration>(qualifier = named(name = BANK_FILE_CONFIG))

            schufaSha = getCurrentSha(
                fileConfig = schufaFileConfig,
                githubConfig = githubConfig,
            )
            bankSha = getCurrentSha(
                fileConfig = bankFileConfig,
                githubConfig = githubConfig,
            )
        }

        @Test
        fun `should return updated file when schufa repo is called`() = runBlocking<Unit> {
            val fileConfig: GithubFileConfiguration by inject(
                qualifier = named(name = SCHUFA_FILE_CONFIG),
            )
            val file = getRatingUpdateFileRequest().copy(
                sha = schufaSha,
                branch = fileConfig.branch,
            )

            val result: UpdateFileResponse = classUnderTest.updateFile(
                fileConfig = fileConfig,
                file = file,
            )

            result.content.shouldNotBeNull()
        }

        @Test
        fun `should return updated file when bank repo is called`() = runBlocking<Unit> {
            val fileConfig: GithubFileConfiguration by inject(
                qualifier = named(name = BANK_FILE_CONFIG),
            )
            val file = getRatingUpdateFileRequest().copy(
                sha = bankSha,
                branch = fileConfig.branch,
            )

            val result: UpdateFileResponse = classUnderTest.updateFile(
                fileConfig = fileConfig,
                file = file,
            )

            result.content.shouldNotBeNull()
        }

        @Test
        fun `should throw a client request exception when file config is invalid`() = runBlocking {
            val fileConfig = getInvalidGithubFileConfiguration()
            val file = getRatingUpdateFileRequest()

            val result: ClientRequestException = shouldThrowExactly {
                classUnderTest.updateFile(
                    fileConfig = fileConfig,
                    file = file,
                )
            }

            result.response shouldHaveStatus HttpStatusCode.NotFound
        }
    }
}
