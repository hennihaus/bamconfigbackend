package de.hennihaus.repositories

import de.hennihaus.configurations.Configuration.DEFAULT_ZONE_ID
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_HOST
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_PORT
import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.models.Parameter
import de.hennihaus.models.Task
import de.hennihaus.objectmothers.EndpointObjectMother
import de.hennihaus.objectmothers.EndpointObjectMother.getSchufaRestEndpoint
import de.hennihaus.objectmothers.EndpointObjectMother.getVBankRestEndpoint
import de.hennihaus.objectmothers.ExposedContainerObjectMother
import de.hennihaus.objectmothers.ParameterObjectMother
import de.hennihaus.objectmothers.ParameterObjectMother.getAmountInEurosParameter
import de.hennihaus.objectmothers.ParameterObjectMother.getRequestIdParameter
import de.hennihaus.objectmothers.ResponseObjectMother
import de.hennihaus.objectmothers.ResponseObjectMother.getInternalServerErrorResponse
import de.hennihaus.objectmothers.ResponseObjectMother.getNotFoundResponse
import de.hennihaus.objectmothers.TaskObjectMother.getAsynchronousBankTask
import de.hennihaus.objectmothers.TaskObjectMother.getDefaultContact
import de.hennihaus.plugins.initKoin
import de.hennihaus.testutils.containers.ExposedContainer
import io.kotest.extensions.time.withConstantNow
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
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
import java.time.ZoneId
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskRepositoryIntegrationTest : KoinTest {

    private val exposedContainer = ExposedContainer.INSTANCE
    private val classUnderTest: TaskRepository by inject()

    @JvmField
    @RegisterExtension
    @Suppress("unused")
    val koinTestExtension = KoinTestExtension.create {
        initKoin(
            properties = mapOf(
                DATABASE_HOST to exposedContainer.host,
                DATABASE_PORT to exposedContainer.firstMappedPort.toString(),
            )
        )
    }

    @BeforeEach
    fun init() = ExposedContainer.resetState()

    @AfterAll
    fun cleanUp() = stopKoin()

    @Nested
    inner class GetById {
        @Test
        fun `should find a task by id`() = runBlocking<Unit> {
            val id = ExposedContainerObjectMother.TASK_UUID

            val result: Task? = classUnderTest.getById(id = id)

            result.shouldNotBeNull()
            result.endpoints.shouldNotBeEmpty()
            result.parameters.shouldNotBeEmpty()
            result.responses.shouldNotBeEmpty()
            result.banks.shouldNotBeEmpty()
        }

        @Test
        fun `should return null when task is not in db`() = runBlocking {
            val id = ExposedContainerObjectMother.UNKNOWN_UUID

            val result: Task? = classUnderTest.getById(id = id)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class GetAll {
        @Test
        fun `should return at a minimum one task`() = runBlocking<Unit> {
            val result: List<Task> = classUnderTest.getAll()

            result.shouldNotBeEmpty()
        }
    }

    @Nested
    inner class DeleteById {
        @Test
        fun `should return true one task was deleted by id`() = runBlocking {
            val id = ExposedContainerObjectMother.TASK_UUID

            val result: Boolean = classUnderTest.deleteById(id = id)

            result.shouldBeTrue()
        }

        @Test
        fun `should return false when no bank was deleted by id`() = runBlocking {
            val id = ExposedContainerObjectMother.UNKNOWN_UUID

            val result: Boolean = classUnderTest.deleteById(id = id)

            result.shouldBeFalse()
        }
    }

    @Nested
    inner class Save {
        @Test
        fun `should save an existing task`() = runBlocking {
            val task = getAsynchronousBankTask(
                uuid = ExposedContainerObjectMother.TASK_UUID,
                title = "New title",
                contact = getDefaultContact(
                    uuid = ExposedContainerObjectMother.UNKNOWN_UUID,
                ),
                endpoints = listOf(
                    getSchufaRestEndpoint(uuid = ExposedContainerObjectMother.UNKNOWN_UUID),
                    getVBankRestEndpoint(
                        uuid = UUID.fromString(EndpointObjectMother.V_BANK_UUID),
                    ),
                ),
                parameters = listOf(
                    getRequestIdParameter(
                        uuid = ExposedContainerObjectMother.UNKNOWN_UUID,
                        name = "NewParameter",
                    ),
                    getAmountInEurosParameter(
                        uuid = UUID.fromString(ParameterObjectMother.AMOUNT_IN_EUROS_UUID),
                    ),
                ),
                responses = listOf(
                    getInternalServerErrorResponse(uuid = ExposedContainerObjectMother.UNKNOWN_UUID),
                    getNotFoundResponse(
                        uuid = UUID.fromString(ResponseObjectMother.NOT_FOUND_UUID),
                    ),
                )
            )

            val result: Task = withConstantNow(
                now = task.updatedAt.atZone(ZoneId.of(DEFAULT_ZONE_ID)).toOffsetDateTime(),
            ) {
                classUnderTest.save(
                    entry = task,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }

            result.shouldBeEqualToIgnoringFields(
                other = task,
                property = Task::banks,
            )
        }

        @Test
        fun `should save a task when no existing task is in db`() = runBlocking<Unit> {
            val task = getAsynchronousBankTask(uuid = ExposedContainerObjectMother.UNKNOWN_UUID)
            classUnderTest.deleteById(id = getAsynchronousBankTask().uuid)

            val result: Task = withConstantNow(
                now = task.updatedAt.atZone(ZoneId.of(DEFAULT_ZONE_ID)).toOffsetDateTime(),
            ) {
                classUnderTest.save(
                    entry = task,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }

            result.shouldBeEqualToIgnoringFields(
                other = task,
                Task::banks,
                Task::parameters,
            )
            result.parameters shouldContainExactlyInAnyOrder task.parameters
        }
    }

    @Nested
    inner class GetTaskIdByTitle {
        @Test
        fun `should return a task uuid when task is found by title`() = runBlocking<Unit> {
            val title = ExposedContainerObjectMother.TASK_TITLE

            val result: UUID? = classUnderTest.getTaskIdByTitle(title = title)

            result.shouldNotBeNull()
        }

        @Test
        fun `should return null when task is not found by title`() = runBlocking {
            val title = "unknownTitle"

            val result: UUID? = classUnderTest.getTaskIdByTitle(title = title)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class GetAllParametersById {
        @Test
        fun `should return at a minimum one parameterId`() = runBlocking<Unit> {
            val id = ExposedContainerObjectMother.TASK_UUID

            val result: List<UUID> = classUnderTest.getAllParametersById(id = id)

            result.shouldNotBeEmpty()
        }
    }

    @Nested
    inner class GetAllResponsesById {
        @Test
        fun `should return at a minimum one responseId`() = runBlocking<Unit> {
            val id = ExposedContainerObjectMother.TASK_UUID

            val result: List<UUID> = classUnderTest.getAllResponsesById(id = id)

            result.shouldNotBeEmpty()
        }
    }

    @Nested
    inner class UpdateParameter {
        @Test
        fun `should update example property of parameter and return a parameter`() = runBlocking<Unit> {
            val name = ExposedContainerObjectMother.PARAMETER_NAME
            val example = "TestParameterName"

            val result: Parameter? = classUnderTest.updateParameter(
                name = name,
                example = example,
                repetitionAttempts = ONE_REPETITION_ATTEMPT,
            )

            result.shouldNotBeNull()
            result.example shouldBe example
        }

        @Test
        fun `should update no parameter and return null when name is unknown`() = runBlocking {
            val name = "unknownName"
            val example = "nonAvailableExampleValue"

            val result: Parameter? = classUnderTest.updateParameter(
                name = name,
                example = example,
                repetitionAttempts = ONE_REPETITION_ATTEMPT,
            )

            classUnderTest.getAll().flatMap { it.parameters }.forAll {
                it.example shouldNotBe example
            }
            result.shouldBeNull()
        }
    }
}
