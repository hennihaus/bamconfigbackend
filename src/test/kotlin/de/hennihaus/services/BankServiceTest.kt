package de.hennihaus.services

import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.models.generated.Bank
import de.hennihaus.objectmothers.BankObjectMother.getAsyncBank
import de.hennihaus.objectmothers.BankObjectMother.getSchufaBank
import de.hennihaus.objectmothers.BankObjectMother.getSyncBank
import de.hennihaus.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.objectmothers.TeamObjectMother.getSecondTeam
import de.hennihaus.objectmothers.TeamObjectMother.getThirdTeam
import de.hennihaus.repositories.BankRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.beInstanceOf
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
import java.util.UUID

class BankServiceTest {

    private val repository = mockk<BankRepository>()
    private val statistic = mockk<StatisticService>()

    private val classUnderTest = BankService(
        repository = repository,
        statistic = statistic,
    )

    @BeforeEach
    fun init() {
        clearAllMocks()
        coEvery { statistic.setHasPassed(team = any()) }
            .returns(returnValue = getFirstTeam())
            .andThen(returnValue = getSecondTeam())
            .andThen(returnValue = getThirdTeam())
            .andThen(returnValue = getFirstTeam())
            .andThen(returnValue = getSecondTeam())
            .andThen(returnValue = getThirdTeam())
            .andThen(returnValue = getFirstTeam())
            .andThen(returnValue = getSecondTeam())
            .andThen(returnValue = getThirdTeam())
    }

    @Nested
    inner class GetAllBanks {
        @Test
        fun `should return a list of banks`() = runBlocking {
            coEvery { repository.getAll() } returns listOf(
                getSchufaBank(),
                getSyncBank(),
                getAsyncBank(),
            )

            val response: List<Bank> = classUnderTest.getAllBanks()

            response.shouldContainExactly(
                getSchufaBank(),
                getSyncBank(),
                getAsyncBank(),
            )
            coVerifySequence {
                repository.getAll()
                statistic.setHasPassed(team = getFirstTeam())
                statistic.setHasPassed(team = getSecondTeam())
                statistic.setHasPassed(team = getThirdTeam())
                statistic.setHasPassed(team = getFirstTeam())
                statistic.setHasPassed(team = getSecondTeam())
                statistic.setHasPassed(team = getThirdTeam())
                statistic.setHasPassed(team = getFirstTeam())
                statistic.setHasPassed(team = getSecondTeam())
                statistic.setHasPassed(team = getThirdTeam())
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { repository.getAll() } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.getAllBanks() }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getAll() }
            coVerify(exactly = 0) { statistic.setHasPassed(team = any()) }
        }
    }

    @Nested
    inner class GetBankById {
        @Test
        fun `should return bank when id is in database`() = runBlocking {
            val id = "${getAsyncBank().uuid}"
            coEvery { repository.getById(id = any()) } returns getAsyncBank()

            val result: Bank = classUnderTest.getBankById(id = id)

            result shouldBe getAsyncBank()
            coVerifySequence {
                repository.getById(id = UUID.fromString(id))
                statistic.setHasPassed(team = getFirstTeam())
                statistic.setHasPassed(team = getSecondTeam())
                statistic.setHasPassed(team = getThirdTeam())
            }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = "${UUID.randomUUID()}"
            coEvery { repository.getById(id = any()) } returns null

            val result = shouldThrowExactly<NotFoundException> {
                classUnderTest.getBankById(id = id)
            }

            result shouldHaveMessage BankService.BANK_NOT_FOUND_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = UUID.fromString(id)) }
            coVerify(exactly = 0) { statistic.setHasPassed(team = any()) }
        }
    }

    @Nested
    inner class SaveBank {
        @Test
        fun `should return and save a bank`() = runBlocking {
            val testBank: Bank = getSchufaBank()
            coEvery { repository.save(entry = any(), repetitionAttempts = any()) } returns testBank

            val result: Bank = classUnderTest.saveBank(bank = testBank)

            result shouldBe testBank
            coVerify(exactly = 1) {
                repository.save(
                    entry = testBank,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val testBank: Bank = getSchufaBank()
            coEvery { repository.save(entry = any()) } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.saveBank(bank = testBank) }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) {
                repository.save(
                    entry = testBank,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
        }
    }
}
