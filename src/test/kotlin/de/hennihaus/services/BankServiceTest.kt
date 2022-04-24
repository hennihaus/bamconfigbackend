package de.hennihaus.services

import de.hennihaus.models.Bank
import de.hennihaus.objectmothers.BankObjectMother.getJmsBank
import de.hennihaus.objectmothers.BankObjectMother.getSchufaBank
import de.hennihaus.objectmothers.BankObjectMother.getVBank
import de.hennihaus.plugins.NotFoundException
import de.hennihaus.repositories.BankRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.instanceOf
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BankServiceTest {

    private val repository = mockk<BankRepository>()
    private val classUnderTest = BankServiceImpl(repository = repository)

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class GetAllBanks {
        @Test
        fun `should return a list of banks`() = runBlocking {
            coEvery { repository.getAll() } returns listOf(
                getSchufaBank(),
                getVBank(),
                getJmsBank()
            )

            val response: List<Bank> = classUnderTest.getAllBanks()

            response.shouldContainExactly(
                getSchufaBank(),
                getVBank(),
                getJmsBank()
            )
            coVerify(exactly = 1) { repository.getAll() }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { repository.getAll() } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.getAllBanks() }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getAll() }
        }
    }

    @Nested
    inner class GetBankByJmsTopic {
        @Test
        fun `should return bank when jmsTopic is in database`() = runBlocking {
            val jmsTopic = getSchufaBank().jmsTopic
            coEvery { repository.getById(id = any()) } returns getSchufaBank()

            val result: Bank = classUnderTest.getBankByJmsTopic(jmsTopic = jmsTopic)

            result shouldBe getSchufaBank()
            coVerify(exactly = 1) { repository.getById(id = withArg { it shouldBe jmsTopic }) }
        }

        @Test
        fun `should throw an exception when jmsTopic is not in database`() = runBlocking {
            val jmsTopic = "unknown"
            coEvery { repository.getById(id = any()) } returns null

            val result = shouldThrow<NotFoundException> { classUnderTest.getBankByJmsTopic(jmsTopic = jmsTopic) }

            result shouldBe instanceOf<NotFoundException>()
            result.message shouldBe BankServiceImpl.ID_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = withArg { it shouldBe jmsTopic }) }
        }
    }

    @Nested
    inner class UpdateBank {
        @Test
        fun `should return and update a bank`() = runBlocking {
            val testBank: Bank = getSchufaBank()
            coEvery { repository.save(entry = any()) } returns testBank

            val result: Bank = classUnderTest.updateBank(bank = testBank)

            result shouldBe testBank
            coVerify(exactly = 1) { repository.save(entry = withArg { it shouldBe testBank }) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val testBank: Bank = getSchufaBank()
            coEvery { repository.save(entry = any()) } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.updateBank(bank = testBank) }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.save(entry = withArg { it shouldBe testBank }) }
        }
    }

    @Nested
    inner class UpdateAllBanks {
        @Test
        fun `should return and update all banks`() = runBlocking {
            val testBanks = listOf(getSchufaBank(), getVBank(), getJmsBank())
            coEvery { repository.save(entry = any()) } returns getSchufaBank() andThen getVBank() andThen getJmsBank()

            val result: List<Bank> = classUnderTest.updateAllBanks(banks = testBanks)

            result.shouldContainExactly(expected = testBanks)
            coVerifySequence {
                repository.save(entry = withArg { it shouldBe getSchufaBank() })
                repository.save(entry = withArg { it shouldBe getVBank() })
                repository.save(entry = withArg { it shouldBe getJmsBank() })
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val testBanks = listOf(getSchufaBank(), getVBank(), getJmsBank())
            coEvery { repository.save(entry = any()) } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.updateAllBanks(banks = testBanks) }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) {
                repository.save(entry = withArg { it shouldBe getSchufaBank() })
            }
        }
    }
}
