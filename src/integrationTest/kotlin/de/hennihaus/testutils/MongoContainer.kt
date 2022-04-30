package de.hennihaus.testutils

import de.hennihaus.models.Bank
import de.hennihaus.models.Group
import de.hennihaus.models.Task
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.koin.java.KoinJavaComponent.getKoin
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait

object MongoContainer {

    private const val IMAGE_NAME = "hennihaus/bamconfigdb"
    private const val IMAGE_VERSION = "latest"
    private const val MONGO_PORT = 27017

    const val DATABASE_NAME = "businessintegration"

    val INSTANCE by lazy { startMongoContainer() }

    private val state by lazy {
        mapOf(
            Group::class.simpleName to getEntries<Group>(),
            Bank::class.simpleName to getEntries<Bank>(),
            Task::class.simpleName to getEntries<Task>()
        )
    }

    fun resetState() {
        resetEntity<Group>()
        resetEntity<Bank>()
        resetEntity<Task>()
    }

    private fun startMongoContainer() = GenericContainer<Nothing>("$IMAGE_NAME:$IMAGE_VERSION").apply {
        withExposedPorts(MONGO_PORT)
        setWaitStrategy(Wait.forListeningPort())
        start()
    }

    private inline fun <reified T : Any> resetEntity() = runBlocking {
        val entries: List<Document> = state[T::class.simpleName]!!
        val col = getKoin().get<CoroutineDatabase>()
            .getCollection<Document>(collectionName = T::class.simpleName!!.lowercase())
        col.deleteMany(Document())
        col.insertMany(entries)
    }

    private inline fun <reified T> getEntries(): List<Document> = runBlocking {
        getKoin().get<CoroutineDatabase>().getCollection<Document>(collectionName = T::class.simpleName!!.lowercase())
            .find()
            .toList()
    }
}
