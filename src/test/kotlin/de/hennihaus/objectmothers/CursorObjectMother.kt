package de.hennihaus.objectmothers

import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.SECOND_TEAM_USERNAME
import de.hennihaus.models.cursors.Direction
import de.hennihaus.models.cursors.TeamCursor
import de.hennihaus.models.cursors.TeamQuery
import de.hennihaus.objectmothers.TeamQueryObjectMother.getTeamQueryWithEmptyFields
import de.hennihaus.objectmothers.TeamQueryObjectMother.getTeamQueryWithNoEmptyFields

object CursorObjectMother {

    const val ASCENDING_DIRECTION = "ASCENDING"
    const val DESCENDING_DIRECTION = "DESCENDING"

    const val EMPTY_CURSOR = ""

    fun getFirstTeamCursorWithNoEmptyFields(
        position: String = EMPTY_CURSOR,
        direction: Direction = Direction.valueOf(value = ASCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithNoEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getFirstTeamCursorWithEmptyFields(
        position: String = EMPTY_CURSOR,
        direction: Direction = Direction.valueOf(value = ASCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getPreviousTeamCursorWithNoEmptyFields(
        position: String = SECOND_TEAM_USERNAME,
        direction: Direction = Direction.valueOf(value = DESCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithNoEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getPreviousTeamCursorWithEmptyFields(
        position: String = SECOND_TEAM_USERNAME,
        direction: Direction = Direction.valueOf(value = DESCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getNextTeamCursorWithNoEmptyFields(
        position: String = SECOND_TEAM_USERNAME,
        direction: Direction = Direction.valueOf(value = ASCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithNoEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getNextTeamCursorWithEmptyFields(
        position: String = SECOND_TEAM_USERNAME,
        direction: Direction = Direction.valueOf(value = ASCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getLastTeamCursorWithNoEmptyFields(
        position: String = EMPTY_CURSOR,
        direction: Direction = Direction.valueOf(value = DESCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithNoEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )

    fun getLastTeamCursorWithEmptyFields(
        position: String = EMPTY_CURSOR,
        direction: Direction = Direction.valueOf(value = DESCENDING_DIRECTION),
        query: TeamQuery = getTeamQueryWithEmptyFields(),
    ) = TeamCursor(
        position = position,
        direction = direction,
        query = query,
    )
}
