package de.hennihaus.objectmothers

import de.hennihaus.bamdatamodel.Team
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getFirstTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getSecondTeam
import de.hennihaus.bamdatamodel.objectmothers.TeamObjectMother.getThirdTeam
import de.hennihaus.models.cursors.TeamCursor
import de.hennihaus.models.cursors.TeamPagination
import de.hennihaus.models.cursors.TeamQuery
import de.hennihaus.models.generated.rest.PaginationDTO
import de.hennihaus.models.generated.rest.QueryDTO
import de.hennihaus.models.generated.rest.TeamDTO
import de.hennihaus.objectmothers.CursorObjectMother.FIRST_TEAM_CURSOR_WITH_EMPTY_FIELDS
import de.hennihaus.objectmothers.CursorObjectMother.FIRST_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS
import de.hennihaus.objectmothers.CursorObjectMother.LAST_TEAM_CURSOR_WITH_EMPTY_FIELDS
import de.hennihaus.objectmothers.CursorObjectMother.LAST_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS
import de.hennihaus.objectmothers.CursorObjectMother.NEXT_TEAM_CURSOR_WITH_EMPTY_FIELDS
import de.hennihaus.objectmothers.CursorObjectMother.NEXT_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS
import de.hennihaus.objectmothers.CursorObjectMother.PREVIOUS_TEAM_CURSOR_WITH_EMPTY_FIELDS
import de.hennihaus.objectmothers.CursorObjectMother.PREVIOUS_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS
import de.hennihaus.objectmothers.CursorObjectMother.getFirstTeamCursorWithEmptyFields
import de.hennihaus.objectmothers.CursorObjectMother.getFirstTeamCursorWithNoEmptyFields
import de.hennihaus.objectmothers.CursorObjectMother.getLastTeamCursorWithEmptyFields
import de.hennihaus.objectmothers.CursorObjectMother.getLastTeamCursorWithNoEmptyFields
import de.hennihaus.objectmothers.CursorObjectMother.getNextTeamCursorWithEmptyFields
import de.hennihaus.objectmothers.CursorObjectMother.getNextTeamCursorWithNoEmptyFields
import de.hennihaus.objectmothers.CursorObjectMother.getPreviousTeamCursorWithEmptyFields
import de.hennihaus.objectmothers.CursorObjectMother.getPreviousTeamCursorWithNoEmptyFields
import de.hennihaus.objectmothers.TeamQueryObjectMother.getTeamQueryWithEmptyFields
import de.hennihaus.objectmothers.TeamQueryObjectMother.getTeamQueryWithNoEmptyFields
import de.hennihaus.routes.mappers.toTeamDTO
import de.hennihaus.routes.mappers.toTeamQueryDTO
import de.hennihaus.models.generated.rest.TeamsDTO as TeamPaginationDTO

object PaginationObjectMother {

    fun getTeamPaginationWithNoEmptyFields(
        first: TeamCursor = getFirstTeamCursorWithNoEmptyFields(),
        prev: TeamCursor? = getPreviousTeamCursorWithNoEmptyFields(),
        next: TeamCursor? = getNextTeamCursorWithNoEmptyFields(),
        last: TeamCursor = getLastTeamCursorWithNoEmptyFields(),
        query: TeamQuery = getTeamQueryWithNoEmptyFields(),
        items: List<Team> = getTeamItems(),
    ) = TeamPagination(
        first = first,
        prev = prev,
        next = next,
        last = last,
        query = query,
        items = items,
    )

    fun getTeamPaginationWithEmptyFields(
        first: TeamCursor = getFirstTeamCursorWithEmptyFields(),
        prev: TeamCursor? = getPreviousTeamCursorWithEmptyFields(),
        next: TeamCursor? = getNextTeamCursorWithEmptyFields(),
        last: TeamCursor = getLastTeamCursorWithEmptyFields(),
        query: TeamQuery = getTeamQueryWithEmptyFields(),
        items: List<Team> = getTeamItems(),
    ) = TeamPagination(
        first = first,
        prev = prev,
        next = next,
        last = last,
        query = query,
        items = items,
    )

    fun getTeamPaginationDTOWithNoEmptyFields(
        pagination: PaginationDTO = getPaginationDTOWithNoEmptyFields(),
        query: QueryDTO = getTeamQueryWithNoEmptyFields().toTeamQueryDTO(),
        items: List<TeamDTO> = getTeamItems().map { it.toTeamDTO() },
    ) = TeamPaginationDTO(
        pagination = pagination,
        query = query,
        items = items,
    )

    fun getTeamPaginationDTOWithEmptyFields(
        pagination: PaginationDTO = getPaginationDTOWithEmptyFields(),
        query: QueryDTO = getTeamQueryWithEmptyFields().toTeamQueryDTO(),
        items: List<TeamDTO> = getTeamItems().map { it.toTeamDTO() },
    ) = TeamPaginationDTO(
        pagination = pagination,
        query = query,
        items = items,
    )

    fun getPaginationDTOWithNoEmptyFields(
        first: String = FIRST_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS,
        prev: String? = PREVIOUS_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS,
        next: String? = NEXT_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS,
        last: String = LAST_TEAM_CURSOR_WITH_NO_EMPTY_FIELDS,
    ) = PaginationDTO(
        first = first,
        prev = prev,
        next = next,
        last = last,
    )

    fun getPaginationDTOWithEmptyFields(
        first: String = FIRST_TEAM_CURSOR_WITH_EMPTY_FIELDS,
        prev: String? = PREVIOUS_TEAM_CURSOR_WITH_EMPTY_FIELDS,
        next: String? = NEXT_TEAM_CURSOR_WITH_EMPTY_FIELDS,
        last: String = LAST_TEAM_CURSOR_WITH_EMPTY_FIELDS,
    ) = PaginationDTO(
        first = first,
        prev = prev,
        next = next,
        last = last,
    )

    private fun getTeamItems() = listOf(
        getFirstTeam(),
        getSecondTeam(),
        getThirdTeam(),
    )
}
