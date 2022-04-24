package de.hennihaus.repositories

import org.litote.kmongo.coroutine.CoroutineCollection

interface Repository<T : Any, ID : Any> {

    val col: CoroutineCollection<T>

    suspend fun getById(id: ID): T? = col.findOneById(id = id as Any)

    suspend fun getAll(): List<T> = col.find().toList()

    suspend fun save(entry: T): T

    suspend fun deleteById(id: ID): Boolean = col.deleteOneById(id = id).deletedCount == ONE_DELETED_ENTITY

    companion object {
        private const val ONE_DELETED_ENTITY = 1L
    }
}
