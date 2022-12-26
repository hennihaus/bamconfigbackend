package de.hennihaus.repositories

import de.hennihaus.models.Parameter
import de.hennihaus.models.Task
import de.hennihaus.repositories.entities.BankEntity
import de.hennihaus.repositories.entities.ParameterEntity
import de.hennihaus.repositories.entities.TaskEntity
import de.hennihaus.repositories.mappers.toParameter
import de.hennihaus.repositories.mappers.toTask
import de.hennihaus.repositories.tables.ContactTable
import de.hennihaus.repositories.tables.EndpointTable
import de.hennihaus.repositories.tables.ParameterTable
import de.hennihaus.repositories.tables.ResponseTable
import de.hennihaus.repositories.tables.TaskParameterTable
import de.hennihaus.repositories.tables.TaskResponseTable
import de.hennihaus.repositories.tables.TaskTable
import de.hennihaus.utils.batchUpsert
import de.hennihaus.utils.inTransaction
import de.hennihaus.utils.upsert
import org.jetbrains.exposed.dao.load
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import java.time.Instant
import java.time.OffsetDateTime
import java.util.UUID

@Single
class TaskRepository {

    suspend fun getById(id: UUID): Task? = inTransaction {
        TaskEntity.findById(id = id)
            ?.load(relations = getTaskRelations())
            ?.toTask()
    }

    suspend fun getAll(): List<Task> = inTransaction {
        TaskEntity.all()
            .with(relations = getTaskRelations())
            .map {
                it.toTask()
            }
    }

    suspend fun deleteById(id: UUID): Boolean = inTransaction {
        TaskEntity.findById(id = id)
            ?.delete()
            ?.let { true }
            ?: false
    }

    suspend fun save(entry: Task, repetitionAttempts: Int): Task = inTransaction(
        repetitionAttempts = repetitionAttempts,
    ) {
        val now = OffsetDateTime.now().toInstant()

        entry.saveContact(now = now)
        entry.saveTask(now = now)
        entry.saveEndpoints()
        entry.saveParameters(now = now)
        entry.saveResponses(now = now)

        TaskEntity.findById(id = entry.uuid)
            ?.load(relations = getTaskRelations())
            ?.toTask()
            ?: throw IllegalStateException(TASK_NOT_FOUND_MESSAGE)
    }

    suspend fun getTaskIdByTitle(title: String): UUID? = inTransaction {
        TaskTable.slice(column = TaskTable.id)
            .select { TaskTable.title eq title }
            .singleOrNull()
            ?.let { it[TaskTable.id].value }
    }

    suspend fun getAllParametersById(id: UUID): List<UUID> = inTransaction {
        TaskParameterTable.slice(column = TaskParameterTable.parameterId)
            .select { TaskParameterTable.taskId eq id }
            .map { it[TaskParameterTable.parameterId].value }
    }

    suspend fun getAllResponsesById(id: UUID): List<UUID> = inTransaction {
        TaskResponseTable.slice(column = TaskResponseTable.responseId)
            .select { TaskResponseTable.taskId eq id }
            .map { it[TaskResponseTable.responseId].value }
    }

    suspend fun updateParameter(name: String, example: String, repetitionAttempts: Int): Parameter? = inTransaction(
        repetitionAttempts = repetitionAttempts,
    ) {
        ParameterTable.update(where = { ParameterTable.name eq name }) {
            it[ParameterTable.example] = example
        }

        ParameterEntity.find { ParameterTable.name eq name }
            .singleOrNull()
            ?.toParameter()
    }

    private fun Task.saveContact(now: Instant) {
        ContactTable.upsert(conflictColumns = listOf(ContactTable.id)) { contactTable ->
            contactTable[id] = contact.uuid
            contactTable[firstname] = contact.firstname
            contactTable[lastname] = contact.lastname
            contactTable[email] = contact.email
            contactTable[updated] = now
        }
        ContactTable.deleteWhere {
            ContactTable.id neq contact.uuid and (
                ContactTable.id notInSubQuery TaskTable
                    .slice(column = TaskTable.contactId)
                    .selectAll()
                    .withDistinct()
                )
        }
    }

