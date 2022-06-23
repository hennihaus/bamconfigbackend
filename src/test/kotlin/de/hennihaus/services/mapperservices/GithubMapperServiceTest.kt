package de.hennihaus.services.mapperservices

import de.hennihaus.models.generated.openapi.BankApi
import de.hennihaus.models.generated.openapi.SchufaApi
import de.hennihaus.objectmothers.BankObjectMother.getVBank
import de.hennihaus.objectmothers.GithubObjectMother
import de.hennihaus.objectmothers.OpenApiObjectMother.getNonUpdatedBankApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getNonUpdatedSchufaApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getUpdatedBankApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getUpdatedBankInfo
import de.hennihaus.objectmothers.OpenApiObjectMother.getUpdatedSchufaApi
import de.hennihaus.objectmothers.OpenApiObjectMother.getUpdatedSchufaInfo
import de.hennihaus.objectmothers.ParameterObjectMother.SOCIAL_SECURITY_NUMBER_PARAMETER
import de.hennihaus.objectmothers.ResponseObjectMother.OK_CODE
import de.hennihaus.objectmothers.TaskObjectMother.getSchufaTask
import de.hennihaus.objectmothers.TaskObjectMother.getSynchronousBankTask
import de.hennihaus.services.mapperservices.GithubMapperServiceImpl.Companion.AMOUNT_IN_EUROS_PARAMETER
import de.hennihaus.services.mapperservices.GithubMapperServiceImpl.Companion.NO_BANK_STEP_MESSAGE
import de.hennihaus.services.mapperservices.GithubMapperServiceImpl.Companion.NO_CONFIGURATION_MESSAGE
import de.hennihaus.services.mapperservices.GithubMapperServiceImpl.Companion.NO_SCHUFA_STEP_MESSAGE
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GithubMapperServiceTest {

    private val defaultTitle = GithubObjectMother.DEFAULT_TITLE

    private val classUnderTest = GithubMapperServiceImpl(
        defaultTitle = defaultTitle,
    )

    @Nested
    inner class UpdateSchufaApi {
        @Test
        fun `should return correctly updated schufa api when isOpenApiVerbose = true`() = runBlocking {
            val api = getNonUpdatedSchufaApi()
            val task = getSchufaTask(isOpenApiVerbose = true)

            val result: SchufaApi = classUnderTest.updateSchufaApi(
                api = api,
                task = task,
            )

            result shouldBe getUpdatedSchufaApi()
        }

        @Test
        fun `should return correctly updated schufa api when isOpenApiVerbose = false`() = runBlocking {
            val api = getNonUpdatedSchufaApi()
            val task = getSchufaTask(isOpenApiVerbose = false)

            val result: SchufaApi = classUnderTest.updateSchufaApi(
                api = api,
                task = task,
            )

            result shouldBe getUpdatedSchufaApi(
                info = getUpdatedSchufaInfo(
                    description = "",
                ),
            )
        }

        @Test
        fun `should throw an exception when task step != SCHUFA_STEP`() = runBlocking {
            val api = getNonUpdatedSchufaApi()
            val task = getSchufaTask(step = -1)

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateSchufaApi(
                    api = api,
                    task = task,
                )
            }

            result shouldHaveMessage NO_SCHUFA_STEP_MESSAGE
        }

        @Test
        fun `should throw an exception when task has not same parameters as schufa api`() = runBlocking {
            val api = getNonUpdatedSchufaApi()
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
                Parameter $SOCIAL_SECURITY_NUMBER_PARAMETER not found in Task while updating SchufaApi!
            """.trimIndent()
        }

        @Test
        fun `should throw an exception when task has not same responses as schufa api`() = runBlocking {
            val api = getNonUpdatedSchufaApi()
            val task = getSchufaTask(
                responses = emptyList(),
            )

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateSchufaApi(
                    api = api,
                    task = task,
                )
            }

            result shouldHaveMessage "Response $OK_CODE not found in Task while updating SchufaApi!"
        }
    }

    @Nested
    inner class UpdateBankApi {
        @Test
        fun `should return correctly updated bank api when isOpenApiVerbose = true`() = runBlocking {
            val api = getNonUpdatedBankApi()
            val task = getSynchronousBankTask(isOpenApiVerbose = true)

            val result: BankApi = classUnderTest.updateBankApi(
                api = api,
                task = task,
            )

            result shouldBe getUpdatedBankApi()
        }

        @Test
        fun `should return correctly updated bank api when isOpenApiVerbose = false`() = runBlocking {
            val api = getNonUpdatedBankApi()
            val task = getSynchronousBankTask(isOpenApiVerbose = false)

            val result: BankApi = classUnderTest.updateBankApi(
                api = api,
                task = task,
            )

            result shouldBe getUpdatedBankApi(
                info = getUpdatedBankInfo(
                    description = "",
                ),
            )
        }

        @Test
        fun `should throw an exception when task step != BANK_STEP`() = runBlocking {
            val api = getNonUpdatedBankApi()
            val task = getSynchronousBankTask(step = -1)

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateBankApi(
                    api = api,
                    task = task,
                )
            }

            result shouldHaveMessage NO_BANK_STEP_MESSAGE
        }

        @Test
        fun `should throw an exception when task has not same parameters as bank api`() = runBlocking {
            val api = getNonUpdatedBankApi()
            val task = getSynchronousBankTask(
                parameters = emptyList(),
            )

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateBankApi(
                    api = api,
                    task = task,
                )
            }

            result shouldHaveMessage "Parameter $AMOUNT_IN_EUROS_PARAMETER not found in Task while updating BankApi!"
        }

        @Test
        fun `should throw an exception when task has not same responses as bank api`() = runBlocking {
            val api = getNonUpdatedBankApi()
            val task = getSynchronousBankTask(
                responses = emptyList(),
            )

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateBankApi(
                    api = api,
                    task = task,
                )
            }

            result shouldHaveMessage "Response $OK_CODE not found in Task while updating BankApi!"
        }

        @Test
        fun `should throw an exception when task has no banks`() = runBlocking {
            val api = getNonUpdatedBankApi()
            val task = getSynchronousBankTask(
                banks = emptyList(),
            )

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateBankApi(
                    api = api,
                    task = task,
                )
            }

            result shouldHaveMessage NO_CONFIGURATION_MESSAGE
        }

        @Test
        fun `should throw an exception when bank of task has no configuration`() = runBlocking {
            val api = getNonUpdatedBankApi()
            val task = getSynchronousBankTask(
                banks = listOf(
                    getVBank(
                        creditConfiguration = null,
                    ),
                ),
            )

            val result: IllegalStateException = shouldThrowExactly {
                classUnderTest.updateBankApi(
                    api = api,
                    task = task,
                )
            }

            result shouldHaveMessage NO_CONFIGURATION_MESSAGE
        }
    }
}
