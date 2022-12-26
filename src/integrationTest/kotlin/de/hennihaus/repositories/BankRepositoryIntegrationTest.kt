package de.hennihaus.repositories

import de.hennihaus.bamdatamodel.Bank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getAsyncBank
import de.hennihaus.bamdatamodel.objectmothers.CreditConfigurationObjectMother.getCreditConfigurationWithNoEmptyFields
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_HOST
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_PORT
import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.objectmothers.ExposedContainerObjectMother
import de.hennihaus.plugins.initKoin
import de.hennihaus.testutils.containers.ExposedContainer
import io.kotest.extensions.time.withConstantNow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

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
        fun `should return at a minimum one bank`() = runBlocking<Unit> {
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
        fun `should save an existing bank with creditConfiguration`() = runBlocking {
            val bank = getAsyncBank(
                uuid = ExposedContainerObjectMother.BANK_UUID,
                name = ExposedContainerObjectMother.BANK_NAME,
                jmsQueue = "NewJmsName",
                creditConfiguration = getCreditConfigurationWithNoEmptyFields(
                    minAmountInEuros = 0,
                ),
            )

            val result = withConstantNow(now = OffsetDateTime.of(bank.updatedAt, ZoneOffset.UTC)) {
                classUnderTest.save(
                    entry = bank,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }

            result shouldBe bank
        }

        @Test
        fun `should save an existing bank without creditConfiguration`() = runBlocking {
            val bank = getAsyncBank(
                uuid = ExposedContainerObjectMother.BANK_UUID,
                name = ExposedContainerObjectMother.BANK_NAME,
                jmsQueue = "NewJmsName",
                creditConfiguration = null,
            )

            val result: Bank = withConstantNow(now = OffsetDateTime.of(bank.updatedAt, ZoneOffset.UTC)) {
                classUnderTest.save(
                    entry = bank,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }

            result shouldBe bank
        }
    }

    @Nested
    inner class GetBankIdByName {
        @Test
        fun `should return a bank uuid when bank is found by name`() = runBlocking<Unit> {
            val name = ExposedContainerObjectMother.BANK_NAME

            val result: UUID? = classUnderTest.getBankIdByName(name = name)

            result.shouldNotBeNull()
        }

        @Test
        fun `should return null when bank is not found by name`() = runBlocking {
            val name = "unknown"

            val result: UUID? = classUnderTest.getBankIdByName(name = name)

            result.shouldBeNull()
        }
    }
}