    private fun Task.saveTask(now: Instant) {
        TaskTable.upsert(conflictColumns = listOf(TaskTable.id)) { taskTable ->
            taskTable[id] = this@saveTask.uuid
            taskTable[contactId] = contact.uuid
            taskTable[integrationStep] = this@saveTask.integrationStep.value
            taskTable[title] = this@saveTask.title
            taskTable[description] = this@saveTask.description
            taskTable[isOpenApiVerbose] = this@saveTask.isOpenApiVerbose
            taskTable[updated] = now
        }
    }

    private fun Task.saveEndpoints() {
        EndpointTable.batchUpsert(
            data = endpoints,
            conflictColumns = listOf(EndpointTable.id),
        ) { endpointTable, endpoint ->
            endpointTable[id] = endpoint.uuid
            endpointTable[taskId] = uuid
            endpointTable[type] = endpoint.type.name
            endpointTable[url] = "${endpoint.url}"
            endpointTable[docsUrl] = "${endpoint.docsUrl}"
        }
        EndpointTable.deleteWhere {
            EndpointTable.taskId eq uuid and (EndpointTable.id notInList endpoints.map { it.uuid })
        }
    }

    private fun Task.saveParameters(now: Instant) {
        ParameterTable.batchUpsert(
            data = parameters,
            conflictColumns = listOf(ParameterTable.id),
        ) { parameterTable, parameter ->
            parameterTable[id] = parameter.uuid
            parameterTable[name] = parameter.name
            parameterTable[type] = parameter.type.name
            parameterTable[description] = parameter.description
            parameterTable[example] = parameter.example
            parameterTable[updated] = now
        }
        TaskParameterTable.batchUpsert(
            data = parameters,
            conflictColumns = listOf(TaskParameterTable.taskId, TaskParameterTable.parameterId),
        ) { table, parameter ->
            table[taskId] = uuid
            table[parameterId] = parameter.uuid
        }
        TaskParameterTable.deleteWhere {
            TaskParameterTable.taskId eq uuid and (TaskParameterTable.parameterId notInList parameters.map { it.uuid })
        }
        ParameterTable.deleteWhere {
            ParameterTable.id notInSubQuery TaskParameterTable
                .slice(column = TaskParameterTable.parameterId)
                .selectAll()
                .withDistinct()
        }
    }

    private fun Task.saveResponses(now: Instant) {
        ResponseTable.batchUpsert(
            data = responses,
            conflictColumns = listOf(ResponseTable.id),
        ) { responseTable, response ->
            responseTable[id] = response.uuid
            responseTable[httpStatusCode] = response.httpStatusCode.value
            responseTable[contentType] = "${response.contentType}"
            responseTable[description] = response.description
            responseTable[example] = response.example
            responseTable[updated] = now
        }
        TaskResponseTable.batchUpsert(
            data = responses,
            conflictColumns = listOf(TaskResponseTable.taskId, TaskResponseTable.responseId),
        ) { taskResponseTable, response ->
            taskResponseTable[taskId] = uuid
            taskResponseTable[responseId] = response.uuid
        }
        TaskResponseTable.deleteWhere {
            TaskResponseTable.taskId eq uuid and (TaskResponseTable.responseId notInList responses.map { it.uuid })
        }
        ResponseTable.deleteWhere {
            ResponseTable.id notInSubQuery TaskResponseTable
                .slice(column = TaskResponseTable.responseId)
                .selectAll()
                .withDistinct()
        }
    }

    private fun getTaskRelations() = arrayOf(
        TaskEntity::contact,
        TaskEntity::endpoints,
        TaskEntity::parameters,
        TaskEntity::responses,
        TaskEntity::banks,
        BankEntity::creditConfiguration,
    )

    companion object {
        private const val TASK_NOT_FOUND_MESSAGE = "Task not found in database"
    }
}
