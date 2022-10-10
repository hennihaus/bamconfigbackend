package de.hennihaus.routes.validations

import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.ASYNC_BANK_NAME
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.SCHUFA_BANK_NAME
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.SYNC_BANK_NAME
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.routes.mappers.toTeamDTO
import de.hennihaus.routes.validations.TeamValidationService.Companion.TEAM_PASSWORD_MAX_LENGTH
import de.hennihaus.routes.validations.TeamValidationService.Companion.TEAM_PASSWORD_MIN_LENGTH
import de.hennihaus.routes.validations.TeamValidationService.Companion.TEAM_USERNAME_MAX_LENGTH
import de.hennihaus.routes.validations.TeamValidationService.Companion.TEAM_USERNAME_MIN_LENGTH
import de.hennihaus.routes.validations.ValidationService.Companion.JMS_QUEUE_MAX_LENGTH
import de.hennihaus.routes.validations.ValidationService.Companion.JMS_QUEUE_MIN_LENGTH
import de.hennihaus.routes.validations.ValidationService.Companion.NAME_MAX_LENGTH
import de.hennihaus.routes.validations.ValidationService.Companion.NAME_MIN_LENGTH
import de.hennihaus.services.BankService
import de.hennihaus.services.TeamService
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

class TeamValidationServiceTest {

    private val team = mockk<TeamService>()
    private val bank = mockk<BankService>()

    private val classUnderTest = TeamValidationService(
        team = team,
        bank = bank,
    )

    @BeforeEach
    fun init() = clearAllMocks()

