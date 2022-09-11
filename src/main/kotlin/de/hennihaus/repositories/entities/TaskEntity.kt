package de.hennihaus.repositories.entities

import de.hennihaus.repositories.tables.BankTable
import de.hennihaus.repositories.tables.ContactTable
import de.hennihaus.repositories.tables.EndpointTable
import de.hennihaus.repositories.tables.ParameterTable
import de.hennihaus.repositories.tables.ResponseTable
import de.hennihaus.repositories.tables.TaskParameterTable
import de.hennihaus.repositories.tables.TaskResponseTable
import de.hennihaus.repositories.tables.TaskTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import java.util.UUID

class TaskEntity(uuid: EntityID<UUID>) : UUIDEntity(id = uuid) {
    val title: String by TaskTable.title
    val description: String by TaskTable.description
    val integrationStep: Int by TaskTable.integrationStep
    val isOpenApiVerbose: Boolean by TaskTable.isOpenApiVerbose
    val contact: ContactEntity by ContactEntity referencedOn TaskTable.contactId
    val endpoints: SizedIterable<EndpointEntity> by EndpointEntity referrersOn EndpointTable.taskId
    val parameters: SizedIterable<ParameterEntity> by ParameterEntity via TaskParameterTable
    val responses: SizedIterable<ResponseEntity> by ResponseEntity via TaskResponseTable
    val banks: SizedIterable<BankEntity> by BankEntity referrersOn BankTable.taskId

    companion object : UUIDEntityClass<TaskEntity>(TaskTable)
}

class ContactEntity(uuid: EntityID<UUID>) : UUIDEntity(id = uuid) {
    val firstname: String by ContactTable.firstname
    val lastname: String by ContactTable.lastname
    val email: String by ContactTable.email

    companion object : UUIDEntityClass<ContactEntity>(ContactTable)
}

class EndpointEntity(uuid: EntityID<UUID>) : UUIDEntity(id = uuid) {
    val type: String by EndpointTable.type
    val url: String by EndpointTable.url
    val docsUrl: String by EndpointTable.docsUrl

    companion object : UUIDEntityClass<EndpointEntity>(EndpointTable)
}

class ParameterEntity(uuid: EntityID<UUID>) : UUIDEntity(id = uuid) {
    val name: String by ParameterTable.name
    val type: String by ParameterTable.type
    val description: String by ParameterTable.description
    val example: String by ParameterTable.example

    companion object : UUIDEntityClass<ParameterEntity>(ParameterTable)
}

class ResponseEntity(uuid: EntityID<UUID>) : UUIDEntity(id = uuid) {
    val httpStatusCode: Int by ResponseTable.httpStatusCode
    val contentType: String by ResponseTable.contentType
    val description: String by ResponseTable.description
    val example: String by ResponseTable.example

    companion object : UUIDEntityClass<ResponseEntity>(ResponseTable)
}
