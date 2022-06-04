package de.hennihaus.services

import de.hennihaus.models.Group
import de.hennihaus.objectmothers.BankObjectMother.getJmsBank
import de.hennihaus.objectmothers.BankObjectMother.getSchufaBank
import de.hennihaus.objectmothers.BankObjectMother.getVBank
import de.hennihaus.objectmothers.GroupObjectMother.getFirstGroup
import de.hennihaus.repositories.BankRepository
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StatsServiceTest {

    private val repository = mockk<BankRepository>()
    private val classUnderTest = StatsServiceImpl(repository = repository)

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class SetHasPassed {

        @BeforeEach
        fun init() {
            coEvery { repository.getAll() } returns listOf(
                getSchufaBank(),
                getVBank(),
                getJmsBank()
            )
        }

        @Test
        fun `should set hasPassed = false when group has zero request stats`() = runBlocking {
            val group = getFirstGroup(
                stats = mapOf(
                    getSchufaBank().jmsQueue to 0,
                    getVBank().jmsQueue to 0,
                    getJmsBank().jmsQueue to 0
                ),
            )

            val result: Group = classUnderTest.setHasPassed(group = group)

            result shouldBe group
            result.hasPassed.shouldBeFalse()
        }

        @Test
        fun `should set hasPassed = false when group has one async bank with zero request stats`() = runBlocking {
            val group = getFirstGroup(
                stats = mapOf(
                    getSchufaBank().jmsQueue to 1,
                    getVBank().jmsQueue to 1,
                    getJmsBank().jmsQueue to 0
                )
            )

            val result: Group = classUnderTest.setHasPassed(group = group)

            result shouldBe group
            result.hasPassed.shouldBeFalse()
        }

        @Test
        fun `should set hasPassed = true when group has one request for every bank`() = runBlocking {
            val group = getFirstGroup(
                stats = mapOf(
                    getSchufaBank().jmsQueue to 1,
                    getVBank().jmsQueue to 1,
                    getJmsBank().jmsQueue to 1
                )
            )

            val result: Group = classUnderTest.setHasPassed(group = group)

            result shouldBe group.copy(hasPassed = true)
        }

        @Test
        @Suppress("MaxLineLength")
        fun `should set hasPassed = true when group has one request for every bank and bank with zero requests zero groups`() =
            runBlocking {
                coEvery { repository.getAll() } returns listOf(
                    getSchufaBank(),
                    getVBank(),
                    getJmsBank(groups = emptyList())
                )
                val group = getFirstGroup(
                    stats = mapOf(
                        getSchufaBank().jmsQueue to 1,
                        getVBank().jmsQueue to 1,
                        getJmsBank().jmsQueue to 0
                    )
                )

                val result: Group = classUnderTest.setHasPassed(group = group)

                result shouldBe group.copy(hasPassed = true)
            }

        @Test
        @Suppress("MaxLineLength")
        fun `should set hasPassed = true when group has one request for all sync banks and all async banks are deactivated`() =
            runBlocking {
                coEvery { repository.getAll() } returns listOf(
                    getSchufaBank(),
                    getVBank(),
                    getJmsBank(isActive = false)
                )
                val group = getFirstGroup(
                    stats = mapOf(
                        getSchufaBank().jmsQueue to 1,
                        getVBank().jmsQueue to 1,
                        getJmsBank().jmsQueue to 0
                    )
                )

                val result: Group = classUnderTest.setHasPassed(group = group)

                result shouldBe group.copy(hasPassed = true)
            }

        @Test
        fun `should set hasPassed = false when group has zero requests for one sync banks`() = runBlocking {
            val group = getFirstGroup(
                stats = mapOf(
                    getSchufaBank().jmsQueue to 0,
                    getVBank().jmsQueue to 1,
                    getJmsBank().jmsQueue to 1
                )
            )

            val result: Group = classUnderTest.setHasPassed(group = group)

            result shouldBe group
            result.hasPassed.shouldBeFalse()
        }
    }
}
