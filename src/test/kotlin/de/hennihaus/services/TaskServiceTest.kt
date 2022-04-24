package de.hennihaus.services

import de.hennihaus.models.Task
import de.hennihaus.objectmothers.TaskObjectMother.getAsynchronousBankTask
import de.hennihaus.objectmothers.TaskObjectMother.getSchufaTask
import de.hennihaus.objectmothers.TaskObjectMother.getSynchronousBankTask
import de.hennihaus.plugins.NotFoundException
import de.hennihaus.repositories.TaskRepository
import io.kotest.assertions.throwables.shouldThrow
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

class TaskServiceTest {

    private val repository = mockk<TaskRepository>()
    private val classUnderTest = TaskServiceImpl(repository)

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class GetAllTasks {
        @Test
        fun `should return a list of tasks sorted by step asc`() = runBlocking {
            coEvery { repository.getAll() } returns listOf(
                getSynchronousBankTask(step = 2),
                getAsynchronousBankTask(step = 3),
                getSchufaTask(step = 1)
            )

            val response: List<Task> = classUnderTest.getAllTasks()

            response.shouldContainExactly(
                getSchufaTask(step = 1),
                getSynchronousBankTask(step = 2),
                getAsynchronousBankTask(step = 3)
            )
            coVerify(exactly = 1) { repository.getAll() }
        }

        @Test
        fun `should throw an exception when error occurs`() = runBlocking {
            coEvery { repository.getAll() } throws Exception()

            val result = shouldThrow<Exception> { classUnderTest.getAllTasks() }

            result should beInstanceOf<Exception>()
            coVerify(exactly = 1) { repository.getAll() }
        }
    }

    @Nested
    inner class GetTaskById {
        @Test
        fun `should return task when id is in database`() = runBlocking {
            val id = getSchufaTask().id.toString()
            coEvery { repository.getById(id = any()) } returns getSchufaTask()

            val result: Task = classUnderTest.getTaskById(id = id)

            result shouldBe getSchufaTask()
            coVerify(exactly = 1) { repository.getById(id = withArg { it shouldBe ObjectId(id) }) }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = getSchufaTask().id.toString()
            coEvery { repository.getById(id = any()) } returns null

            val result = shouldThrow<NotFoundException> { classUnderTest.getTaskById(id = id) }

            result should beInstanceOf<NotFoundException>()
            result.message shouldBe TaskServiceImpl.ID_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = withArg { it shouldBe ObjectId(id) }) }
        }
    }

    @Nested
    inner class PatchTask {
        @Test
        fun `should just update title, description and parameters from a task`() = runBlocking {
            val id = getSchufaTask().id.toString()
            val task = getSynchronousBankTask(
                title = getSchufaTask().title,
                description = getSchufaTask().description,
                parameters = getSchufaTask().parameters
            )
            coEvery { repository.getById(id = any()) } returns getSchufaTask()
            coEvery { repository.save(entry = any()) } returns getSchufaTask()

            val result: Task = classUnderTest.patchTask(id = id, task = task)

            result shouldBe getSchufaTask()
            coVerifySequence {
                repository.getById(id = withArg { it shouldBe ObjectId(id) })
                repository.save(entry = withArg { getSchufaTask() })
            }
        }

        @Test
        fun `should throw an exception when id is not in database`() = runBlocking {
            val id = getSchufaTask().id.toString()
            val task = getSynchronousBankTask(
                title = getSchufaTask().title,
                description = getSchufaTask().description,
                parameters = getSchufaTask().parameters
            )
            coEvery { repository.getById(id = any()) } returns null
            coEvery { repository.save(entry = any()) } returns getSchufaTask()

            val result = shouldThrow<NotFoundException> { classUnderTest.patchTask(id = id, task = task) }

            result should beInstanceOf<NotFoundException>()
            result.message shouldBe TaskServiceImpl.ID_MESSAGE
            coVerify(exactly = 1) { repository.getById(id = withArg { it shouldBe ObjectId(id) }) }
            coVerify(exactly = 0) { repository.save(entry = any()) }
        }
    }
}
