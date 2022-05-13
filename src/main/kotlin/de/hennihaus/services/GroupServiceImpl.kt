package de.hennihaus.services

import de.hennihaus.configurations.Configuration.PASSWORD_LENGTH
import de.hennihaus.models.Group
import de.hennihaus.plugins.NotFoundException
import de.hennihaus.repositories.GroupRepository
import de.hennihaus.utils.toObjectId
import org.koin.core.annotation.Property
import org.koin.core.annotation.Single
import org.litote.kmongo.id.toId
import org.passay.CharacterRule
import org.passay.EnglishCharacterData
import org.passay.PasswordGenerator

@Single
class GroupServiceImpl(
    private val repository: GroupRepository,
    private val stats: StatsService,
    @Property(PASSWORD_LENGTH) private val passwordLength: String
) : GroupService {

    override suspend fun getAllGroups(): List<Group> = repository.getAll()
        .sortedBy { it.username }
        .map { stats.setHasPassed(group = it) }

    override suspend fun getGroupById(id: String): Group {
        val group = id.toObjectId {
            repository.getById(id = it) ?: throw NotFoundException(message = ID_MESSAGE)
        }
        return stats.setHasPassed(group = group)
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

    override suspend fun saveGroup(group: Group): Group = stats.setHasPassed(group = group).let {
        repository.save(entry = it)
    }

    override suspend fun deleteGroupById(id: String) {
        id.toObjectId { objectId ->
            objectId.takeIf { repository.deleteById(id = it) } ?: throw NotFoundException(message = ID_MESSAGE)
        }
    }

    override suspend fun resetAllGroups(): List<Group> {
        return repository.getAll()
            .map { resetGroup(group = it) }
            .map { repository.save(entry = it) }
    }

    override suspend fun resetStats(id: String): Group {
        return id.toObjectId { objectId ->
            repository.getById(id = objectId)
                ?.let { it.copy(stats = it.stats.mapValues { ZERO_REQUESTS }) }
                ?.let { stats.setHasPassed(group = it) }
                ?.also { repository.save(entry = it) }
                ?: throw NotFoundException(message = ID_MESSAGE)
        }
    }

    private fun resetGroup(group: Group): Group = group.let {
        it.copy(
            stats = it.stats.mapValues { ZERO_REQUESTS },
            hasPassed = false,
            password = PasswordGenerator().generatePassword(
                passwordLength.toInt(),
                CharacterRule(EnglishCharacterData.Alphabetical)
            )
        )
    }

    companion object {
        internal const val ID_MESSAGE = "No Group for given ID found!"
        private const val ZERO_REQUESTS = 0
    }
}
