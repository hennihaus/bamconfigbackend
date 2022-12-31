package de.hennihaus.services

import de.hennihaus.bamdatamodel.Bank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getAsyncBank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getSchufaBank
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.getSyncBank
import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.repositories.BankRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.beInstanceOf
import io.ktor.server.plugins.NotFoundException
import io.mockk.Called
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
    private val task = mockk<TaskService>()
    private val github = mockk<GithubService>()

    private val classUnderTest = BankService(
        repository = repository,
        task = task,
        github = github,
    )

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class GetAllBanks {
        @Test
        fun `should return a list of banks sorted by isAsync true to false`() = runBlocking {
            coEvery { repository.getAll() } returns listOf(
                getAsyncBank(),
                getSyncBank(),
                getSchufaBank(),
            )

            val response: List<Bank> = classUnderTest.getAllBanks()

            response.shouldContainExactly(
                getSchufaBank(),
                getSyncBank(),
                getAsyncBank(),
            )
            coVerifySequence {
                repository.getAll()
            }
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
    inner class GetBankById {
        @Test
        fun `should return bank when id is in database`() = runBlocking {
            val id = "${getAsyncBank().uuid}"
            coEvery { repository.getById(id = any()) } returns getAsyncBank()

            val result: Bank = classUnderTest.getBankById(id = id)

            result shouldBe getAsyncBank()
            coVerifySequence {
                repository.getById(id = UUID.fromString(id))
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
        }
    }

    @Nested
    inner class PatchBank {
        @Test
        fun `should just update special fields in db when bank has no creditConfiguration`() = runBlocking {
            val testBank = getAsyncBank(
                thumbnailUrl = getSchufaBank().thumbnailUrl,
                isActive = getSchufaBank().isActive,
                creditConfiguration = getSchufaBank().creditConfiguration,
            )
            coEvery { repository.getById(id = any()) } returns getSchufaBank(
                thumbnailUrl = getAsyncBank().thumbnailUrl,
                isActive = getAsyncBank().isActive,
                creditConfiguration = getAsyncBank().creditConfiguration,
            )
            coEvery { repository.save(entry = any(), repetitionAttempts = any()) } returns getSchufaBank()

            val result: Bank = classUnderTest.patchBank(
                id = "${testBank.uuid}",
                bank = testBank,
            )

            result shouldBe getSchufaBank()
            coVerifySequence {
                repository.getById(id = testBank.uuid)
                repository.save(entry = getSchufaBank(), repetitionAttempts = ONE_REPETITION_ATTEMPT)
            }
            coVerify { listOf(task, github) wasNot Called }
        }

        @Test
        fun `should just update special fields in db, parameter and api when bank is sync`() = runBlocking {
            val testBank = getAsyncBank(
                thumbnailUrl = getSyncBank().thumbnailUrl,
                isActive = getSyncBank().isActive,
                creditConfiguration = getSyncBank().creditConfiguration,
            )
            coEvery { repository.getById(id = any()) } returns getSyncBank(
                thumbnailUrl = getAsyncBank().thumbnailUrl,
                isActive = getAsyncBank().isActive,
                creditConfiguration = getAsyncBank().creditConfiguration,
            )
            coEvery { repository.save(entry = any(), repetitionAttempts = any()) } returns getSyncBank()
            coEvery {
                task.patchParameters(
                    minAmountInEuros = any(),
                    maxAmountInEuros = any(),
                    minTermInMonths = any(),
                    maxTermInMonths = any()
                )
            } returns Unit
            coEvery { github.updateOpenApi(creditConfiguration = any()) } returns Unit

            val result: Bank = classUnderTest.patchBank(
                id = "${testBank.uuid}",
                bank = testBank,
            )

            result shouldBe getSyncBank()
            coVerifySequence {
                repository.getById(id = testBank.uuid)
                repository.save(
                    entry = getSyncBank(),
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
                task.patchParameters(
                    minAmountInEuros = getSyncBank().creditConfiguration!!.minAmountInEuros,
                    maxAmountInEuros = getSyncBank().creditConfiguration!!.maxAmountInEuros,
                    minTermInMonths = getSyncBank().creditConfiguration!!.minTermInMonths,
                    maxTermInMonths = getSyncBank().creditConfiguration!!.maxTermInMonths,
                )
                github.updateOpenApi(creditConfiguration = getSyncBank().creditConfiguration!!)
            }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val testBank: Bank = getSchufaBank(
                thumbnailUrl = getSyncBank().thumbnailUrl,
                isActive = getSyncBank().isActive,
                creditConfiguration = getSyncBank().creditConfiguration,
            )
            coEvery { repository.getById(id = any()) } returns null

            val result = shouldThrowExactly<NotFoundException> {
                classUnderTest.patchBank(id = "${testBank.uuid}", bank = testBank)
            }

            result shouldHaveMessage BankService.BANK_NOT_FOUND_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = testBank.uuid) }
            coVerify(exactly = 0) {
                repository.save(
                    entry = any(),
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }
        }
    }

    @Nested
    inner class HasName {
        @Test
        fun `should return true when name is already in database`() = runBlocking {
            val (_, name) = getSchufaBank()
            coEvery { repository.getBankIdByName(name = any()) } returns getSchufaBank().uuid

            val result: Boolean = classUnderTest.hasName(name = name)

            result.shouldBeTrue()
            coVerify(exactly = 1) { repository.getBankIdByName(name = name) }
        }

        @Test
        fun `should return false when name is not in database`() = runBlocking {
            val (_, name) = getSchufaBank()
            coEvery { repository.getBankIdByName(name = any()) } returns null

            val result: Boolean = classUnderTest.hasName(name = name)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getBankIdByName(name = name) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (_, name) = getSchufaBank()
            coEvery { repository.getBankIdByName(name = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.hasName(name = name)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getBankIdByName(name = name) }
        }
    }
}
