package de.hennihaus.services

import de.hennihaus.bamdatamodel.objectmothers.CreditConfigurationObjectMother.getCreditConfigurationWithNoEmptyFields
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getExampleTeam
import de.hennihaus.models.IntegrationStep
import de.hennihaus.objectmothers.GithubObjectMother.getCreditFileResponse
import de.hennihaus.objectmothers.GithubObjectMother.getCreditUpdateFileRequest
import de.hennihaus.objectmothers.GithubObjectMother.getGithubCommitConfiguration
import de.hennihaus.objectmothers.GithubObjectMother.getGithubFileConfiguration
import de.hennihaus.objectmothers.GithubObjectMother.getRatingFileResponse
import de.hennihaus.objectmothers.GithubObjectMother.getRatingUpdateFileRequest
import de.hennihaus.objectmothers.OpenApiObjectMother.getByCreditConfigurationUpdatedBankApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getByTaskUpdatedBankApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getByTaskUpdatedSchufaApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getByTeamUpdatedBankApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getByTeamUpdatedSchufaApi
import de.hennihaus.objectmothers.TaskObjectMother.getAsynchronousBankTask
import de.hennihaus.objectmothers.TaskObjectMother.getSchufaTask
import de.hennihaus.objectmothers.TaskObjectMother.getSynchronousBankTask
import de.hennihaus.services.callservices.GithubCallService
import de.hennihaus.services.mapperservices.GithubMapperService
import io.mockk.Called
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.every
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
            every { githubMapper.updateSchufaApi(api = any(), task = any()) } returns getByTaskUpdatedSchufaApi()
            coEvery { githubCall.updateFile(fileConfig = any(), file = any()) } returns mockk()

            classUnderTest.updateOpenApi(task = task)

            coVerifySequence {
                githubCall.getFile(
                    fileConfig = getGithubFileConfiguration(),
                )
                githubMapper.updateSchufaApi(
                    api = getByTaskUpdatedSchufaApi(),
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
            every { githubMapper.updateBankApi(api = any(), task = any()) } returns getByTaskUpdatedBankApi()
            coEvery { githubCall.updateFile(fileConfig = any(), file = any()) } returns mockk()

            classUnderTest.updateOpenApi(task = task)

            coVerifySequence {
                githubCall.getFile(
                    fileConfig = getGithubFileConfiguration(),
                )
                githubMapper.updateBankApi(
                    api = getByTaskUpdatedBankApi(),
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

        @Test
        fun `should correctly update bank with creditConfiguration`() = runBlocking {
            val creditConfiguration = getCreditConfigurationWithNoEmptyFields()
            coEvery { githubCall.getFile(fileConfig = any()) } returns getCreditFileResponse()
            every { githubMapper.updateBankApi(api = any(), creditConfiguration = any()) } returns getByCreditConfigurationUpdatedBankApi()
            coEvery { githubCall.updateFile(fileConfig = any(), file = any()) } returns mockk()

            classUnderTest.updateOpenApi(creditConfiguration = creditConfiguration)

            coVerifySequence {
                githubCall.getFile(
                    fileConfig = getGithubFileConfiguration(),
                )
                githubMapper.updateBankApi(
                    api = getByCreditConfigurationUpdatedBankApi(),
                    creditConfiguration = creditConfiguration,
                )
                githubCall.updateFile(
                    fileConfig = getGithubFileConfiguration(),
                    file = getCreditUpdateFileRequest(),
                )
            }
        }

        @Test
        fun `should correctly update schufa and bank with team`() = runBlocking {
            val team = getExampleTeam()
            coEvery { githubCall.getFile(fileConfig = any()) } returnsMany listOf(
                getRatingFileResponse(),
                getCreditFileResponse(),
            )
            every { githubMapper.updateSchufaApi(api = any(), team = any()) } returns getByTeamUpdatedSchufaApi()
            every { githubMapper.updateBankApi(api = any(), team = any()) } returns getByTeamUpdatedBankApi()
            coEvery { githubCall.updateFile(fileConfig = any(), file = any()) } returnsMany listOf(
                mockk(),
                mockk(),
            )

            classUnderTest.updateOpenApi(team = team)

            coVerifySequence {
                githubCall.getFile(
                    fileConfig = getGithubFileConfiguration(),
                )
                githubMapper.updateSchufaApi(
                    api = getByTeamUpdatedSchufaApi(),
                    team = team,
                )
                githubCall.updateFile(
                    fileConfig = getGithubFileConfiguration(),
                    file = getRatingUpdateFileRequest(),
                )
                githubCall.getFile(
                    fileConfig = getGithubFileConfiguration(),
                )
                githubMapper.updateBankApi(
                    api = getByTeamUpdatedBankApi(),
                    team = team,
                )
                githubCall.updateFile(
                    fileConfig = getGithubFileConfiguration(),
                    file = getCreditUpdateFileRequest(),
                )
            }
        }
    }
}
