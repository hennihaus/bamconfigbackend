package de.hennihaus.repositories

import org.jetbrains.exposed.sql.transactions.TransactionManager

interface Repository<T : Any, ID : Any> {

    suspend fun getById(id: ID): T?

    suspend fun getAll(): List<T>

    suspend fun deleteById(id: ID): Boolean

    suspend fun save(entry: T, repetitionAttempts: Int = TransactionManager.manager.defaultRepetitionAttempts): T
}
