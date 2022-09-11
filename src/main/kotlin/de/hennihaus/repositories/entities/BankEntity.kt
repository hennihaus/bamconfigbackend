package de.hennihaus.repositories.entities

import de.hennihaus.repositories.tables.BankTable
import de.hennihaus.repositories.tables.CreditConfigurationTable
import de.hennihaus.repositories.tables.StatisticTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import java.util.UUID

class BankEntity(uuid: EntityID<UUID>) : UUIDEntity(id = uuid) {
    val task: TaskEntity by TaskEntity referencedOn BankTable.taskId
    val name: String by BankTable.name
    val jmsQueue: String by BankTable.jmsQueue
    val thumbnailUrl: String by BankTable.thumbnailUrl
    val isAsync: Boolean by BankTable.isAsync
    val isActive: Boolean by BankTable.isActive
    val creditConfiguration: CreditConfigurationEntity? by CreditConfigurationEntity.optionalBackReferencedOn(
        column = CreditConfigurationTable.bankId,
    )
    val teams: SizedIterable<TeamEntity> by TeamEntity via StatisticTable

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
