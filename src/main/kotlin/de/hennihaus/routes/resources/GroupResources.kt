package de.hennihaus.routes.resources

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

object GroupPaths {
    const val GROUPS_PATH = "/groups"
    const val ID_PATH = "/{id}"
    const val USERNAME_EXISTS_PATH = "/{username}/username"
    const val PASSWORD_EXISTS_PATH = "/{password}/password"
    const val JMS_QUEUE_EXISTS_PATH = "/{jmsQueue}/jmsQueue"
    const val RESET_ALL_STATS_PATH = "/stats"
}

@Serializable
@Resource(GroupPaths.GROUPS_PATH)
class Groups {

    @Serializable
    @Resource(GroupPaths.ID_PATH)
    data class Id(val parent: Groups = Groups(), val id: String) {

        @Serializable
        @Resource(GroupPaths.USERNAME_EXISTS_PATH)
        data class CheckUsername(val parent: Id, val username: String)

        @Serializable
        @Resource(GroupPaths.PASSWORD_EXISTS_PATH)
        data class CheckPassword(val parent: Id, val password: String)

        @Serializable
        @Resource(GroupPaths.JMS_QUEUE_EXISTS_PATH)
        data class CheckJmsQueue(val parent: Id, val jmsQueue: String)

        @Serializable
        @Resource(GroupPaths.RESET_ALL_STATS_PATH)
        data class ResetStats(val parent: Id)
    }
}
