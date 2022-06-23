package de.hennihaus.services

import de.hennihaus.configurations.TaskConfiguration.ASYNC_BANK_STEP
import de.hennihaus.configurations.TaskConfiguration.SCHUFA_STEP
import de.hennihaus.configurations.TaskConfiguration.SYNC_BANK_STEP
import de.hennihaus.objectmothers.GithubObjectMother.getCreditFileResponse
import de.hennihaus.objectmothers.GithubObjectMother.getCreditUpdateFileRequest
import de.hennihaus.objectmothers.GithubObjectMother.getGithubCommitConfiguration
import de.hennihaus.objectmothers.GithubObjectMother.getGithubFileConfiguration
import de.hennihaus.objectmothers.GithubObjectMother.getRatingFileResponse
import de.hennihaus.objectmothers.GithubObjectMother.getRatingUpdateFileRequest
import de.hennihaus.objectmothers.OpenApiObjectMother.getUpdatedBankApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getUpdatedSchufaApi
import de.hennihaus.objectmothers.TaskObjectMother.getAsynchronousBankTask
import de.hennihaus.objectmothers.TaskObjectMother.getSchufaTask
import de.hennihaus.objectmothers.TaskObjectMother.getSynchronousBankTask
import de.hennihaus.services.GithubServiceImpl.Companion.UNKNOWN_INTEGRATION_STEP_MESSAGE
import de.hennihaus.services.callservices.GithubCallServiceImpl
import de.hennihaus.services.mapperservices.GithubMapperServiceImpl
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.Called
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GithubServiceTest {

    private val githubCall = mockk<GithubCallServiceImpl>()
    private val githubMapper = mockk<GithubMapperServiceImpl>()
    private val commitConfig = getGithubCommitConfiguration()
    private val schufaFileConfig = getGithubFileConfiguration()
    private val bankFileConfig = getGithubFileConfiguration()

    private val classUnderTest = GithubServiceImpl(
        githubCall = githubCall,
        githubMapper = githubMapper,
        commitConfig = commitConfig,
        schufaFileConfig = schufaFileConfig,
        bankFileConfig = bankFileConfig,
    )

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class UpdateOpenApi {
        @Test
        fun `should correctly update schufa with schufa task`() = runBlocking {
            val task = getSchufaTask(step = SCHUFA_STEP)
            coEvery { githubCall.getFile(fileConfig = any()) } returns getRatingFileResponse()
            coEvery { githubMapper.updateSchufaApi(api = any(), task = any()) } returns getUpdatedSchufaApi()
            coEvery { githubCall.updateFile(fileConfig = any(), file = any()) } returns mockk()

            classUnderTest.updateOpenApi(task = task)

            coVerifySequence {
                githubCall.getFile(
                    fileConfig = getGithubFileConfiguration(),
                )
                githubMapper.updateSchufaApi(
                    api = getUpdatedSchufaApi(),
                    task = task,
                )
                githubCall.updateFile(
                    fileConfig = getGithubFileConfiguration(),
                    file = getRatingUpdateFileRequest(),
                )
            }
        }

        @Test
        fun `should correctly update bank with sync bank task`() = runBlocking {
            val task = getSynchronousBankTask(step = SYNC_BANK_STEP)
            coEvery { githubCall.getFile(fileConfig = any()) } returns getCreditFileResponse()
            coEvery { githubMapper.updateBankApi(api = any(), task = any()) } returns getUpdatedBankApi()
            coEvery { githubCall.updateFile(fileConfig = any(), file = any()) } returns mockk()

            classUnderTest.updateOpenApi(task = task)

            coVerifySequence {
                githubCall.getFile(
                    fileConfig = getGithubFileConfiguration(),
                )
                githubMapper.updateBankApi(
                    api = getUpdatedBankApi(),
                    task = task,
                )
                githubCall.updateFile(
                    fileConfig = getGithubFileConfiguration(),
                    file = getCreditUpdateFileRequest(),
                )
            }
        }

        @Test
        fun `should update nothing with async bank step`() = runBlocking {
            val task = getAsynchronousBankTask(step = ASYNC_BANK_STEP)

            classUnderTest.updateOpenApi(task = task)

            verify { listOf(githubCall, githubMapper) wasNot Called }
        }

        @Test
        fun `should update nothing and throw exception when bank step is unknown`() = runBlocking {
            val task = getSchufaTask(step = -1)

            val result: IllegalArgumentException = shouldThrowExactly {
                classUnderTest.updateOpenApi(task = task)
            }

            result shouldHaveMessage UNKNOWN_INTEGRATION_STEP_MESSAGE
            verify { listOf(githubCall, githubMapper) wasNot Called }
        }
    }
}
