package de.hennihaus.routes.validations

import de.hennihaus.objectmothers.TaskObjectMother.getAsynchronousBankTask
import de.hennihaus.objectmothers.TaskObjectMother.getDefaultContact
import de.hennihaus.objectmothers.TaskObjectMother.getSchufaTask
import de.hennihaus.objectmothers.TaskObjectMother.getSynchronousBankTask
import de.hennihaus.routes.mappers.toTaskDTO
import de.hennihaus.routes.validations.TaskValidationService.Companion.PARAMETER_DESCRIPTION_MAX_LENGTH
import de.hennihaus.routes.validations.TaskValidationService.Companion.PARAMETER_EXAMPLE_MAX_LENGTH
import de.hennihaus.routes.validations.TaskValidationService.Companion.PARAMETER_EXAMPLE_MIN_LENGTH
import de.hennihaus.routes.validations.TaskValidationService.Companion.RESPONSE_DESCRIPTION_MAX_LENGTH
import de.hennihaus.routes.validations.TaskValidationService.Companion.RESPONSE_DESCRIPTION_MIN_LENGTH
import de.hennihaus.routes.validations.TaskValidationService.Companion.TASK_DESCRIPTION_MAX_LENGTH
import de.hennihaus.routes.validations.TaskValidationService.Companion.TASK_TITLE_MAX_LENGTH
import de.hennihaus.routes.validations.TaskValidationService.Companion.TASK_TITLE_MIN_LENGTH
import de.hennihaus.routes.validations.ValidationService.Companion.NAME_MAX_LENGTH
import de.hennihaus.routes.validations.ValidationService.Companion.NAME_MIN_LENGTH
import de.hennihaus.services.TaskService
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.property.Arb
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyAll
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TaskValidationServiceTest {

    private val task = mockk<TaskService>()

    private val classUnderTest = TaskValidationService(
        task = task,
    )

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class ValidateBody {
        @BeforeEach
        fun init() {
            coEvery { task.isTitleUnique(id = any(), title = any()) } returns true
            coEvery { task.getAllParametersById(id = any()) } returns buildList {
                addAll(elements = getSchufaTask().parameters.map { it.uuid })
                addAll(elements = getSynchronousBankTask().parameters.map { it.uuid })
                addAll(elements = getAsynchronousBankTask().parameters.map { it.uuid })
            }
            coEvery { task.getAllResponsesById(id = any()) } returns buildList {
                addAll(elements = getSchufaTask().responses.map { it.uuid })
                addAll(elements = getSynchronousBankTask().responses.map { it.uuid })
                addAll(elements = getAsynchronousBankTask().responses.map { it.uuid })
            }
        }

        @Test
        fun `should return an empty list when schufa task is valid`() = runBlocking {
            val body = getSchufaTask().toTaskDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
            coVerifyAll {
                task.isTitleUnique(id = body.uuid, title = body.title)
                task.getAllParametersById(id = body.uuid)
                task.getAllResponsesById(id = body.uuid)
            }
        }

        @Test
        fun `should return an empty list when synchronous bank task is valid`() = runBlocking<Unit> {
            val body = getSynchronousBankTask().toTaskDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return an empty list when asynchronous bank task is valid`() = runBlocking<Unit> {
            val body = getAsynchronousBankTask().toTaskDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
        }

        @Test
        fun `should return a list with one error when uuid is invalid`() = runBlocking {
            val body = getSchufaTask().toTaskDTO().copy(
                uuid = "invalidUUID",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("uuid must have valid uuid format")
        }

        @Test
        fun `should return a list with one error when description is too long`() = runBlocking {
            val body = getSchufaTask().toTaskDTO().copy(
                description = Arb.string(size = TASK_DESCRIPTION_MAX_LENGTH.inc()).single(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("description must have at most $TASK_DESCRIPTION_MAX_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when title is too short`() = runBlocking {
            val body = getSchufaTask().toTaskDTO().copy(
                title = Arb.string(size = TASK_TITLE_MIN_LENGTH.dec()).single(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("title must have at least $TASK_TITLE_MIN_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when title is too long`() = runBlocking {
            val body = getSchufaTask().toTaskDTO().copy(
                title = Arb.string(size = TASK_TITLE_MAX_LENGTH.inc()).single(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("title must have at most $TASK_TITLE_MAX_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when title is not unique`() = runBlocking {
            coEvery { task.isTitleUnique(id = any(), title = any()) } returns false
            val body = getSchufaTask().toTaskDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("title must be unique")
            coVerify(exactly = 1) { task.isTitleUnique(id = body.uuid, body.title) }
        }

        @Test
        fun `should return a list with one error when integrationStep has no enum value`() = runBlocking {
            val body = getSchufaTask().toTaskDTO().copy(
                integrationStep = Int.MIN_VALUE,
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("integrationStep must be one of: '1', '2', '3'")
        }

        @Test
        fun `should return a list with one error when contact has an invalid uuid`() = runBlocking {
            val contact = getSchufaTask().toTaskDTO().contact.copy(
                uuid = "invalidUUID",
            )
            val body = getSchufaTask().toTaskDTO().copy(
                contact = contact,
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("contact.uuid must have valid uuid format")
        }

        @Test
        fun `should return a list with one error when firstname of a contact is too short`() = runBlocking {
            val contact = getSchufaTask().toTaskDTO().contact.copy(
                firstname = Arb.string(size = NAME_MIN_LENGTH.dec()).single(),
            )
            val body = getSchufaTask().toTaskDTO().copy(
                contact = contact,
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("contact.firstname must have at least $NAME_MIN_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when firstname of a contact is too long`() = runBlocking {
            val contact = getSchufaTask().toTaskDTO().contact.copy(
                firstname = Arb.string(size = NAME_MAX_LENGTH.inc()).single(),
            )
            val body = getSchufaTask().toTaskDTO().copy(
                contact = contact,
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("contact.firstname must have at most $NAME_MAX_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when lastname of a contact is too short`() = runBlocking {
            val contact = getSchufaTask().toTaskDTO().contact.copy(
                lastname = Arb.string(size = NAME_MIN_LENGTH.dec()).single(),
            )
            val body = getSchufaTask().toTaskDTO().copy(
                contact = contact,
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("contact.lastname must have at least $NAME_MIN_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when lastname of a contact is too long`() = runBlocking {
            val contact = getSchufaTask().toTaskDTO().contact.copy(
                lastname = Arb.string(size = NAME_MAX_LENGTH.inc()).single(),
            )
            val body = getSchufaTask().toTaskDTO().copy(
                contact = contact,
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("contact.lastname must have at most $NAME_MAX_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when contact has an invalid email`() = runBlocking {
            val contact = getDefaultContact(email = "invalidEmail")
            val body = getSchufaTask(contact = contact).toTaskDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("contact.email must have valid email format")
        }

        @Test
        fun `should return a list with one error when one endpoint has an invalid uuid`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                endpoints = listOf(
                    task.endpoints.first().copy(
                        uuid = "invalidUUID",
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("endpoints[0].uuid must have valid uuid format")
        }

        @Test
        fun `should return a list with one error when one endpoint type has no enum value`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                endpoints = listOf(
                    task.endpoints.first().copy(
                        type = "invalidType",
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("endpoints[0].type must be one of: 'REST', 'JMS'")
        }

        @Test
        fun `should return a list with one error when one endpoint has an invalid url`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                endpoints = listOf(
                    task.endpoints.first().copy(
                        url = "invalidUrl",
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("endpoints[0].url must have valid url format")
        }

        @Test
        fun `should return a list with one error when one endpoint has an invalid docsUrl`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                endpoints = listOf(
                    task.endpoints.first().copy(
                        docsUrl = "invalidUrl",
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("endpoints[0].docsUrl must have valid url format")
        }

        @Test
        fun `should return a list with one error when parameter elements are not unique`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                parameters = task.parameters + task.parameters[0],
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("parameters all items must be unique")
        }

        @Test
        fun `should return a list with two errors when one uuid parameter is invalid and not exists`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                parameters = listOf(
                    task.parameters.first().copy(
                        uuid = "invalidUUID",
                    )
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "parameters[0].uuid must have valid uuid format",
                "parameters[0].uuid must exists",
            )
        }

        @Test
        fun `should return a list with one error when one parameter type has no enum value`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                parameters = listOf(
                    task.parameters.first().copy(
                        type = "unknownType",
                    )
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "parameters[0].type must be one of: 'STRING', 'INTEGER', 'LONG', 'CHARACTER'",
            )
        }

        @Test
        fun `should return a list with one error when one parameter description is too long`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                parameters = listOf(
                    task.parameters.first().copy(
                        description = Arb.string(size = PARAMETER_DESCRIPTION_MAX_LENGTH.inc()).single(),
                    )
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "parameters[0].description must have at most $PARAMETER_DESCRIPTION_MAX_LENGTH characters",
            )
        }

        @Test
        fun `should return a list with one error when one parameter example is too short`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                parameters = listOf(
                    task.parameters.first().copy(
                        example = Arb.string(size = PARAMETER_EXAMPLE_MIN_LENGTH.dec()).single(),
                    )
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "parameters[0].example must have at least $PARAMETER_EXAMPLE_MIN_LENGTH characters",
            )
        }

        @Test
        fun `should return a list with one error when one parameter example is too long`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                parameters = listOf(
                    task.parameters.first().copy(
                        example = Arb.string(size = PARAMETER_EXAMPLE_MAX_LENGTH.inc()).single(),
                    )
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "parameters[0].example must have at most $PARAMETER_EXAMPLE_MAX_LENGTH characters",
            )
        }

        @Test
        fun `should return a list with one error when response elements are not unique`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                responses = task.responses + task.responses[0],
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("responses all items must be unique")
        }

        @Test
        fun `should return a list with two errors when one uuid response is invalid and not exists`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                responses = listOf(
                    task.responses.first().copy(
                        uuid = "invalidUUID",
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "responses[0].uuid must have valid uuid format",
                "responses[0].uuid must exists",
            )
        }

        @Test
        fun `should return a list with one error when one response httpStatusCode is unknown`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                responses = listOf(
                    task.responses.first().copy(
                        httpStatusCode = Int.MIN_VALUE,
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "responses[0].httpStatusCode must be one of: '-1', '100', '101', '102', '200', '201', '202', '203', '204', '205', '206', '207', '300', '301', '302', '303', '304', '305', '306', '307', '308', '400', '401', '402', '403', '404', '405', '406', '407', '408', '409', '410', '411', '412', '413', '414', '415', '416', '417', '422', '423', '424', '426', '429', '431', '500', '501', '502', '503', '504', '505', '506', '507'",
            )
        }

        @Test
        fun `should return a list with one error when one response contentType not parseable`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                responses = listOf(
                    task.responses.first().copy(
                        contentType = "unknownContentType",
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("responses[0].contentType must have valid content type format")
        }

        @Test
        fun `should return a list with one error when one response description is too short`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                responses = listOf(
                    task.responses.first().copy(
                        description = Arb.string(size = RESPONSE_DESCRIPTION_MIN_LENGTH.dec()).single(),
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "responses[0].description must have at least $RESPONSE_DESCRIPTION_MIN_LENGTH characters",
            )
        }

        @Test
        fun `should return a list with one error when one response description is too long`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                responses = listOf(
                    task.responses.first().copy(
                        description = Arb.string(size = RESPONSE_DESCRIPTION_MAX_LENGTH.inc()).single(),
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf(
                "responses[0].description must have at most $RESPONSE_DESCRIPTION_MAX_LENGTH characters",
            )
        }

        @Test
        fun `should return a list with one error when one response example is not json`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                responses = listOf(
                    task.responses.first().copy(
                        example = "invalidJson",
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("responses[0].example must have valid json format")
        }

        @Test
        fun `should return a list with one error when one response example empty json object`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                responses = listOf(
                    task.responses.first().copy(
                        example = "{}",
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("responses[0].example must not be '{}'")
        }

        @Test
        fun `should return a list with one error when one response example empty json array`() = runBlocking {
            val task = getSchufaTask().toTaskDTO()
            val body = task.copy(
                responses = listOf(
                    task.responses.first().copy(
                        example = "[]",
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("responses[0].example must not be '[]'")
        }
    }
}
