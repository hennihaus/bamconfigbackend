package de.hennihaus.repositories

import de.hennihaus.configurations.MongoConfiguration
import de.hennihaus.containers.MongoContainer
import de.hennihaus.models.Bank
import de.hennihaus.objectmothers.BankObjectMother.getJmsBank
import de.hennihaus.objectmothers.MongoContainerObjectMother
import de.hennihaus.plugins.initKoin
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
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

    private val mongoContainer = MongoContainer.INSTANCE
    private val classUnderTest: BankRepository by inject()

    @JvmField
    @RegisterExtension
    @Suppress("unused")
    val koinTestExtension = KoinTestExtension.create {
        initKoin(
            properties = mapOf(
                MongoConfiguration.DATABASE_HOST to mongoContainer.host,
                MongoConfiguration.DATABASE_PORT to mongoContainer.firstMappedPort.toString(),
            )
        )
    }

    @BeforeEach
    fun init() = MongoContainer.resetState()

    @AfterAll
    fun cleanUp() = stopKoin()

    @Nested
    inner class GetById {
        @Test
        fun `should find a bank by jmsQueue`() = runBlocking<Unit> {
            val jmsQueue = MongoContainerObjectMother.BANK_JMS_QUEUE

            val result: Bank? = classUnderTest.getById(id = jmsQueue)

            result should beInstanceOf<Bank>()
            result!!.groups.size shouldBeGreaterThanOrEqual 1
        }

        @Test
        fun `should return null when jmsQueue is not in db`() = runBlocking {
            val jmsQueue = "unknown"

            val result: Bank? = classUnderTest.getById(id = jmsQueue)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class GetAll {
        @Test
        fun `should return at least one bank`() = runBlocking<Unit> {
            val result: List<Bank> = classUnderTest.getAll()

            result.size shouldBeGreaterThanOrEqual 1
            result.find { it.isAsync }!!.groups.size shouldBeGreaterThanOrEqual 1
        }
    }

    @Nested
    inner class Save {
        @Test
        fun `should save an existing bank`() = runBlocking {
            val bank = getJmsBank(
                jmsQueue = MongoContainerObjectMother.BANK_JMS_QUEUE,
                name = "NewBankName",
                groups = listOf(MongoContainerObjectMother.getFirstGroup())
            )

            val result: Bank = classUnderTest.save(entry = bank)

            result shouldBe bank
            classUnderTest.getById(id = bank.jmsQueue) shouldBe bank
        }

        @Test
        fun `should save a bank when no existing bank is in db`() = runBlocking {
            val bank = getJmsBank(
                jmsQueue = "newBank",
                groups = listOf(MongoContainerObjectMother.getFirstGroup())
            )

            val result: Bank = classUnderTest.save(entry = bank)

            result shouldBe bank
            classUnderTest.getById(id = bank.jmsQueue) shouldBe bank
        }
    }

    @Nested
    inner class DeleteById {
        @Test
        fun `should return true when one bank was deleted by jmsQueue`() = runBlocking {
            val jmsQueue = MongoContainerObjectMother.BANK_JMS_QUEUE

            val result: Boolean = classUnderTest.deleteById(id = jmsQueue)

            result.shouldBeTrue()
        }

        @Test
        fun `should return false when no bank was deleted by jmsQueue`() = runBlocking {
            val jmsQueue = "unknown"

            val result: Boolean = classUnderTest.deleteById(id = jmsQueue)

            result.shouldBeFalse()
        }
    }
}
