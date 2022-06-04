package de.hennihaus.services

import de.hennihaus.models.Group
import de.hennihaus.objectmothers.GroupObjectMother.NON_ZERO_STATS
import de.hennihaus.objectmothers.GroupObjectMother.ZERO_STATS
import de.hennihaus.objectmothers.GroupObjectMother.getFirstGroup
import de.hennihaus.objectmothers.GroupObjectMother.getSecondGroup
import de.hennihaus.objectmothers.GroupObjectMother.getThirdGroup
import de.hennihaus.plugins.NotFoundException
import de.hennihaus.repositories.GroupRepository
import de.hennihaus.services.GroupServiceImpl.Companion.ID_MESSAGE
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.types.beInstanceOf
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class GroupServiceTest {

    private val repository = mockk<GroupRepository>()
    private val stats = mockk<StatsServiceImpl>()
    private val passwordLength = "10"
    private val classUnderTest = GroupServiceImpl(
        repository = repository,
        stats = stats,
        passwordLength = passwordLength
    )

    @BeforeEach
    fun init() {
        clearAllMocks()
        coEvery { stats.setHasPassed(group = any()) }
            .returns(returnValue = getFirstGroup())
            .andThen(returnValue = getSecondGroup())
            .andThen(returnValue = getThirdGroup())
    }

    @Nested
    inner class GetAllGroups {
        @Test
        fun `should return a list of groups sorted by username asc`() = runBlocking {
            coEvery { repository.getAll() } returns listOf(
                getSecondGroup(),
                getThirdGroup(),
                getFirstGroup()
            )

            val response: List<Group> = classUnderTest.getAllGroups()

            response.shouldContainExactly(
                getFirstGroup(),
                getSecondGroup(),
                getThirdGroup()
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

            val result = shouldThrow<Exception> { classUnderTest.getAllGroups() }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getAll() }
            coVerify(exactly = 0) { stats.setHasPassed(group = any()) }
        }
    }

    @Nested
    inner class GroupById {
        @Test
        fun `should return group when id is in database`() = runBlocking {
            val id = getFirstGroup().id.toString()
            coEvery { repository.getById(id = any()) } returns getFirstGroup()

            val result: Group = classUnderTest.getGroupById(id = id)

            result shouldBe getFirstGroup()
            coVerifySequence {
                repository.getById(id = ObjectId(id))
                stats.setHasPassed(group = getFirstGroup())
            }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = getFirstGroup().id.toString()
            coEvery { repository.getById(id = any()) } returns null

            val result = shouldThrow<NotFoundException> { classUnderTest.getGroupById(id = id) }

            result should beInstanceOf<NotFoundException>()
            result.message shouldBe ID_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = withArg { it shouldBe ObjectId(id) }) }
            coVerify(exactly = 0) { stats.setHasPassed(group = any()) }
        }
    }

    @Nested
    inner class CheckUsername {
        @Test
        fun `should return true when username is already in db and ids are different`() = runBlocking {
            val (id, username) = getFirstGroup()
            coEvery { repository.getGroupByUsername(username = username) } returns getSecondGroup()

            val result: Boolean = classUnderTest.checkUsername(id = id.toString(), username = username)

            result.shouldBeTrue()
            coVerify(exactly = 1) { repository.getGroupByUsername(username = withArg { it shouldBe username }) }
        }

        @Test
        fun `should return false when username is in database and ids are equal`() = runBlocking {
            val (id, username) = getFirstGroup()
            coEvery { repository.getGroupByUsername(username = any()) } returns getFirstGroup()

            val result: Boolean = classUnderTest.checkUsername(id = id.toString(), username = username)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getGroupByUsername(username = withArg { it shouldBe username }) }
        }

        @Test
        fun `should return false when username is not in database`() = runBlocking {
            val (id, username) = getFirstGroup()
            coEvery { repository.getGroupByUsername(username = any()) } returns null

            val result: Boolean = classUnderTest.checkUsername(id = id.toString(), username = username)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getGroupByUsername(username = withArg { it shouldBe username }) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, username) = getFirstGroup()
            coEvery { repository.getGroupByUsername(username = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.checkUsername(id = id.toString(), username = username)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getGroupByUsername(username = withArg { it shouldBe username }) }
        }
    }

    @Nested
    inner class CheckPassword {
        @Test
        fun `should return true when password is already in db and ids are different`() = runBlocking {
            val (id, _, password) = getFirstGroup()
            coEvery { repository.getGroupByPassword(password = password) } returns getSecondGroup()

            val result: Boolean = classUnderTest.checkPassword(id = id.toString(), password = password)

            result.shouldBeTrue()
            coVerify(exactly = 1) { repository.getGroupByPassword(password = withArg { it shouldBe password }) }
        }

        @Test
        fun `should return false when password is in database and ids are equal`() = runBlocking {
            val (id, _, password) = getFirstGroup()
            coEvery { repository.getGroupByPassword(password = any()) } returns getFirstGroup()

            val result: Boolean = classUnderTest.checkPassword(id = id.toString(), password = password)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getGroupByPassword(password = withArg { it shouldBe password }) }
        }

        @Test
        fun `should return false when password is not in database`() = runBlocking {
            val (id, _, password) = getFirstGroup()
            coEvery { repository.getGroupByPassword(password = any()) } returns null

            val result: Boolean = classUnderTest.checkPassword(id = id.toString(), password = password)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getGroupByPassword(password = withArg { it shouldBe password }) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, _, password) = getFirstGroup()
            coEvery { repository.getGroupByPassword(password = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.checkPassword(id = id.toString(), password = password)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getGroupByPassword(password = withArg { it shouldBe password }) }
        }
    }

    @Nested
    inner class CheckJmsQueue {
        @Test
        fun `should return true when jmsQueue is already in db and ids are different`() = runBlocking {
            val (id, _, _, jmsQueue) = getFirstGroup()
            coEvery { repository.getGroupByJmsQueue(jmsQueue = jmsQueue) } returns getSecondGroup()

            val result: Boolean = classUnderTest.checkJmsQueue(id = id.toString(), jmsQueue = jmsQueue)

            result.shouldBeTrue()
            coVerify(exactly = 1) { repository.getGroupByJmsQueue(jmsQueue = withArg { it shouldBe jmsQueue }) }
        }

        @Test
        fun `should return false when jmsQueue is in database and ids are equal`() = runBlocking {
            val (id, _, _, jmsQueue) = getFirstGroup()
            coEvery { repository.getGroupByJmsQueue(jmsQueue = any()) } returns getFirstGroup()

            val result: Boolean = classUnderTest.checkJmsQueue(id = id.toString(), jmsQueue = jmsQueue)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getGroupByJmsQueue(jmsQueue = withArg { it shouldBe jmsQueue }) }
        }

        @Test
        fun `should return false when jmsQueue is not in database`() = runBlocking {
            val (id, _, _, jmsQueue) = getFirstGroup()
            coEvery { repository.getGroupByJmsQueue(jmsQueue = any()) } returns null

            val result: Boolean = classUnderTest.checkJmsQueue(id = id.toString(), jmsQueue = jmsQueue)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getGroupByJmsQueue(jmsQueue = withArg { it shouldBe jmsQueue }) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, _, _, jmsQueue) = getFirstGroup()
            coEvery { repository.getGroupByJmsQueue(jmsQueue = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.checkJmsQueue(id = id.toString(), jmsQueue = jmsQueue)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getGroupByJmsQueue(jmsQueue = withArg { it shouldBe jmsQueue }) }
        }
    }

    @Nested
    inner class SaveGroup {
        @Test
        fun `should return and save a group`() = runBlocking {
            val testGroup: Group = getFirstGroup()
            coEvery { repository.save(entry = any()) } returns testGroup

            val result: Group = classUnderTest.saveGroup(group = testGroup)

            result shouldBe testGroup
            coVerifySequence {
                stats.setHasPassed(group = getFirstGroup())
                repository.save(entry = getFirstGroup())
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val testGroup: Group = getFirstGroup()
            coEvery { stats.setHasPassed(group = any()) } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.saveGroup(group = testGroup) }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { stats.setHasPassed(group = getFirstGroup()) }
            coVerify(exactly = 0) { repository.save(entry = any()) }
        }
    }

    @Nested
    inner class DeleteGroupById {
        @Test
        fun `should delete an group by id`() = runBlocking {
            val id = getFirstGroup().id.toString()
            coEvery { repository.deleteById(id = any()) } returns true

            classUnderTest.deleteGroupById(id = id)

            coVerify(exactly = 1) { repository.deleteById(id = withArg { it shouldBe ObjectId(id) }) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val id = getFirstGroup().id.toString()
            coEvery { repository.deleteById(id = any()) } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.deleteGroupById(id = id) }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.deleteById(id = withArg { it shouldBe ObjectId(id) }) }
        }
    }

    @Nested
    inner class ResetAllGroups {
        @Test
        fun `should reset all groups stats, hasPassed and passwords`() = runBlocking {
            coEvery { repository.getAll() } returns listOf(
                getFirstGroup(stats = NON_ZERO_STATS, hasPassed = true),
                getSecondGroup(stats = ZERO_STATS, hasPassed = false),
            )
            coEvery { repository.save(entry = any()) } returns getFirstGroup() andThen getSecondGroup()

            val result: List<Group> = classUnderTest.resetAllGroups()

            result shouldBe listOf(
                getFirstGroup(),
                getSecondGroup()
            )
            coVerifySequence {
                repository.getAll()
                repository.save(
                    entry = withArg {
                        it.shouldBeEqualToIgnoringFields(
                            other = getFirstGroup(stats = ZERO_STATS, hasPassed = false),
                            property = Group::password
                        )
                        it.password shouldMatch Regex(pattern = "[a-zA-Z]{10}")
                    }
                )
                repository.save(
                    entry = withArg {
                        it.shouldBeEqualToIgnoringFields(
                            other = getSecondGroup(stats = ZERO_STATS, hasPassed = false),
                            property = Group::password
                        )
                        it.password shouldMatch Regex(pattern = "[a-zA-Z]{10}")
                    }
                )
            }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { repository.getAll() } returns listOf(getFirstGroup())
            coEvery { repository.save(entry = any()) } throws NotFoundException(message = ID_MESSAGE)

            val result = shouldThrow<NotFoundException> { classUnderTest.resetAllGroups() }

            result should beInstanceOf<NotFoundException>()
            coVerifySequence {
                repository.getAll()
                repository.save(
                    entry = withArg {
                        it.shouldBeEqualToIgnoringFields(
                            other = getFirstGroup(),
                            property = Group::password
                        )
                        it.password shouldMatch Regex(pattern = "[a-zA-Z]{10}")
                    }
                )
            }
        }
    }

    @Nested
    inner class ResetStats {
        @Test
        fun `should return group and reset all stats for a group id`() = runBlocking {
            val id = getFirstGroup().id.toString()
            coEvery { repository.getById(id = any()) } returns getFirstGroup(
                stats = mapOf(
                    "schufa" to 0,
                    "vbank" to 1,
                    "jmsBankA" to 2
                )
            )
            coEvery { repository.save(entry = any()) } returns getFirstGroup(stats = ZERO_STATS)

            val result: Group = classUnderTest.resetStats(id)

            result shouldBe getFirstGroup(stats = ZERO_STATS)
            coVerifySequence {
                repository.getById(id = ObjectId(id))
                stats.setHasPassed(group = getFirstGroup(stats = ZERO_STATS))
                repository.save(entry = getFirstGroup(stats = ZERO_STATS))
            }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = getFirstGroup().id.toString()
            coEvery { repository.getById(id = any()) } returns null

            val result = shouldThrow<NotFoundException> { classUnderTest.resetStats(id = id) }

            result should beInstanceOf<NotFoundException>()
            result.message shouldBe ID_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = ObjectId(id)) }
            coVerify(exactly = 0) { stats.setHasPassed(group = any()) }
            coVerify(exactly = 0) { repository.save(entry = any()) }
        }
    }
}
