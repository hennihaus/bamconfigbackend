package de.hennihaus.services

import de.hennihaus.models.Group
import de.hennihaus.objectmothers.GroupObjectMother.ZERO_STATS
import de.hennihaus.objectmothers.GroupObjectMother.getFirstGroup
import de.hennihaus.objectmothers.GroupObjectMother.getSecondGroup
import de.hennihaus.objectmothers.GroupObjectMother.getThirdGroup
import de.hennihaus.plugins.NotFoundException
import de.hennihaus.repositories.GroupRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
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
    private val classUnderTest = GroupServiceImpl(repository = repository)

    @BeforeEach
    fun init() = clearAllMocks()

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
            coVerify(exactly = 1) { repository.getAll() }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { repository.getAll() } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.getAllGroups() }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getAll() }
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
            coVerify(exactly = 1) { repository.getById(id = withArg { it shouldBe ObjectId(id) }) }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = getFirstGroup().id.toString()
            coEvery { repository.getById(id = any()) } returns null

            val result = shouldThrow<NotFoundException> { classUnderTest.getGroupById(id = id) }

            result should beInstanceOf<NotFoundException>()
            result.message shouldBe GroupServiceImpl.ID_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = withArg { it shouldBe ObjectId(id) }) }
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
    inner class CheckJmsTopic {
        @Test
        fun `should return true when jmsTopic is already in db and ids are different`() = runBlocking {
            val (id, _, _, jmsTopic) = getFirstGroup()
            coEvery { repository.getGroupByJmsTopic(jmsTopic = jmsTopic) } returns getSecondGroup()

            val result: Boolean = classUnderTest.checkJmsTopic(id = id.toString(), jmsTopic = jmsTopic)

            result.shouldBeTrue()
            coVerify(exactly = 1) { repository.getGroupByJmsTopic(jmsTopic = withArg { it shouldBe jmsTopic }) }
        }

        @Test
        fun `should return false when jmsTopic is in database and ids are equal`() = runBlocking {
            val (id, _, _, jmsTopic) = getFirstGroup()
            coEvery { repository.getGroupByJmsTopic(jmsTopic = any()) } returns getFirstGroup()

            val result: Boolean = classUnderTest.checkJmsTopic(id = id.toString(), jmsTopic = jmsTopic)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getGroupByJmsTopic(jmsTopic = withArg { it shouldBe jmsTopic }) }
        }

        @Test
        fun `should return false when jmsTopic is not in database`() = runBlocking {
            val (id, _, _, jmsTopic) = getFirstGroup()
            coEvery { repository.getGroupByJmsTopic(jmsTopic = any()) } returns null

            val result: Boolean = classUnderTest.checkJmsTopic(id = id.toString(), jmsTopic = jmsTopic)

            result.shouldBeFalse()
            coVerify(exactly = 1) { repository.getGroupByJmsTopic(jmsTopic = withArg { it shouldBe jmsTopic }) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val (id, _, _, jmsTopic) = getFirstGroup()
            coEvery { repository.getGroupByJmsTopic(jmsTopic = any()) } throws Exception()

            val result = shouldThrow<Exception> {
                classUnderTest.checkJmsTopic(id = id.toString(), jmsTopic = jmsTopic)
            }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getGroupByJmsTopic(jmsTopic = withArg { it shouldBe jmsTopic }) }
        }
    }

    @Nested
    inner class CreateGroup {
        @Test
        fun `should return and create a group`() = runBlocking {
            val testGroup: Group = getFirstGroup()
            coEvery { repository.save(entry = any()) } returns testGroup

            val result: Group = classUnderTest.createGroup(group = testGroup)

            result shouldBe testGroup
            coVerify(exactly = 1) { repository.save(entry = withArg { it shouldBe testGroup }) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val testGroup: Group = getFirstGroup()
            coEvery { repository.save(entry = any()) } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.createGroup(group = testGroup) }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.save(entry = withArg { it shouldBe testGroup }) }
        }
    }

    @Nested
    inner class UpdateGroup {
        @Test
        fun `should return and update a group`() = runBlocking {
            val testGroup: Group = getFirstGroup()
            coEvery { repository.save(entry = any()) } returns testGroup

            val result: Group = classUnderTest.updateGroup(group = testGroup)

            result shouldBe testGroup
            coVerify(exactly = 1) { repository.save(entry = withArg { it shouldBe testGroup }) }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            val testGroup: Group = getFirstGroup()
            coEvery { repository.save(entry = any()) } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.updateGroup(group = testGroup) }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.save(entry = withArg { it shouldBe testGroup }) }
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
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = getFirstGroup().id.toString()
            coEvery { repository.deleteById(id = any()) } returns false

            val result = shouldThrow<NotFoundException> { classUnderTest.deleteGroupById(id = id) }

            result should beInstanceOf<NotFoundException>()
            result.message shouldBe GroupServiceImpl.ID_MESSAGE
            coVerify(exactly = 1) { repository.deleteById(id = withArg { it shouldBe ObjectId(id) }) }
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
                repository.getById(id = withArg { it shouldBe ObjectId(id) })
                repository.save(entry = withArg { it shouldBe getFirstGroup(stats = ZERO_STATS) })
            }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = getFirstGroup().id.toString()
            coEvery { repository.getById(id = any()) } returns null

            val result = shouldThrow<NotFoundException> { classUnderTest.resetStats(id = id) }

            result should beInstanceOf<NotFoundException>()
            result.message shouldBe GroupServiceImpl.ID_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = withArg { it shouldBe ObjectId(id) }) }
            coVerify(exactly = 0) { repository.save(entry = any()) }
        }
    }
}
