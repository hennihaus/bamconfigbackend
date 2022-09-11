package de.hennihaus.repositories.entities

import de.hennihaus.repositories.tables.StatisticTable
import de.hennihaus.repositories.tables.StudentTable
import de.hennihaus.repositories.tables.TeamTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import java.util.UUID

class TeamEntity(uuid: EntityID<UUID>) : UUIDEntity(id = uuid) {
    val username: String by TeamTable.username
    val password: String by TeamTable.password
    val jmsQueue: String by TeamTable.jmsQueue
    val students: SizedIterable<StudentEntity> by StudentEntity referrersOn StudentTable.teamId
    val statistics: SizedIterable<StatisticEntity> by StatisticEntity referrersOn StatisticTable.teamId

    companion object : UUIDEntityClass<TeamEntity>(TeamTable)
}

class StudentEntity(uuid: EntityID<UUID>) : UUIDEntity(id = uuid) {
    val firstname: String by StudentTable.firstname
    val lastname: String by StudentTable.lastname

    companion object : UUIDEntityClass<StudentEntity>(StudentTable)
}

class StatisticEntity(id: EntityID<Long>) : LongEntity(id = id) {
    val bank: BankEntity by BankEntity referencedOn StatisticTable.bankId
    val team: TeamEntity by TeamEntity referencedOn StatisticTable.teamId
    val requestsCount: Long by StatisticTable.requestsCount

    companion object : LongEntityClass<StatisticEntity>(StatisticTable)
}
