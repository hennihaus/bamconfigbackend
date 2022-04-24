package de.hennihaus.repositories

import com.mongodb.client.model.UpdateOptions
import de.hennihaus.configurations.MongoConfiguration.GROUP_COLLECTION
import de.hennihaus.models.Group
import de.hennihaus.plugins.NotFoundException
import de.hennihaus.services.GroupServiceImpl
import de.hennihaus.utils.toObjectId
import org.bson.types.ObjectId
import org.koin.core.annotation.Single
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.updateOne
import org.litote.kmongo.eq

@Single
class GroupRepository(private val db: CoroutineDatabase) : Repository<Group, ObjectId> {

    override val col: CoroutineCollection<Group>
        get() = db.getCollection(GROUP_COLLECTION)

    override suspend fun save(entry: Group): Group {
        col.updateOne(
            target = entry,
            options = UpdateOptions().upsert(true)
        )
        return getById(id = entry.id.toString().toObjectId { it }) ?: throw NotFoundException(
            message = GroupServiceImpl.ID_MESSAGE
        )
    }

    suspend fun getGroupByUsername(username: String): Group? = col.findOne(
        Group::username eq username
    )

    suspend fun getGroupByPassword(password: String): Group? = col.findOne(
        Group::password eq password
    )

    suspend fun getGroupByJmsTopic(jmsTopic: String): Group? = col.findOne(
        Group::jmsTopic eq jmsTopic
    )
}
