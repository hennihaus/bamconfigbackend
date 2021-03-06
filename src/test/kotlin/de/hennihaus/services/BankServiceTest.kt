package de.hennihaus.services

import de.hennihaus.models.Bank
import de.hennihaus.objectmothers.BankObjectMother.getJmsBank
import de.hennihaus.objectmothers.BankObjectMother.getSchufaBank
import de.hennihaus.objectmothers.BankObjectMother.getVBank
import de.hennihaus.objectmothers.GroupObjectMother.getFirstGroup
import de.hennihaus.objectmothers.GroupObjectMother.getSecondGroup
import de.hennihaus.objectmothers.GroupObjectMother.getThirdGroup
import de.hennihaus.repositories.BankRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.instanceOf
import io.ktor.server.plugins.NotFoundException
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
    private val stats = mockk<StatsServiceImpl>()
    private val classUnderTest = BankServiceImpl(repository = repository, stats = stats)

    @BeforeEach
    fun init() {
        clearAllMocks()
        coEvery { stats.setHasPassed(group = any()) }
            .returns(returnValue = getFirstGroup())
            .andThen(returnValue = getSecondGroup())
            .andThen(returnValue = getThirdGroup())
    }

    @Nested
    inner class GetAllBanks {
        @Test
        fun `should return a list of banks`() = runBlocking {
            coEvery { repository.getAll() } returns listOf(
                getSchufaBank(),
                getVBank(),
                getJmsBank(),
            )

            val response: List<Bank> = classUnderTest.getAllBanks()

            response.shouldContainExactly(
                getSchufaBank(),
                getVBank(),
                getJmsBank(),
            )
            coVerifySequence {
                repository.getAll()
                stats.setHasPassed(group = getFirstGroup())
                stats.setHasPassed(group = getSecondGroup())
                stats.setHasPassed(group = getThirdGroup())
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { repository.getAll() } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.getAllBanks() }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getAll() }
            coVerify(exactly = 0) { stats.setHasPassed(group = any()) }
        }
    }

    @Nested
    inner class GetBankByJmsQueue {
        @Test
        fun `should return bank when jmsQueue is in database`() = runBlocking {
            val jmsQueue = getJmsBank().jmsQueue
            coEvery { repository.getById(id = any()) } returns getJmsBank()

            val result: Bank = classUnderTest.getBankByJmsQueue(jmsQueue = jmsQueue)

            result shouldBe getJmsBank()
            coVerifySequence {
                repository.getById(id = jmsQueue)
                stats.setHasPassed(group = getFirstGroup())
                stats.setHasPassed(group = getSecondGroup())
                stats.setHasPassed(group = getThirdGroup())
            }
        }

        @Test
        fun `should throw an exception when jmsQueue is not in database`() = runBlocking {
            val jmsQueue = "unknown"
            coEvery { repository.getById(id = any()) } returns null

            val result = shouldThrow<NotFoundException> { classUnderTest.getBankByJmsQueue(jmsQueue = jmsQueue) }

            result shouldBe instanceOf<NotFoundException>()
            result.message shouldBe BankServiceImpl.BANK_NOT_FOUND_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = jmsQueue) }
            coVerify(exactly = 0) { stats.setHasPassed(group = any()) }
        }
    }

    @Nested
    inner class SaveBank {
        @Test
        fun `should return and save a bank`() = runBlocking {
            val testBank: Bank = getSchufaBank()
            coEvery { repository.save(entry = any()) } returns testBank

            val result: Bank = classUnderTest.saveBank(bank = testBank)

            result shouldBe testBank
            coVerify(exactly = 1) { repository.save(entry = withArg { it shouldBe testBank }) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val testBank: Bank = getSchufaBank()
            coEvery { repository.save(entry = any()) } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.saveBank(bank = testBank) }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.save(entry = withArg { it shouldBe testBank }) }
        }
    }

    @Nested
    inner class SaveAllBanks {
        @Test
        fun `should return and save all banks`() = runBlocking {
            val testBanks = listOf(getSchufaBank(), getVBank(), getJmsBank())
            coEvery { repository.save(entry = any()) } returns getSchufaBank() andThen getVBank() andThen getJmsBank()

            val result: List<Bank> = classUnderTest.saveAllBanks(banks = testBanks)

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

            val result = shouldThrow<Exception> { classUnderTest.saveAllBanks(banks = testBanks) }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) {
                repository.save(entry = withArg { it shouldBe getSchufaBank() })
            }
        }
    }
}
