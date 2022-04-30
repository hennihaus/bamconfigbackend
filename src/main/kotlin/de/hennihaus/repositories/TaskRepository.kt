package de.hennihaus.repositories

import com.mongodb.client.model.UpdateOptions
import de.hennihaus.configurations.MongoConfiguration.ID_FIELD
import de.hennihaus.models.Bank
import de.hennihaus.models.Group
import de.hennihaus.models.Task
import de.hennihaus.plugins.NotFoundException
import de.hennihaus.services.TaskServiceImpl
import de.hennihaus.utils.toObjectId
import org.bson.types.ObjectId
import org.koin.core.annotation.Single
import org.litote.kmongo.MongoOperator.`in`
import org.litote.kmongo.SetTo
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.aggregate
import org.litote.kmongo.coroutine.updateOne
import org.litote.kmongo.eq
import org.litote.kmongo.expr
import org.litote.kmongo.from
import org.litote.kmongo.id.toId
import org.litote.kmongo.lookup
import org.litote.kmongo.match
import org.litote.kmongo.set
import org.litote.kmongo.variable
import org.litote.kmongo.variableDefinition

@Single
class TaskRepository(private val db: CoroutineDatabase) : Repository<Task, ObjectId> {

    override val col: CoroutineCollection<Task>
        get() = db.getCollection()

    /**
     * db.task.aggregate([
     *      {
     *          $match: {
     *              _id: "<ID>"
     *          }
     *      },
     *      {
     *          $lookup: {
     *              from: "bank",
     *              let: {
     *                  bank_ids: "$banks"
     *              },
     *              pipeline: [
     *                  {
     *                      $match: {
     *                          $expr: {
     *                              $in: [ "$_id", "$$bank_ids" ]
     *                          }
     *                      }
     *                  },
     *                  {
     *                      $lookup: {
     *                          from: "group",
     *                          localField: "groups",
     *                          foreignField: "_id",
     *                          as: "groups"
     *                      }
     *                  }
     *              ],
     *              as: "banks"
     *          }
     *      }
     * ]);
     */
    override suspend fun getById(id: ObjectId): Task? {
        return col.aggregate<Task>(
            match(
                filter = Task::id eq id.toId()
            ),
            lookup(
                from = Bank::class.simpleName?.lowercase() as String,
                let = listOf(
                    Task::banks.variableDefinition()
                ),
                resultProperty = Task::banks,
                pipeline = arrayOf(
                    match(
                        expr(
                            `in` from listOf(Bank::jmsTopic, Task::banks.variable)
                        )
                    ),
                    lookup(
                        from = Group::class.simpleName?.lowercase() as String,
                        localField = Bank::groups.name,
                        foreignField = ID_FIELD,
                        newAs = Bank::groups.name
                    )
                )
            )
        ).first()
    }

    /**
     * db.task.aggregate([
     *      {
     *          $lookup: {
     *              from: "bank",
     *              let: {
     *                  bank_ids: "$banks"
     *              },
     *              pipeline: [
     *                  {
     *                      $match: {
     *                          $expr: {
     *                              $in: [ "$_id", "$$bank_ids" ]
     *                          }
     *                      }
     *                  },
     *                  {
     *                      $lookup: {
     *                          from: "group",
     *                          localField: "groups",
     *                          foreignField: "_id",
     *                          as: "groups"
     *                      }
     *                  }
     *              ],
     *              as: "bank"
     *          }
     *      }
     * ]);
     */
    override suspend fun getAll(): List<Task> {
        return col.aggregate<Task>(
            lookup(
                from = Bank::class.simpleName as String,
                let = listOf(
                    Task::banks.variableDefinition()
                ),
                resultProperty = Task::banks,
                pipeline = arrayOf(
                    match(
                        expr(
                            `in` from listOf(Bank::jmsTopic, Task::banks.variable)
                        )
                    ),
                    lookup(
                        from = Group::class.simpleName?.lowercase() as String,
                        localField = Bank::groups.name,
                        foreignField = ID_FIELD,
                        newAs = Bank::groups.name
                    )
                )
            )
        ).toList()
    }

    /**
     * db.tasks.updateOne(
     *      {
     *          _id: ObjectId("<ID>")
     *      },
     *      {
     *          $set: {
     *              banks: [
     *                  "<BANK_ID/BANK_JMS_TOPIC>"
     *              ]
     *          }
     *      }
     * );
     */
    override suspend fun save(entry: Task): Task {
        col.updateOne(
            target = entry,
            options = UpdateOptions().upsert(true)
        )
        col.updateOne(
            filter = Task::id eq entry.id,
            update = set(
                SetTo(
                    Task::banks, entry.banks.map { bank -> bank.jmsTopic }
                )
            )
        )
        return getById(id = entry.id.toString().toObjectId { it }) ?: throw NotFoundException(
            message = TaskServiceImpl.ID_MESSAGE
        )
    }
}
