package de.hennihaus.models.cursors

import de.hennihaus.bamdatamodel.Team
import de.hennihaus.bamdatamodel.TeamType
import de.hennihaus.models.Pagination
import java.io.Serializable

typealias TeamCursor = Cursor<TeamQuery>

typealias TeamPagination = Pagination<TeamQuery, Team>

data class TeamQuery(
    val limit: Int,
    val type: TeamType?,
    val username: String?,
    val password: String?,
    val jmsQueue: String?,
    val hasPassed: Boolean?,
    val minRequests: Long?,
    val maxRequests: Long?,
    val studentFirstname: String?,
    val studentLastname: String?,
    val banks: List<String>?,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 8868291383640594341L
    }
}
