package de.hennihaus.repositories

import de.hennihaus.configurations.MongoConfiguration.DATABASE_HOST
import de.hennihaus.configurations.MongoConfiguration.DATABASE_NAME
import de.hennihaus.configurations.MongoConfiguration.DATABASE_PORT
import de.hennihaus.models.Group
import de.hennihaus.objectmothers.GroupObjectMother.getFirstGroup
import de.hennihaus.objectmothers.TestContainerObjectMother
import de.hennihaus.plugins.initKoin
import de.hennihaus.testutils.MongoContainer
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import kotlinx.coroutines.runBlocking
import org.bson.types.ObjectId
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
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.id.toId

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupRepositoryIntegrationTest : KoinTest {

    private val mongoContainer = MongoContainer.INSTANCE
    private val classUnderTest: GroupRepository by inject()

    @JvmField
    @RegisterExtension
    @Suppress("unused")
    val koinTestExtension = KoinTestExtension.create {
        initKoin(
            properties = mapOf(
                DATABASE_HOST to mongoContainer.host,
                DATABASE_PORT to mongoContainer.firstMappedPort.toString(),
                DATABASE_NAME to MongoContainer.DATABASE_NAME
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
        fun `should find a group by id`() = runBlocking {
            val id = TestContainerObjectMother.GROUP_OBJECT_ID

            val result: Group? = classUnderTest.getById(id = id)

            result should beInstanceOf<Group>()
        }

        @Test
        fun `should return null when id is not in db`() = runBlocking {
            val id = ObjectId()

            val result: Group? = classUnderTest.getById(id = id)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class GetAll {
        @Test
        fun `should return at least one group`() = runBlocking {
            val result: List<Group> = classUnderTest.getAll()

            result.size shouldBeGreaterThanOrEqual 1
        }
    }

    @Nested
    inner class Save {
        @Test
        fun `should save an existing group`() = runBlocking {
            val group = getFirstGroup(
                id = TestContainerObjectMother.GROUP_OBJECT_ID.toId(),
                username = "NewUsername"
            )

            val result: Group = classUnderTest.save(entry = group)

            result shouldBe group
            getKoin()
                .get<CoroutineDatabase>()
                .getCollection<Group>()
                .findOneById(id = ObjectId(group.id.toString())) shouldBe group
        }

        @Test
        fun `should save a group when no existing group is in db`() = runBlocking {
            val group = getFirstGroup(id = ObjectId().toId())

            val result: Group = classUnderTest.save(entry = group)

            result shouldBe group
            getKoin()
                .get<CoroutineDatabase>()
                .getCollection<Group>()
                .findOneById(id = ObjectId(group.id.toString())) shouldBe group
        }
    }

    @Nested
    inner class DeleteById {
        @Test
        fun `should return true when one group was deleted by id`() = runBlocking {
            val id = TestContainerObjectMother.GROUP_OBJECT_ID

            val result: Boolean = classUnderTest.deleteById(id = id)

            result.shouldBeTrue()
        }

        @Test
        fun `should return false when no group was deleted by id`() = runBlocking {
            val id = ObjectId()

            val result: Boolean = classUnderTest.deleteById(id = id)

            result.shouldBeFalse()
        }
    }

    @Nested
    inner class GetGroupByUsername {
        @Test
        fun `should return a group when group is found by username`() = runBlocking {
            val username = TestContainerObjectMother.GROUP_USERNAME

            val result: Group? = classUnderTest.getGroupByUsername(username = username)

            result should beInstanceOf<Group>()
        }

        @Test
        fun `should return null when group is not found by username`() = runBlocking {
            val username = "unknown"

            val result: Group? = classUnderTest.getGroupByUsername(username = username)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class GetGroupByPassword {
        @Test
        fun `should return a group when group is found by password`() = runBlocking {
            val password = TestContainerObjectMother.GROUP_PASSWORD

            val result: Group? = classUnderTest.getGroupByPassword(password = password)

            result should beInstanceOf<Group>()
        }

        @Test
        fun `should return null when group is not found by password`() = runBlocking {
            val password = "unknown"

            val result: Group? = classUnderTest.getGroupByPassword(password = password)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class GetGroupByJmsTopic {
        @Test
        fun `should return a group when group is found by jmsTopic`() = runBlocking {
            val jmsTopic = TestContainerObjectMother.GROUP_JMS_TOPIC

            val result: Group? = classUnderTest.getGroupByJmsTopic(jmsTopic = jmsTopic)

            result should beInstanceOf<Group>()
        }

        @Test
        fun `should return null when group is not found by jmsTopic`() = runBlocking {
            val jmsTopic = "unknown"

            val result: Group? = classUnderTest.getGroupByJmsTopic(jmsTopic = jmsTopic)

            result.shouldBeNull()
        }
    }
}
