package de.hennihaus.services

import de.hennihaus.bamdatamodel.Team
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getSecondTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getThirdTeam
import de.hennihaus.models.cursors.TeamPagination
import de.hennihaus.objectmothers.CursorObjectMother.EMPTY_CURSOR
import de.hennihaus.objectmothers.CursorObjectMother.getFirstTeamCursorWithEmptyFields
import de.hennihaus.objectmothers.CursorObjectMother.getLastTeamCursorWithEmptyFields
import de.hennihaus.objectmothers.CursorObjectMother.getNextTeamCursorWithEmptyFields
import de.hennihaus.objectmothers.CursorObjectMother.getPreviousTeamCursorWithEmptyFields
import de.hennihaus.objectmothers.PaginationObjectMother.getTeamPaginationWithEmptyFields
import de.hennihaus.objectmothers.TeamQueryObjectMother.DEFAULT_MIN_LIMIT
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CursorServiceTest {

    private val classUnderTest = CursorService()

    @Nested
    inner class BuildPagination {
        @Test
        fun `should build correct pagination with first cursor and items are greater limit`() {
            val cursor = getFirstTeamCursorWithEmptyFields()
            val positionSupplier = { team: Team -> team.username }
            val positionFallback = EMPTY_CURSOR
            val items = listOf(getFirstTeam(), getSecondTeam())
            val limit = DEFAULT_MIN_LIMIT

            val result: TeamPagination = classUnderTest.buildPagination(
                cursor = cursor,
                positionSupplier = positionSupplier,
                positionFallback = positionFallback,
                items = items,
                limit = limit,
            )

            result shouldBe getTeamPaginationWithEmptyFields(
                prev = null,
                next = getNextTeamCursorWithEmptyFields(
                    position = getFirstTeam().username,
                ),
                items = listOf(
                    getFirstTeam(),
                ),
            )
        }

        @Test
        fun `should build correct pagination with first cursor and items are empty`() {
            val cursor = getFirstTeamCursorWithEmptyFields()
            val positionSupplier = { team: Team -> team.username }
            val positionFallback = EMPTY_CURSOR
            val items = emptyList<Team>()
            val limit = DEFAULT_MIN_LIMIT

            val result: TeamPagination = classUnderTest.buildPagination(
                cursor = cursor,
                positionSupplier = positionSupplier,
                positionFallback = positionFallback,
                items = items,
                limit = limit,
            )

            result shouldBe getTeamPaginationWithEmptyFields(
                prev = null,
                next = null,
                items = emptyList(),
            )
        }

        @Test
        fun `should build correct pagination with first cursor and items are equal limit`() {
            val cursor = getFirstTeamCursorWithEmptyFields()
            val positionSupplier = { team: Team -> team.username }
            val positionFallback = EMPTY_CURSOR
            val items = listOf(getFirstTeam())
            val limit = DEFAULT_MIN_LIMIT

            val result: TeamPagination = classUnderTest.buildPagination(
                cursor = cursor,
                positionSupplier = positionSupplier,
                positionFallback = positionFallback,
                items = items,
                limit = limit,
            )

            result shouldBe getTeamPaginationWithEmptyFields(
                prev = null,
                next = null,
                items = listOf(
                    getFirstTeam(),
                ),
            )
        }

        @Test
        fun `should build correct pagination with last cursor and items are greater limit`() {
            val cursor = getLastTeamCursorWithEmptyFields()
            val positionSupplier = { team: Team -> team.username }
            val positionFallback = EMPTY_CURSOR
            val items = listOf(getSecondTeam(), getThirdTeam())
            val limit = DEFAULT_MIN_LIMIT

            val result: TeamPagination = classUnderTest.buildPagination(
                cursor = cursor,
                positionSupplier = positionSupplier,
                positionFallback = positionFallback,
                items = items,
                limit = limit,
            )

            result shouldBe getTeamPaginationWithEmptyFields(
                prev = getPreviousTeamCursorWithEmptyFields(
                    position = getThirdTeam().username,
                ),
                next = null,
                items = listOf(
                    getThirdTeam(),
                ),
            )
        }

        @Test
        fun `should build correct pagination with last cursor and items are empty`() {
            val cursor = getLastTeamCursorWithEmptyFields()
            val positionSupplier = { team: Team -> team.username }
            val positionFallback = EMPTY_CURSOR
            val items = emptyList<Team>()
            val limit = DEFAULT_MIN_LIMIT

            val result: TeamPagination = classUnderTest.buildPagination(
                cursor = cursor,
                positionSupplier = positionSupplier,
                positionFallback = positionFallback,
                items = items,
                limit = limit,
            )

            result shouldBe getTeamPaginationWithEmptyFields(
                prev = null,
                next = null,
                items = emptyList(),
            )
        }

        @Test
        fun `should build correct pagination with last cursor and items are equal limit`() {
            val cursor = getLastTeamCursorWithEmptyFields()
            val positionSupplier = { team: Team -> team.username }
            val positionFallback = EMPTY_CURSOR
            val items = listOf(getThirdTeam())
            val limit = DEFAULT_MIN_LIMIT

            val result: TeamPagination = classUnderTest.buildPagination(
                cursor = cursor,
                positionSupplier = positionSupplier,
                positionFallback = positionFallback,
                items = items,
                limit = limit,
            )

            result shouldBe getTeamPaginationWithEmptyFields(
                prev = null,
                next = null,
                items = listOf(
                    getThirdTeam(),
                ),
            )
        }

        @Test
        fun `should build correct pagination with previous cursor and items are greater limit`() {
            val cursor = getPreviousTeamCursorWithEmptyFields()
            val positionSupplier = { team: Team -> team.username }
            val positionFallback = EMPTY_CURSOR
            val items = listOf(getFirstTeam(), getSecondTeam())
            val limit = DEFAULT_MIN_LIMIT

            val result: TeamPagination = classUnderTest.buildPagination(
                cursor = cursor,
                positionSupplier = positionSupplier,
                positionFallback = positionFallback,
                items = items,
                limit = limit,
            )

            result shouldBe getTeamPaginationWithEmptyFields(
                prev = getPreviousTeamCursorWithEmptyFields(
                    position = getSecondTeam().username,
                ),
                next = getNextTeamCursorWithEmptyFields(
                    position = getSecondTeam().username,
                ),
                items = listOf(
                    getSecondTeam(),
                ),
            )
        }

        @Test
        fun `should build correct pagination with previous cursor and items are empty`() {
            val cursor = getPreviousTeamCursorWithEmptyFields()
            val positionSupplier = { team: Team -> team.username }
            val positionFallback = EMPTY_CURSOR
            val items = emptyList<Team>()
            val limit = DEFAULT_MIN_LIMIT

            val result: TeamPagination = classUnderTest.buildPagination(
                cursor = cursor,
                positionSupplier = positionSupplier,
                positionFallback = positionFallback,
                items = items,
                limit = limit,
            )

            result shouldBe getTeamPaginationWithEmptyFields(
                prev = null,
                next = null,
                items = emptyList(),
            )
        }

        @Test
        fun `should build correct pagination with previous cursor and items are equal limit`() {
            val cursor = getPreviousTeamCursorWithEmptyFields()
            val positionSupplier = { team: Team -> team.username }
            val positionFallback = EMPTY_CURSOR
            val items = listOf(getSecondTeam())
            val limit = DEFAULT_MIN_LIMIT

            val result: TeamPagination = classUnderTest.buildPagination(
                cursor = cursor,
                positionSupplier = positionSupplier,
                positionFallback = positionFallback,
                items = items,
                limit = limit,
            )

            result shouldBe getTeamPaginationWithEmptyFields(
                prev = null,
                next = getNextTeamCursorWithEmptyFields(
                    position = getSecondTeam().username,
                ),
                items = listOf(
                    getSecondTeam(),
                ),
            )
        }

        @Test
        fun `should build correct pagination with next cursor and items are greater limit`() {
            val cursor = getNextTeamCursorWithEmptyFields()
            val positionSupplier = { team: Team -> team.username }
            val positionFallback = EMPTY_CURSOR
            val items = listOf(getSecondTeam(), getThirdTeam())
            val limit = DEFAULT_MIN_LIMIT

            val result: TeamPagination = classUnderTest.buildPagination(
                cursor = cursor,
                positionSupplier = positionSupplier,
                positionFallback = positionFallback,
                items = items,
                limit = limit,
            )

            result shouldBe getTeamPaginationWithEmptyFields(
                prev = getPreviousTeamCursorWithEmptyFields(
                    position = getSecondTeam().username,
                ),
                next = getNextTeamCursorWithEmptyFields(
                    position = getSecondTeam().username,
                ),
                items = listOf(
                    getSecondTeam(),
                ),
            )
        }

        @Test
        fun `should build correct pagination with next cursor and items are empty`() {
            val cursor = getNextTeamCursorWithEmptyFields()
            val positionSupplier = { team: Team -> team.username }
            val positionFallback = EMPTY_CURSOR
            val items = emptyList<Team>()
            val limit = DEFAULT_MIN_LIMIT

            val result: TeamPagination = classUnderTest.buildPagination(
                cursor = cursor,
                positionSupplier = positionSupplier,
                positionFallback = positionFallback,
                items = items,
                limit = limit,
            )

            result shouldBe getTeamPaginationWithEmptyFields(
                prev = null,
                next = null,
                items = emptyList(),
            )
        }

        @Test
        fun `should build correct pagination with next cursor and items are equal limit`() {
            val cursor = getNextTeamCursorWithEmptyFields()
            val positionSupplier = { team: Team -> team.username }
            val positionFallback = EMPTY_CURSOR
            val items = listOf(getSecondTeam())
            val limit = DEFAULT_MIN_LIMIT

            val result: TeamPagination = classUnderTest.buildPagination(
                cursor = cursor,
                positionSupplier = positionSupplier,
                positionFallback = positionFallback,
                items = items,
                limit = limit,
            )

            result shouldBe getTeamPaginationWithEmptyFields(
                prev = getPreviousTeamCursorWithEmptyFields(
                    position = getSecondTeam().username,
                ),
                next = null,
                items = listOf(
                    getSecondTeam(),
                ),
            )
        }
    }
}
