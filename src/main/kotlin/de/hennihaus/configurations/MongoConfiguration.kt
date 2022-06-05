package de.hennihaus.configurations

import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mongoModule = module {

    single {
        val databaseName = getProperty<String>(MongoConfiguration.DATABASE_NAME)
        val databaseHost = getProperty<String>(MongoConfiguration.DATABASE_HOST)
        val databasePort = getProperty<String>(MongoConfiguration.DATABASE_PORT)

        KMongo.createClient("mongodb://$databaseHost:$databasePort/$databaseName").coroutine
    } withOptions {
        createdAtStart()
    }

    single {
        val databaseName = getProperty<String>(MongoConfiguration.DATABASE_NAME)

        get<CoroutineClient>().getDatabase(databaseName)
    } withOptions {
        createdAtStart()
    }
}

object MongoConfiguration {
    const val DATABASE_NAME = "ktor.mongodb.database"
    const val DATABASE_HOST = "ktor.mongodb.host"
    const val DATABASE_PORT = "ktor.mongodb.port"

    const val ID_FIELD = "_id"
}
