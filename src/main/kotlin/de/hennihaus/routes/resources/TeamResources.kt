package de.hennihaus.routes.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

object TeamPaths {
    const val TEAMS_PATH = "/teams"
    const val ID_PATH = "/{id}"

    const val USERNAME_EXISTS_PATH = "/check/username/{username}"
    const val PASSWORD_EXISTS_PATH = "/check/password/{password}"
    const val JMS_QUEUE_EXISTS_PATH = "/check/jmsQueue/{jmsQueue}"
    const val RESET_ALL_STATISTICS_PATH = "/statistics"
}

@Serializable
@Resource(TeamPaths.TEAMS_PATH)
class Teams {

    @Serializable
    @Resource(TeamPaths.ID_PATH)
    data class Id(val parent: Teams = Teams(), val id: String) {

        @Serializable
        @Resource(TeamPaths.USERNAME_EXISTS_PATH)
        data class CheckUsername(val parent: Id, val username: String)

        @Serializable
        @Resource(TeamPaths.PASSWORD_EXISTS_PATH)
        data class CheckPassword(val parent: Id, val password: String)

        @Serializable
        @Resource(TeamPaths.JMS_QUEUE_EXISTS_PATH)
        data class CheckJmsQueue(val parent: Id, val jmsQueue: String)

        @Serializable
        @Resource(TeamPaths.RESET_ALL_STATISTICS_PATH)
        data class ResetStatistics(val parent: Id)
    }
}
