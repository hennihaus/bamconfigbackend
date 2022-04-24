package de.hennihaus.repositories

import de.hennihaus.configurations.MongoConfiguration
import de.hennihaus.models.Task
import de.hennihaus.objectmothers.TaskObjectMother.getAsynchronousBankTask
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
import org.litote.kmongo.id.toId

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskRepositoryIntegrationTest : KoinTest {

    private val mongoContainer = MongoContainer.INSTANCE
    private val classUnderTest: TaskRepository by inject()

    @JvmField
    @RegisterExtension
    @Suppress("unused")
    val koinTestExtension = KoinTestExtension.create {
        initKoin(
            properties = mapOf(
                MongoConfiguration.DATABASE_HOST to mongoContainer.host,
                MongoConfiguration.DATABASE_PORT to mongoContainer.firstMappedPort.toString(),
                MongoConfiguration.DATABASE_NAME to MongoContainer.DATABASE_NAME
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
        fun `should find a task by id`() = runBlocking {
            val id = TestContainerObjectMother.TASK_OBJECT_ID

            val result: Task? = classUnderTest.getById(id = id)

            result should beInstanceOf<Task>()
            result!!.banks.size shouldBeGreaterThanOrEqual 1
            result.banks[0].groups.size shouldBeGreaterThanOrEqual 1
        }

        @Test
        fun `should return null when task is not in db`() = runBlocking {
            val id = ObjectId()

            val result: Task? = classUnderTest.getById(id = id)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class GetAll {
        @Test
        fun `should return at least one task`() = runBlocking {
            val result: List<Task> = classUnderTest.getAll()

            result.size shouldBeGreaterThanOrEqual 1
            result[0].banks.size shouldBeGreaterThanOrEqual 1
            result[0].banks[0].groups.size shouldBeGreaterThanOrEqual 1
        }
    }

    @Nested
    inner class Save {
        @Test
        fun `should save an existing task`() = runBlocking {
            val task = getAsynchronousBankTask(
                id = TestContainerObjectMother.TASK_OBJECT_ID.toId(),
                title = "New title"
            )

            val result: Task = classUnderTest.save(entry = task)

            result shouldBe task
            classUnderTest.getById(id = ObjectId(task.id.toString())) shouldBe task
        }

        @Test
        fun `should save a task when no existing bank is in db`() = runBlocking {
            val task = getAsynchronousBankTask(id = ObjectId().toId())

            val result: Task = classUnderTest.save(entry = task)

            result shouldBe task
            classUnderTest.getById(id = ObjectId(task.id.toString())) shouldBe task
        }
    }

    @Nested
    inner class DeleteById {
        @Test
        fun `should return true one task was deleted by id`() = runBlocking {
            val id = TestContainerObjectMother.TASK_OBJECT_ID

            val result: Boolean = classUnderTest.deleteById(id = id)

            result.shouldBeTrue()
        }

        @Test
        fun `should return false when no bank was deleted by id`() = runBlocking {
            val id = ObjectId()

            val result: Boolean = classUnderTest.deleteById(id = id)

            result.shouldBeFalse()
        }
    }
}
