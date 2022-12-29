package de.hennihaus.repositories

import de.hennihaus.bamdatamodel.Team
import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.SCHUFA_BANK_NAME
import de.hennihaus.bamdatamodel.objectmothers.BankObjectMother.SYNC_BANK_NAME
import de.hennihaus.bamdatamodel.objectmothers.StudentObjectMother.getFirstStudent
import de.hennihaus.bamdatamodel.objectmothers.StudentObjectMother.getSecondStudent
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.configurations.Configuration.DEFAULT_ZONE_ID
import de.hennihaus.configurations.Configuration.PASSWORD_LENGTH
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_HOST
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_PORT
import de.hennihaus.configurations.ExposedConfiguration.ONE_REPETITION_ATTEMPT
import de.hennihaus.objectmothers.CursorObjectMother.getFirstTeamCursorWithEmptyFields
import de.hennihaus.objectmothers.CursorObjectMother.getFirstTeamCursorWithNoEmptyFields
import de.hennihaus.objectmothers.ExposedContainerObjectMother
import de.hennihaus.objectmothers.ExposedContainerObjectMother.BANK_NAME
import de.hennihaus.objectmothers.TeamQueryObjectMother.getTeamQueryWithNoEmptyFields
import de.hennihaus.plugins.initKoin
import de.hennihaus.repositories.StatisticRepository.Companion.ZERO_REQUESTS
import de.hennihaus.testutils.containers.ExposedContainer
import io.kotest.extensions.time.withConstantNow
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.date.shouldBeBetween
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.maps.shouldContainValues
import io.kotest.matchers.maps.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.checkAll
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
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamRepositoryIntegrationTest : KoinTest {

    private val exposedContainer = ExposedContainer.INSTANCE
    private val classUnderTest: TeamRepository by inject()

    @JvmField
    @RegisterExtension
    @Suppress("unused")
    val koinTestExtension = KoinTestExtension.create {
        initKoin(
            properties = mapOf(
                DATABASE_HOST to exposedContainer.host,
                DATABASE_PORT to exposedContainer.firstMappedPort.toString(),
            ),
        )
    }

    @BeforeEach
    fun init() = ExposedContainer.resetState()

    @AfterAll
    fun cleanUp() = stopKoin()

    @Nested
    inner class GetById {
        @Test
        fun `should find a team by id`() = runBlocking {
            val id = ExposedContainerObjectMother.TEAM_UUID

            val result: Team? = classUnderTest.getById(id = id)

            result.shouldNotBeNull()
            result.students.shouldNotBeEmpty()
            result.statistics.shouldNotBeEmpty()
        }

        @Test
        fun `should return null when id is not in db`() = runBlocking {
            val id = ExposedContainerObjectMother.UNKNOWN_UUID

            val result: Team? = classUnderTest.getById(id = id)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class GetAll {
        @Test
        fun `should return at least one team with minimal cursor fields`() = runBlocking<Unit> {
            val cursor = getFirstTeamCursorWithEmptyFields()

            val result: List<Team> = classUnderTest.getAll(cursor = cursor)

            result.shouldNotBeEmpty()
        }

        @Test
        fun `should return at least one team with maximal cursor fields`() = runBlocking<Unit> {
            val cursor = getFirstTeamCursorWithNoEmptyFields(
                query = getTeamQueryWithNoEmptyFields(
                    type = TeamType.REGULAR,
                    username = ExposedContainerObjectMother.TEAM_USERNAME,
                    password = ExposedContainerObjectMother.TEAM_PASSWORD,
                    jmsQueue = ExposedContainerObjectMother.TEAM_JMS_QUEUE,
                    studentFirstname = ExposedContainerObjectMother.STUDENT_FIRSTNAME,
                    studentLastname = ExposedContainerObjectMother.STUDENT_LASTNAME,
                    banks = listOf(BANK_NAME),
                ),
            )

            val result: List<Team> = classUnderTest.getAll(cursor = cursor)

            result.shouldNotBeEmpty()
        }
    }

    @Nested
    inner class DeleteById {
        @Test
        fun `should return true when one team was deleted by id`() = runBlocking {
            val id = ExposedContainerObjectMother.TEAM_UUID

            val result: Boolean = classUnderTest.deleteById(id = id)

            result.shouldBeTrue()
        }

        @Test
        fun `should return false when no team was deleted by id`() = runBlocking {
            val id = ExposedContainerObjectMother.UNKNOWN_UUID

            val result: Boolean = classUnderTest.deleteById(id = id)

            result.shouldBeFalse()
        }
    }

    @Nested
    inner class Save {
        @Test
        fun `should save an existing team`() = runBlocking {
            val team = getFirstTeam(
                uuid = ExposedContainerObjectMother.TEAM_UUID,
                username = "NewUsername",
                students = listOf(
                    getFirstStudent(uuid = ExposedContainerObjectMother.STUDENT_UUID),
                    getSecondStudent(uuid = ExposedContainerObjectMother.UNKNOWN_UUID),
                ),
                statistics = mapOf(
                    SCHUFA_BANK_NAME to 0L,
                    SYNC_BANK_NAME to 0L,
                    BANK_NAME to 1L,
                ),
            )

            val result: Team = withConstantNow(
                now = team.updatedAt.atZone(ZoneId.of(DEFAULT_ZONE_ID)).toOffsetDateTime(),
            ) {
                classUnderTest.save(
                    entry = team,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }

            result.shouldBeEqualToIgnoringFields(
                other = team,
                property = Team::createdAt,
            )
            result.createdAt.atZone(ZoneId.of(DEFAULT_ZONE_ID)).toInstant().shouldBeBetween(
                fromInstant = Instant.now().minusSeconds(FIVE_SECONDS),
                toInstant = Instant.now().plusSeconds(FIVE_SECONDS),
            )
        }

        @Test
        fun `should save a team when no existing team is in db`() = runBlocking {
            val team = getFirstTeam(
                uuid = ExposedContainerObjectMother.UNKNOWN_UUID,
                username = "NewUsername",
                password = "NewPassword",
                jmsQueue = "NewJmsQueue",
            )

            val result: Team = withConstantNow(
                now = team.updatedAt.atZone(ZoneId.of(DEFAULT_ZONE_ID)).toOffsetDateTime(),
            ) {
                classUnderTest.save(
                    entry = team,
                    repetitionAttempts = ONE_REPETITION_ATTEMPT,
                )
            }

            result.shouldBeEqualToIgnoringFields(
                other = team,
                property = Team::createdAt,
            )
            result.createdAt.atZone(ZoneId.of(DEFAULT_ZONE_ID)).toInstant().shouldBeBetween(
                fromInstant = Instant.now().minusSeconds(FIVE_SECONDS),
                toInstant = Instant.now().plusSeconds(FIVE_SECONDS),
            )
        }
    }

    @Nested
    inner class GetTeamIdByType {
        @Test
        fun `should return null when team is not found by type`() = runBlocking<Unit> {
            checkAll(genA = Arb.enum<TeamType>()) {
                val result: UUID? = classUnderTest.getTeamIdByType(
                    type = it,
                )

                result.shouldNotBeNull()
            }
        }
    }

    @Nested
    inner class GetTeamIdByUsername {
        @Test
        fun `should return a team uuid when team is found by username`() = runBlocking<Unit> {
            val username = ExposedContainerObjectMother.TEAM_USERNAME

            val result: UUID? = classUnderTest.getTeamIdByUsername(username = username)

            result.shouldNotBeNull()
        }

        @Test
        fun `should return null when team is not found by username`() = runBlocking {
            val username = "unknownUsername"

            val result: UUID? = classUnderTest.getTeamIdByUsername(username = username)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class GetTeamIdByPassword {
        @Test
        fun `should return a team uuid when team is found by password`() = runBlocking<Unit> {
            val password = ExposedContainerObjectMother.TEAM_PASSWORD

            val result: UUID? = classUnderTest.getTeamIdByPassword(password = password)

            result.shouldNotBeNull()
        }

        @Test
        fun `should return null when team is not found by password`() = runBlocking {
            val password = "unknownPassword"

            val result: UUID? = classUnderTest.getTeamIdByPassword(password = password)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class GetTeamIdByJmsQueue {
        @Test
        fun `should return a team uuid when team is found by jmsQueue`() = runBlocking<Unit> {
            val jmsQueue = ExposedContainerObjectMother.TEAM_JMS_QUEUE

            val result: UUID? = classUnderTest.getTeamIdByJmsQueue(jmsQueue = jmsQueue)

            result.shouldNotBeNull()
        }

        @Test
        fun `should return null when team is not found by jmsQueue`() = runBlocking {
            val jmsQueue = "unknown"

            val result: UUID? = classUnderTest.getTeamIdByJmsQueue(jmsQueue = jmsQueue)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class ResetAllTeams {
        @Test
        fun `should reset all team statistics, hasPassed and passwords`() = runBlocking<Unit> {
            val result: List<UUID> = classUnderTest.resetAllTeams(
                repetitionAttempts = ONE_REPETITION_ATTEMPT,
            )

            result.shouldNotBeEmpty()
            classUnderTest.getAll(cursor = getFirstTeamCursorWithEmptyFields()).forAll {
                it.hasPassed.shouldBeFalse()
                it.password shouldHaveLength getKoin().getProperty<String>(key = PASSWORD_LENGTH)!!.toInt()
                it.statistics.shouldContainValues(ZERO_REQUESTS)
            }
        }
    }

    companion object {
        private const val FIVE_SECONDS = 5L
    }
}
