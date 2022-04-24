package de.hennihaus.services

import de.hennihaus.models.Group
import de.hennihaus.plugins.NotFoundException
import de.hennihaus.repositories.GroupRepository
import de.hennihaus.utils.toObjectId
import org.koin.core.annotation.Single
import org.litote.kmongo.id.toId

@Single
class GroupServiceImpl(private val repository: GroupRepository) : GroupService {

    override suspend fun getAllGroups(): List<Group> = repository.getAll().sortedBy { it.username }

    override suspend fun getGroupById(id: String): Group {
        return id.toObjectId {
            repository.getById(id = it) ?: throw NotFoundException(message = ID_MESSAGE)
        }
    }

    override suspend fun checkUsername(id: String, username: String): Boolean {
        return id.toObjectId { objectId ->
            repository.getGroupByUsername(username = username)?.let { it.id != objectId.toId<Group>() } ?: false
        }
    }

    override suspend fun checkPassword(id: String, password: String): Boolean {
        return id.toObjectId { objectId ->
            repository.getGroupByPassword(password = password)?.let { it.id != objectId.toId<Group>() } ?: false
        }
    }

    override suspend fun checkJmsTopic(id: String, jmsTopic: String): Boolean {
        return id.toObjectId { objectId ->
            repository.getGroupByJmsTopic(jmsTopic = jmsTopic)?.let { it.id != objectId.toId<Group>() } ?: false
        }
    }

    override suspend fun createGroup(group: Group): Group = repository.save(entry = group)

    override suspend fun updateGroup(group: Group): Group = repository.save(entry = group)

    override suspend fun deleteGroupById(id: String) {
        id.toObjectId { objectId ->
            objectId.takeIf { repository.deleteById(id = it) } ?: throw NotFoundException(message = ID_MESSAGE)
        }
    }

    override suspend fun resetStats(id: String): Group {
        return id.toObjectId { objectId ->
            repository.getById(id = objectId)
                ?.let { it.copy(stats = it.stats.mapValues { ZERO_REQUESTS }) }
                ?.also { repository.save(entry = it) }
                ?: throw NotFoundException(message = ID_MESSAGE)
        }
    }

    companion object {
        internal const val ID_MESSAGE = "No Group for given ID found!"
        private const val ZERO_REQUESTS = 0
    }
}