    @Nested
    inner class ValidateBody {
        @BeforeEach
        fun init() {
            coEvery { team.isUsernameUnique(id = any(), username = any()) } returns true
            coEvery { team.isPasswordUnique(id = any(), password = any()) } returns true
            coEvery { team.isJmsQueueUnique(id = any(), jmsQueue = any()) } returns true
            coEvery { team.getJmsQueueById(id = any()) } returns getFirstTeam().jmsQueue
            coEvery { bank.hasName(name = any()) } returns true
        }

        @Test
        fun `should return an empty list when team is valid`() = runBlocking {
            val body = getFirstTeam().toTeamDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
            coVerifyAll {
                team.isUsernameUnique(id = body.uuid, username = body.username)
                team.isPasswordUnique(id = body.uuid, password = body.password)
                team.isJmsQueueUnique(id = body.uuid, jmsQueue = body.jmsQueue)
                team.getJmsQueueById(id = body.uuid)
                bank.hasName(name = SCHUFA_BANK_NAME)
                bank.hasName(name = SYNC_BANK_NAME)
                bank.hasName(name = ASYNC_BANK_NAME)
            }
        }

        @Test
        fun `should return an empty list when team is created with new jmsQueue`() = runBlocking {
            coEvery { team.getJmsQueueById(id = any()) } returns null
            val body = getFirstTeam().toTeamDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result.shouldBeEmpty()
            coVerify(exactly = 1) { team.getJmsQueueById(id = body.uuid) }
        }

        @Test
        fun `should return a list with one error when uuid is invalid`() = runBlocking {
            val body = getFirstTeam().toTeamDTO().copy(
                uuid = "invalidUUID",
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("uuid must have valid uuid format")
        }

        @Test
        fun `should return a list with one error when username is too short`() = runBlocking {
            val body = getFirstTeam().toTeamDTO().copy(
                username = Arb.string(size = TEAM_USERNAME_MIN_LENGTH.dec()).single(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("username must have at least $TEAM_USERNAME_MIN_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when username is too long`() = runBlocking {
            val body = getFirstTeam().toTeamDTO().copy(
                username = Arb.string(size = TEAM_USERNAME_MAX_LENGTH.inc()).single(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("username must have at most $TEAM_USERNAME_MAX_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when username is not unique`() = runBlocking {
            coEvery { team.isUsernameUnique(id = any(), username = any()) } returns false
            val body = getFirstTeam().toTeamDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("username must be unique")
            coVerify(exactly = 1) { team.isUsernameUnique(id = body.uuid, username = body.username) }
        }

        @Test
        fun `should return a list with one error when password is too short`() = runBlocking {
            val body = getFirstTeam().toTeamDTO().copy(
                password = Arb.string(size = TEAM_PASSWORD_MIN_LENGTH.dec()).single(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("password must have at least $TEAM_PASSWORD_MIN_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when password is too long`() = runBlocking {
            val body = getFirstTeam().toTeamDTO().copy(
                password = Arb.string(size = TEAM_PASSWORD_MAX_LENGTH.inc()).single(),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("password must have at most $TEAM_PASSWORD_MAX_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when password is not unique`() = runBlocking {
            coEvery { team.isPasswordUnique(id = any(), password = any()) } returns false
            val body = getFirstTeam().toTeamDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("password must be unique")
            coVerify(exactly = 1) { team.isPasswordUnique(id = body.uuid, password = body.password) }
        }

        @Test
        fun `should return a list with one error when jmsQueue is too short`() = runBlocking {
            val jmsQueue = Arb.string(size = JMS_QUEUE_MIN_LENGTH.dec()).single()
            val body = getFirstTeam().toTeamDTO().copy(
                jmsQueue = jmsQueue,
            )
            coEvery { team.getJmsQueueById(id = any()) } returns jmsQueue

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("jmsQueue must have at least $JMS_QUEUE_MIN_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when jmsQueue is too long`() = runBlocking {
            val jmsQueue = Arb.string(size = JMS_QUEUE_MAX_LENGTH.inc()).single()
            val body = getFirstTeam().toTeamDTO().copy(
                jmsQueue = jmsQueue,
            )
            coEvery { team.getJmsQueueById(id = any()) } returns jmsQueue

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("jmsQueue must have at most $JMS_QUEUE_MAX_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when jmsQueue is not unique`() = runBlocking {
            coEvery { team.isJmsQueueUnique(id = any(), jmsQueue = any()) } returns false
            val body = getFirstTeam().toTeamDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("jmsQueue must be unique")
            coVerify(exactly = 1) { team.isJmsQueueUnique(id = body.uuid, jmsQueue = body.jmsQueue) }
        }

        @Test
        fun `should return a list with one error when jmsQueue is not old jmsQueue`() = runBlocking {
            coEvery { team.getJmsQueueById(id = any()) } returns "jmsQueue"
            val body = getFirstTeam().toTeamDTO()

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("jmsQueue must be old jmsQueue")
            coVerify(exactly = 1) { team.getJmsQueueById(id = body.uuid) }
        }

        @Test
        fun `should return a list with one error when bankName in statistic does not exist`() = runBlocking {
            coEvery { bank.hasName(name = any()) } returnsMany listOf(true, true, true, false)
            val body = getFirstTeam().toTeamDTO().copy(
                statistics = getFirstTeam().statistics + Pair(
                    first = "unknownBankName",
                    second = 0L,
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("statistics.unknownBankName.key must exists")
            coVerifyAll {
                bank.hasName(name = SCHUFA_BANK_NAME)
                bank.hasName(name = SYNC_BANK_NAME)
                bank.hasName(name = ASYNC_BANK_NAME)
                bank.hasName(name = "unknownBankName")
            }
        }

        @Test
        fun `should return a list with one error when student elements are not unique`() = runBlocking {
            val team = getFirstTeam().toTeamDTO()
            val body = team.copy(
                students = team.students + team.students[0],
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("students all items must be unique")
        }

        @Test
        fun `should return a list with one error when one student has an invalid uuid`() = runBlocking {
            val team = getFirstTeam().toTeamDTO()
            val body = team.copy(
                students = listOf(
                    team.students.first().copy(
                        uuid = "invalidUUID",
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("students[0].uuid must have valid uuid format")
        }

        @Test
        fun `should return a list with one error when one student has a too short firstname`() = runBlocking {
            val team = getFirstTeam().toTeamDTO()
            val body = team.copy(
                students = listOf(
                    team.students.first().copy(
                        firstname = Arb.string(size = NAME_MIN_LENGTH.dec()).single(),
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("students[0].firstname must have at least $NAME_MIN_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when one student has a too long firstname`() = runBlocking {
            val team = getFirstTeam().toTeamDTO()
            val body = team.copy(
                students = listOf(
                    team.students.first().copy(
                        firstname = Arb.string(size = NAME_MAX_LENGTH.inc()).single(),
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("students[0].firstname must have at most $NAME_MAX_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when one student has a too short lastname`() = runBlocking {
            val team = getFirstTeam().toTeamDTO()
            val body = team.copy(
                students = listOf(
                    team.students.first().copy(
                        lastname = Arb.string(size = NAME_MIN_LENGTH.dec()).single(),
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("students[0].lastname must have at least $NAME_MIN_LENGTH characters")
        }

        @Test
        fun `should return a list with one error when one student has a too long lastname`() = runBlocking {
            val team = getFirstTeam().toTeamDTO()
            val body = team.copy(
                students = listOf(
                    team.students.first().copy(
                        lastname = Arb.string(size = NAME_MAX_LENGTH.inc()).single(),
                    ),
                ),
            )

            val result: List<String> = classUnderTest.validateBody(
                body = body,
            )

            result shouldContainExactly listOf("students[0].lastname must have at most $NAME_MAX_LENGTH characters")
        }
    }
}
