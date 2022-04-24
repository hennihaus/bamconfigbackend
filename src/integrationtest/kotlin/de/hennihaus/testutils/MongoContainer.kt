package de.hennihaus.testutils

import de.hennihaus.configurations.MongoConfiguration.BANK_COLLECTION
import de.hennihaus.configurations.MongoConfiguration.GROUP_COLLECTION
import de.hennihaus.configurations.MongoConfiguration.TASK_COLLECTION
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

    private const val IMAGE_NAME = "hfubusinessintegration/configdb"
    private const val IMAGE_VERSION = "latest"
    private const val MONGO_PORT = 27017

    const val DATABASE_NAME = "businessintegration"

    val INSTANCE by lazy { startMongoContainer() }

    private val state by lazy {
        mapOf(
            Group::class.simpleName to getEntries(collectionName = GROUP_COLLECTION),
            Bank::class.simpleName to getEntries(collectionName = BANK_COLLECTION),
            Task::class.simpleName to getEntries(collectionName = TASK_COLLECTION)
        )
    }

    fun resetState() {
        resetEntity<Group>(collectionName = GROUP_COLLECTION)
        resetEntity<Bank>(collectionName = BANK_COLLECTION)
        resetEntity<Task>(collectionName = TASK_COLLECTION)
    }

    private fun startMongoContainer() = GenericContainer<Nothing>("$IMAGE_NAME:$IMAGE_VERSION").apply {
        withExposedPorts(MONGO_PORT)
        setWaitStrategy(Wait.forListeningPort())
        start()
    }

    private inline fun <reified T : Any> resetEntity(collectionName: String) = runBlocking {
        val entries: List<Document> = state[T::class.simpleName]!!
        val col = getKoin().get<CoroutineDatabase>().getCollection<Document>(collectionName = collectionName)
        col.deleteMany(Document())
        col.insertMany(entries)
    }

    private fun getEntries(collectionName: String): List<Document> = runBlocking {
        getKoin().get<CoroutineDatabase>().getCollection<Document>(collectionName = collectionName)
            .find()
            .toList()
    }
}
