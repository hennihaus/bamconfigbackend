package de.hennihaus.repositories

import com.mongodb.client.model.UpdateOptions
import de.hennihaus.configurations.MongoConfiguration.ID_FIELD
import de.hennihaus.models.Bank
import de.hennihaus.models.Group
import de.hennihaus.plugins.NotFoundException
import de.hennihaus.services.BankServiceImpl
import de.hennihaus.utils.toObjectId
import org.koin.core.annotation.Single
import org.litote.kmongo.SetTo
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.aggregate
import org.litote.kmongo.coroutine.updateOne
import org.litote.kmongo.eq
import org.litote.kmongo.lookup
import org.litote.kmongo.match
import org.litote.kmongo.set

@Single
class BankRepository(private val db: CoroutineDatabase) : Repository<Bank, String> {

    override val col: CoroutineCollection<Bank>
        get() = db.getCollection()

    /**
     * db.bank.aggregate([
     *      {
     *          $match: {
     *              _id: "<ID>"
     *          }
     *      },
     *      {
     *          $lookup: {
     *              from: "group",
     *              localField: "groups",
     *              foreignField: "_id",
     *              as: "groups",
     *          }
     *      }
     * ]);
     */
    override suspend fun getById(id: String): Bank? {
        return col.aggregate<Bank>(
            match(
                filter = Bank::jmsQueue eq id
            ),
            lookup(
                from = Group::class.simpleName?.lowercase() as String,
                localField = Bank::groups.name,
                foreignField = ID_FIELD,
                newAs = Bank::groups.name
            )
        ).first()
    }

    /**
     * db.bank.aggregate([
     *      {
     *          $lookup: {
     *              from: "group",
     *              localField: "groups",
     *              foreignField: "_id",
     *              as: "groups",
     *          }
     *      }
     * ]);
     */
    override suspend fun getAll(): List<Bank> {
        return col.aggregate<Bank>(
            lookup(
                from = Group::class.simpleName?.lowercase() as String,
                localField = Bank::groups.name,
                foreignField = ID_FIELD,
                newAs = Bank::groups.name
            )
        ).toList()
    }

    /**
     * db.bank.updateOne(
     *      {
     *          _id: "<ID>"
     *      },
     *      {
     *          $set: {
     *              groups: [
     *                  ObjectId("<ID>"),
     *                  ObjectId("<ID>"),
     *                  ObjectId("<ID>"),
     *                  ObjectId("<ID>")
     *              ]
     *          }
     *      }
     * );
     */
    override suspend fun save(entry: Bank): Bank {
        col.updateOne(
            target = entry,
            options = UpdateOptions().upsert(true)
        )
        col.updateOne(
            filter = Bank::jmsQueue eq entry.jmsQueue,
            update = set(
                SetTo(
                    Bank::groups, entry.groups.map { group -> group.id.toString().toObjectId { it } }
                )
            )
        )
        return getById(id = entry.jmsQueue) ?: throw NotFoundException(
            message = BankServiceImpl.ID_MESSAGE
        )
    }
}
