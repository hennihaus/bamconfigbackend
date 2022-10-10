package de.hennihaus.routes.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

object TeamPaths {
    const val TEAMS_PATH = "/teams"
    const val ID_PATH = "/{id}"

    const val USERNAME_UNIQUE_PATH = "/unique/username/{username}"
    const val PASSWORD_UNIQUE_PATH = "/unique/password/{password}"
    const val JMS_QUEUE_UNIQUE_PATH = "/unique/jmsQueue/{jmsQueue}"
    const val RESET_ALL_STATISTICS_PATH = "/statistics"
}

@Serializable
@Resource(TeamPaths.TEAMS_PATH)
class Teams {

    @Serializable
    @Resource(TeamPaths.ID_PATH)
    data class Id(val parent: Teams = Teams(), val id: String) {

        @Serializable
        @Resource(TeamPaths.USERNAME_UNIQUE_PATH)
        data class UniqueUsername(val parent: Id, val username: String)

        @Serializable
        @Resource(TeamPaths.PASSWORD_UNIQUE_PATH)
        data class UniquePassword(val parent: Id, val password: String)

        @Serializable
        @Resource(TeamPaths.JMS_QUEUE_UNIQUE_PATH)
        data class UniqueJmsQueue(val parent: Id, val jmsQueue: String)

        @Serializable
        @Resource(TeamPaths.RESET_ALL_STATISTICS_PATH)
        data class ResetStatistics(val parent: Id)
    }
}
