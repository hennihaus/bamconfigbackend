package de.hennihaus.repositories.entities

import de.hennihaus.repositories.tables.BankTable
import de.hennihaus.repositories.tables.CreditConfigurationTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.time.Instant
import java.util.UUID

class BankEntity(uuid: EntityID<UUID>) : UUIDEntity(id = uuid) {
    val name: String by BankTable.name
    val jmsQueue: String by BankTable.jmsQueue
    val thumbnailUrl: String by BankTable.thumbnailUrl
    val isAsync: Boolean by BankTable.isAsync
    val isActive: Boolean by BankTable.isActive
    val creditConfiguration: CreditConfigurationEntity? by CreditConfigurationEntity.optionalBackReferencedOn(
        column = CreditConfigurationTable.bankId,
    )
    val updatedAt: Instant by BankTable.updated

    companion object : UUIDEntityClass<BankEntity>(BankTable)
}

class CreditConfigurationEntity(uuid: EntityID<UUID>) : UUIDEntity(id = uuid) {
    val minAmountInEuros: Int by CreditConfigurationTable.minAmountInEuros
    val maxAmountInEuros: Int by CreditConfigurationTable.maxAmountInEuros
    val minTermInMonths: Int by CreditConfigurationTable.minTermInMonths
    val maxTermInMonths: Int by CreditConfigurationTable.maxTermInMonths
    val minSchufaRating: String by CreditConfigurationTable.minSchufaRating
    val maxSchufaRating: String by CreditConfigurationTable.maxSchufaRating

    companion object : UUIDEntityClass<CreditConfigurationEntity>(CreditConfigurationTable)
}
