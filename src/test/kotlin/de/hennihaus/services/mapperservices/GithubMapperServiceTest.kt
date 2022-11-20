package de.hennihaus.services.mapperservices

import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.bamdatamodel.objectmothers.CreditConfigurationObjectMother.getCreditConfigurationWithNoEmptyFields
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getExampleTeam
import de.hennihaus.models.IntegrationStep
import de.hennihaus.models.generated.openapi.BankApi
import de.hennihaus.models.generated.openapi.SchufaApi
import de.hennihaus.objectmothers.GithubObjectMother
import de.hennihaus.objectmothers.OpenApiObjectMother.getByCreditConfigurationUpdatedBankApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getByTaskUpdatedBankApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getByTaskUpdatedBankInfo
import de.hennihaus.objectmothers.OpenApiObjectMother.getByTaskUpdatedSchufaApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getByTaskUpdatedSchufaInfo
import de.hennihaus.objectmothers.OpenApiObjectMother.getByTeamUpdatedBankApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getByTeamUpdatedSchufaApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getNonUpdatedByCreditConfigurationBankApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getNonUpdatedByTaskBankApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getNonUpdatedByTaskSchufaApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getNonUpdatedByTeamBankApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getNonUpdatedByTeamSchufaApi
import de.hennihaus.objectmothers.ParameterObjectMother.AMOUNT_IN_EUROS_PARAMETER
import de.hennihaus.objectmothers.ParameterObjectMother.PASSWORD_EXAMPLE
import de.hennihaus.objectmothers.ParameterObjectMother.SOCIAL_SECURITY_NUMBER_PARAMETER
import de.hennihaus.objectmothers.ParameterObjectMother.USERNAME_EXAMPLE
import de.hennihaus.objectmothers.ResponseObjectMother.OK_CODE
import de.hennihaus.objectmothers.TaskObjectMother.getSchufaTask
import de.hennihaus.objectmothers.TaskObjectMother.getSynchronousBankTask
import de.hennihaus.services.mapperservices.GithubMapperService.Companion.NO_BANK_STEP_MESSAGE
import de.hennihaus.services.mapperservices.GithubMapperService.Companion.NO_SCHUFA_STEP_MESSAGE
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GithubMapperServiceTest {

    private val defaultTitle = GithubObjectMother.DEFAULT_TITLE

    private val classUnderTest = GithubMapperService(
        defaultTitle = defaultTitle,
    )

    @Nested
    inner class UpdateSchufaApiByTask {
        @Test
        fun `should return correctly updated schufa api when isOpenApiVerbose = true`() {
            val api = getNonUpdatedByTaskSchufaApi()
            val task = getSchufaTask(isOpenApiVerbose = true)

            val result: SchufaApi = classUnderTest.updateSchufaApi(
                api = api,
                task = task,
            )

            result shouldBe getByTaskUpdatedSchufaApi()
        }

        @Test
        fun `should return correctly updated schufa api when isOpenApiVerbose = false`() {
            val api = getNonUpdatedByTaskSchufaApi()
            val task = getSchufaTask(isOpenApiVerbose = false)

            val result: SchufaApi = classUnderTest.updateSchufaApi(
                api = api,
                task = task,
            )

            result shouldBe getByTaskUpdatedSchufaApi(
                info = getByTaskUpdatedSchufaInfo(
                    description = "",
                ),
            )
        }

        @Test
        fun `should throw an exception when task step != SCHUFA_STEP`() {
            val api = getNonUpdatedByTaskSchufaApi()
            val task = getSchufaTask(integrationStep = IntegrationStep.SYNC_BANK_STEP)

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateSchufaApi(
                    api = api,
                    task = task,
                )
            }

            result shouldHaveMessage NO_SCHUFA_STEP_MESSAGE
        }

        @Test
        fun `should throw an exception when task has not same parameters as schufa api`() {
            val api = getNonUpdatedByTaskSchufaApi()
            val task = getSchufaTask(
                parameters = emptyList(),
            )

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateSchufaApi(
                    api = api,
                    task = task,
                )
            }

            result shouldHaveMessage """
                parameter $SOCIAL_SECURITY_NUMBER_PARAMETER not found in Task while updating SchufaApi
            """.trimIndent()
        }

        @Test
        fun `should throw an exception when task has not same responses as schufa api`() {
            val api = getNonUpdatedByTaskSchufaApi()
            val task = getSchufaTask(
                responses = emptyList(),
            )

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateSchufaApi(
                    api = api,
                    task = task,
                )
            }

            result shouldHaveMessage "response $OK_CODE not found in Task while updating SchufaApi"
        }
    }

    @Nested
    inner class UpdateSchufaApiByTeam {
        @Test
        fun `should return correctly updated schufa api`() {
            val api = getNonUpdatedByTeamSchufaApi()
            val team = getExampleTeam(
                username = USERNAME_EXAMPLE,
                password = PASSWORD_EXAMPLE,
            )

            val result: SchufaApi = classUnderTest.updateSchufaApi(
                api = api,
                team = team,
            )

            result shouldBe getByTeamUpdatedSchufaApi()
        }

        @Test
        fun `should throw an exception when team type != EXAMPLE`() {
            val api = getNonUpdatedByTeamSchufaApi()
            val team = getExampleTeam(
                type = TeamType.REGULAR,
            )

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateSchufaApi(
                    api = api,
                    team = team,
                )
            }

            result shouldHaveMessage "provided team is not an example team"
        }
    }

    @Nested
    inner class UpdateBankApiByTask {
        @Test
        fun `should return correctly updated bank api when isOpenApiVerbose = true`() {
            val api = getNonUpdatedByTaskBankApi()
            val task = getSynchronousBankTask(isOpenApiVerbose = true)

            val result: BankApi = classUnderTest.updateBankApi(
                api = api,
                task = task,
            )

            result shouldBe getByTaskUpdatedBankApi()
        }

        @Test
        fun `should return correctly updated bank api when isOpenApiVerbose = false`() {
            val api = getNonUpdatedByTaskBankApi()
            val task = getSynchronousBankTask(isOpenApiVerbose = false)

            val result: BankApi = classUnderTest.updateBankApi(
                api = api,
                task = task,
            )

            result shouldBe getByTaskUpdatedBankApi(
                info = getByTaskUpdatedBankInfo(
                    description = "",
                ),
            )
        }

        @Test
        fun `should throw an exception when task step != SYNC_BANK_STEP`() {
            val api = getNonUpdatedByTaskBankApi()
            val task = getSynchronousBankTask(integrationStep = IntegrationStep.SCHUFA_STEP)

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateBankApi(
                    api = api,
                    task = task,
                )
            }

            result shouldHaveMessage NO_BANK_STEP_MESSAGE
        }

        @Test
        fun `should throw an exception when task has not same parameters as bank api`() {
            val api = getNonUpdatedByTaskBankApi()
            val task = getSynchronousBankTask(
                parameters = emptyList(),
            )

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateBankApi(
                    api = api,
                    task = task,
                )
            }

            result shouldHaveMessage "parameter $AMOUNT_IN_EUROS_PARAMETER not found in Task while updating BankApi"
        }

        @Test
        fun `should throw an exception when task has not same responses as bank api`() {
            val api = getNonUpdatedByTaskBankApi()
            val task = getSynchronousBankTask(
                responses = emptyList(),
            )

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateBankApi(
                    api = api,
                    task = task,
                )
            }

            result shouldHaveMessage "response $OK_CODE not found in Task while updating BankApi"
        }
    }

    @Nested
    inner class UpdateBankApiByTeam {
        @Test
        fun `should return correctly updated bank api`() {
            val api = getNonUpdatedByTeamBankApi()
            val team = getExampleTeam(
                username = USERNAME_EXAMPLE,
                password = PASSWORD_EXAMPLE,
            )

            val result: BankApi = classUnderTest.updateBankApi(
                api = api,
                team = team,
            )

            result shouldBe getByTeamUpdatedBankApi()
        }

        @Test
        fun `should throw an exception when team type != EXAMPLE`() {
            val api = getNonUpdatedByTeamBankApi()
            val team = getExampleTeam(
                type = TeamType.REGULAR,
            )

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateBankApi(
                    api = api,
                    team = team,
                )
            }

            result shouldHaveMessage "provided team is not an example team"
        }
    }

    @Nested
    inner class UpdateBankApiByCreditConfiguration {
        @Test
        fun `should return correctly updated bank api`() {
            val api = getNonUpdatedByCreditConfigurationBankApi()
            val creditConfiguration = getCreditConfigurationWithNoEmptyFields()

            val result: BankApi = classUnderTest.updateBankApi(
                api = api,
                creditConfiguration = creditConfiguration,
            )

            result shouldBe getByCreditConfigurationUpdatedBankApi()
        }
    }
}
