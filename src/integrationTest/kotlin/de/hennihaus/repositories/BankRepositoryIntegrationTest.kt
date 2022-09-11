package de.hennihaus.repositories

import de.hennihaus.configurations.ExposedConfiguration.DATABASE_HOST
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_PORT
import de.hennihaus.models.generated.Bank
import de.hennihaus.objectmothers.BankObjectMother.getAsyncBank
import de.hennihaus.objectmothers.CreditConfigurationObjectMother.getCreditConfigurationWithNoEmptyFields
import de.hennihaus.objectmothers.ExposedContainerObjectMother
import de.hennihaus.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.objectmothers.TeamObjectMother.getSecondTeam
import de.hennihaus.objectmothers.TeamObjectMother.getThirdTeam
import de.hennihaus.plugins.initKoin
import de.hennihaus.testutils.containers.ExposedContainer
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BankRepositoryIntegrationTest : KoinTest {

    private val exposedContainer = ExposedContainer.INSTANCE
    private val classUnderTest: BankRepository by inject()

    @JvmField
    @RegisterExtension
    @Suppress("unused")
    val koinTestExtension = KoinTestExtension.create {
        initKoin(
            properties = mapOf(
                DATABASE_HOST to exposedContainer.host,
                DATABASE_PORT to exposedContainer.firstMappedPort.toString(),
            )
        )
    }

    @BeforeEach
    fun init() = ExposedContainer.resetState()

    @AfterAll
    fun cleanUp() = stopKoin()

    @Nested
    inner class GetById {
        @Test
        fun `should find a bank by id`() = runBlocking<Unit> {
            val id = ExposedContainerObjectMother.BANK_UUID

            val result: Bank? = classUnderTest.getById(id = id)

            result.shouldNotBeNull()
            result.creditConfiguration.shouldNotBeNull()
            result.teams.shouldNotBeEmpty()
        }

        @Test
        fun `should return null when id is not in db`() = runBlocking {
            val id = ExposedContainerObjectMother.UNKNOWN_UUID

            val result: Bank? = classUnderTest.getById(id = id)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class GetAll {
        @Test
        fun `should return at least one bank`() = runBlocking<Unit> {
            val result: List<Bank> = classUnderTest.getAll()

            result.shouldNotBeEmpty()
        }
    }

    @Nested
    inner class DeleteById {
        @Test
        fun `should return true when one bank was deleted by id`() = runBlocking {
            val id = ExposedContainerObjectMother.BANK_UUID

            val result: Boolean = classUnderTest.deleteById(id = id)

            result.shouldBeTrue()
        }

        @Test
        fun `should return false when no bank was deleted by name`() = runBlocking {
            val id = ExposedContainerObjectMother.UNKNOWN_UUID

            val result: Boolean = classUnderTest.deleteById(id = id)

            result.shouldBeFalse()
        }
    }

    @Nested
    inner class Save {
        @Test
        fun `should save an existing bank with creditConfiguration`() = runBlocking<Unit> {
            val bank = getAsyncBank(
                uuid = ExposedContainerObjectMother.BANK_UUID,
                jmsQueue = "NewJmsName",
                creditConfiguration = getCreditConfigurationWithNoEmptyFields(
                    minAmountInEuros = 0,
                ),
                teams = listOf(
                    getFirstTeam(),
                    getSecondTeam(),
                    getThirdTeam(),
                ),
            )

            val result: Bank = classUnderTest.save(entry = bank)

            result.shouldBeEqualToIgnoringFields(
                other = bank,
                property = Bank::teams,
            )
            result.teams shouldHaveSize bank.teams.size
        }

        @Test
        fun `should save an existing bank without creditConfiguration`() = runBlocking<Unit> {
            val bank = getAsyncBank(
                uuid = ExposedContainerObjectMother.BANK_UUID,
                jmsQueue = "NewJmsName",
                creditConfiguration = null,
            )

            val result: Bank = classUnderTest.save(entry = bank)

            result.shouldBeEqualToIgnoringFields(
                other = bank,
                property = Bank::teams,
            )
            result.teams shouldHaveSize bank.teams.size
        }
    }
}
