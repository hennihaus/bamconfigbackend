package de.hennihaus.repositories

import de.hennihaus.configurations.ExposedConfiguration.DATABASE_HOST
import de.hennihaus.configurations.ExposedConfiguration.DATABASE_PORT
import de.hennihaus.models.generated.Team
import de.hennihaus.objectmothers.BankObjectMother.SCHUFA_BANK_NAME
import de.hennihaus.objectmothers.BankObjectMother.SYNC_BANK_NAME
import de.hennihaus.objectmothers.ExposedContainerObjectMother
import de.hennihaus.objectmothers.ExposedContainerObjectMother.PSD_BANK_NAME
import de.hennihaus.objectmothers.StudentObjectMother.getFirstStudent
import de.hennihaus.objectmothers.StudentObjectMother.getSecondStudent
import de.hennihaus.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.plugins.initKoin
import de.hennihaus.testutils.containers.ExposedContainer
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.maps.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
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
        fun `should return at least one team`() = runBlocking<Unit> {
            val result: List<Team> = classUnderTest.getAll()

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
                    PSD_BANK_NAME to 1L,
                )
            )

            val result: Team = classUnderTest.save(entry = team)

            result shouldBe team
        }

        @Test
        fun `should save a team when no existing team is in db`() = runBlocking {
            val team = getFirstTeam(
                uuid = ExposedContainerObjectMother.UNKNOWN_UUID,
                username = "NewUsername",
                password = "NewPassword",
                jmsQueue = "NewJmsQueue",
            )

            val result: Team = classUnderTest.save(entry = team)

            result shouldBe team
        }
    }

    @Nested
    inner class GetTeamByUsername {
        @Test
        fun `should return a team when team is found by username`() = runBlocking {
            val username = ExposedContainerObjectMother.TEAM_USERNAME

            val result: Team? = classUnderTest.getTeamByUsername(username = username)

            result should beInstanceOf<Team>()
        }

        @Test
        fun `should return null when team is not found by username`() = runBlocking {
            val username = "unknownUsername"

            val result: Team? = classUnderTest.getTeamByUsername(username = username)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class GetTeamByPassword {
        @Test
        fun `should return a team when team is found by password`() = runBlocking {
            val password = ExposedContainerObjectMother.TEAM_PASSWORD

            val result: Team? = classUnderTest.getTeamByPassword(password = password)

            result should beInstanceOf<Team>()
        }

        @Test
        fun `should return null when team is not found by password`() = runBlocking {
            val password = "unknownPassword"

            val result: Team? = classUnderTest.getTeamByPassword(password = password)

            result.shouldBeNull()
        }
    }

    @Nested
    inner class GetTeamByJmsQueue {
        @Test
        fun `should return a team when team is found by jmsQueue`() = runBlocking {
            val jmsQueue = ExposedContainerObjectMother.TEAM_JMS_QUEUE

            val result: Team? = classUnderTest.getTeamByJmsQueue(jmsQueue = jmsQueue)

            result should beInstanceOf<Team>()
        }

        @Test
        fun `should return null when team is not found by jmsQueue`() = runBlocking {
            val jmsQueue = "unknown"

            val result: Team? = classUnderTest.getTeamByJmsQueue(jmsQueue = jmsQueue)

            result.shouldBeNull()
        }
    }
}
