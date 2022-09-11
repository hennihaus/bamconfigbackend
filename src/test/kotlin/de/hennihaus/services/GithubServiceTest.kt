package de.hennihaus.services

import de.hennihaus.models.generated.IntegrationStep
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
import de.hennihaus.services.callservices.GithubCallService
import de.hennihaus.services.mapperservices.GithubMapperService
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

    private val githubCall = mockk<GithubCallService>()
    private val githubMapper = mockk<GithubMapperService>()
    private val commitConfig = getGithubCommitConfiguration()
    private val schufaFileConfig = getGithubFileConfiguration()
    private val bankFileConfig = getGithubFileConfiguration()

    private val classUnderTest = GithubService(
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
            val task = getSchufaTask(integrationStep = IntegrationStep.SCHUFA_STEP)
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
            val task = getSynchronousBankTask(integrationStep = IntegrationStep.SYNC_BANK_STEP)
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
            val task = getAsynchronousBankTask(integrationStep = IntegrationStep.ASYNC_BANK_STEP)

            classUnderTest.updateOpenApi(task = task)

            verify { listOf(githubCall, githubMapper) wasNot Called }
        }
    }
}
