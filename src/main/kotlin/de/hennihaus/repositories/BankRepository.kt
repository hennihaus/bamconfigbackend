package de.hennihaus.repositories

import com.mongodb.client.model.UpdateOptions
import de.hennihaus.configurations.MongoConfiguration.BANK_COLLECTION
import de.hennihaus.configurations.MongoConfiguration.GROUP_COLLECTION
import de.hennihaus.configurations.MongoConfiguration.ID_FIELD
import de.hennihaus.models.Bank
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
        get() = db.getCollection(BANK_COLLECTION)

    /**
     * db.banks.aggregate([
     *      {
     *          $match: {
     *              _id: "<ID>"
     *          }
     *      },
     *      {
     *          $lookup: {
     *              from: "groups",
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
                filter = Bank::jmsTopic eq id
            ),
            lookup(
                from = GROUP_COLLECTION,
                localField = GROUP_COLLECTION,
                foreignField = ID_FIELD,
                newAs = GROUP_COLLECTION
            )
        ).first()
    }

    /**
     * db.banks.aggregate([
     *      {
     *          $lookup: {
     *              from: "groups",
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
                from = GROUP_COLLECTION,
                localField = GROUP_COLLECTION,
                foreignField = ID_FIELD,
                newAs = GROUP_COLLECTION
            )
        ).toList()
    }

    /**
     * db.banks.updateOne(
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
            filter = Bank::jmsTopic eq entry.jmsTopic,
            update = set(
                SetTo(
                    Bank::groups, entry.groups.map { group -> group.id.toString().toObjectId { it } }
                )
            )
        )
        return getById(id = entry.jmsTopic) ?: throw NotFoundException(
            message = BankServiceImpl.ID_MESSAGE
        )
    }
}
