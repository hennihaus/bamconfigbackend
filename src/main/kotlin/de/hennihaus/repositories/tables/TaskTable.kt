package de.hennihaus.repositories.tables

import de.hennihaus.repositories.tables.ContactTableDescription.CONTACT_EMAIL_COLUMN
import de.hennihaus.repositories.tables.ContactTableDescription.CONTACT_FIRSTNAME_COLUMN
import de.hennihaus.repositories.tables.ContactTableDescription.CONTACT_LASTNAME_COLUMN
import de.hennihaus.repositories.tables.ContactTableDescription.CONTACT_LAST_UPDATED_COLUMN
import de.hennihaus.repositories.tables.ContactTableDescription.CONTACT_UUID_COLUMN
import de.hennihaus.repositories.tables.EndpointTableDescription.ENDPOINT_DOCS_URL_COLUMN
import de.hennihaus.repositories.tables.EndpointTableDescription.ENDPOINT_TYPE_COLUMN
import de.hennihaus.repositories.tables.EndpointTableDescription.ENDPOINT_URL_COLUMN
import de.hennihaus.repositories.tables.EndpointTableDescription.ENDPOINT_UUID_COLUMN
import de.hennihaus.repositories.tables.ParameterTableDescription.PARAMETER_DESCRIPTION_COLUMN
import de.hennihaus.repositories.tables.ParameterTableDescription.PARAMETER_EXAMPLE_COLUMN
import de.hennihaus.repositories.tables.ParameterTableDescription.PARAMETER_LAST_UPDATED_COLUMN
import de.hennihaus.repositories.tables.ParameterTableDescription.PARAMETER_NAME_COLUMN
import de.hennihaus.repositories.tables.ParameterTableDescription.PARAMETER_TYPE_COLUMN
import de.hennihaus.repositories.tables.ParameterTableDescription.PARAMETER_UUID_COLUMN
import de.hennihaus.repositories.tables.ResponseTableDescription.RESPONSE_CONTENT_TYPE_COLUMN
import de.hennihaus.repositories.tables.ResponseTableDescription.RESPONSE_DESCRIPTION_COLUMN
import de.hennihaus.repositories.tables.ResponseTableDescription.RESPONSE_EXAMPLE_JSON_COLUMN
import de.hennihaus.repositories.tables.ResponseTableDescription.RESPONSE_HTTP_STATUS_CODE_COLUMN
import de.hennihaus.repositories.tables.ResponseTableDescription.RESPONSE_LAST_UPDATED_COLUMN
import de.hennihaus.repositories.tables.ResponseTableDescription.RESPONSE_UUID_COLUMN
import de.hennihaus.repositories.tables.TaskParameterTableDescription.TASK_PARAMETER_TABLE
import de.hennihaus.repositories.tables.TaskResponseTableDescription.TASK_RESPONSE_TABLE
import de.hennihaus.repositories.tables.TaskTableDescription.TASK_DESCRIPTION_COLUMN
import de.hennihaus.repositories.tables.TaskTableDescription.TASK_INTEGRATION_STEP_COLUMN
import de.hennihaus.repositories.tables.TaskTableDescription.TASK_IS_OPEN_API_VERBOSE_COLUMN
import de.hennihaus.repositories.tables.TaskTableDescription.TASK_LAST_UPDATED_COLUMN
import de.hennihaus.repositories.tables.TaskTableDescription.TASK_TITLE_COLUMN
import de.hennihaus.repositories.tables.TaskTableDescription.TASK_UUID_COLUMN
import de.hennihaus.repositories.types.jsonb
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object TaskTable : UUIDTable(columnName = TASK_UUID_COLUMN) {
    val contactId = reference(name = CONTACT_UUID_COLUMN, foreign = ContactTable)
    val integrationStep = integer(name = TASK_INTEGRATION_STEP_COLUMN)
    val title = text(name = TASK_TITLE_COLUMN)
    val description = text(name = TASK_DESCRIPTION_COLUMN)
    val isOpenApiVerbose = bool(name = TASK_IS_OPEN_API_VERBOSE_COLUMN)
    val lastUpdated = timestamp(name = TASK_LAST_UPDATED_COLUMN)
}

object ContactTable : UUIDTable(columnName = CONTACT_UUID_COLUMN) {
    val firstname = text(name = CONTACT_FIRSTNAME_COLUMN)
    val lastname = text(name = CONTACT_LASTNAME_COLUMN)
    val email = text(name = CONTACT_EMAIL_COLUMN)
    val lastUpdated = timestamp(name = CONTACT_LAST_UPDATED_COLUMN)
}

object EndpointTable : UUIDTable(columnName = ENDPOINT_UUID_COLUMN) {
    val taskId = reference(name = TASK_UUID_COLUMN, foreign = TaskTable)
    val type = text(name = ENDPOINT_TYPE_COLUMN)
    val url = text(name = ENDPOINT_URL_COLUMN)
    val docsUrl = text(name = ENDPOINT_DOCS_URL_COLUMN)
}

object ParameterTable : UUIDTable(columnName = PARAMETER_UUID_COLUMN) {
    val name = text(name = PARAMETER_NAME_COLUMN)
    val type = text(name = PARAMETER_TYPE_COLUMN)
    val description = text(name = PARAMETER_DESCRIPTION_COLUMN)
    val example = text(name = PARAMETER_EXAMPLE_COLUMN)
    val lastUpdated = timestamp(name = PARAMETER_LAST_UPDATED_COLUMN)
}

object TaskParameterTable : Table(name = TASK_PARAMETER_TABLE) {
    val taskId = reference(name = TASK_UUID_COLUMN, foreign = TaskTable)
    val parameterId = reference(name = PARAMETER_UUID_COLUMN, foreign = ParameterTable)
}

object ResponseTable : UUIDTable(columnName = RESPONSE_UUID_COLUMN) {
    val httpStatusCode = integer(name = RESPONSE_HTTP_STATUS_CODE_COLUMN)
    val contentType = text(name = RESPONSE_CONTENT_TYPE_COLUMN)
    val description = text(name = RESPONSE_DESCRIPTION_COLUMN)
    val example = jsonb(name = RESPONSE_EXAMPLE_JSON_COLUMN)
    val lastUpdated = timestamp(name = RESPONSE_LAST_UPDATED_COLUMN)
}

object TaskResponseTable : Table(name = TASK_RESPONSE_TABLE) {
    val taskId = reference(name = TASK_UUID_COLUMN, foreign = TaskTable)
    val responseId = reference(name = RESPONSE_UUID_COLUMN, foreign = ResponseTable)
}

object TaskTableDescription {
    const val TASK_UUID_COLUMN = "task_uuid"
    const val TASK_INTEGRATION_STEP_COLUMN = "task_integration_step"
    const val TASK_TITLE_COLUMN = "task_title"
    const val TASK_DESCRIPTION_COLUMN = "task_description"
    const val TASK_IS_OPEN_API_VERBOSE_COLUMN = "task_is_open_api_verbose"
    const val TASK_LAST_UPDATED_COLUMN = "task_updated_timestamp_with_time_zone"
}

object ContactTableDescription {
    const val CONTACT_UUID_COLUMN = "contact_uuid"
    const val CONTACT_FIRSTNAME_COLUMN = "contact_firstname"
    const val CONTACT_LASTNAME_COLUMN = "contact_lastname"
    const val CONTACT_EMAIL_COLUMN = "contact_email"
    const val CONTACT_LAST_UPDATED_COLUMN = "contact_updated_timestamp_with_time_zone"
}

object EndpointTableDescription {
    const val ENDPOINT_UUID_COLUMN = "endpoint_uuid"
    const val ENDPOINT_TYPE_COLUMN = "endpoint_type"
    const val ENDPOINT_URL_COLUMN = "endpoint_url"
    const val ENDPOINT_DOCS_URL_COLUMN = "endpoint_docs_url"
}

object ParameterTableDescription {
    const val PARAMETER_UUID_COLUMN = "parameter_uuid"
    const val PARAMETER_NAME_COLUMN = "parameter_name"
    const val PARAMETER_TYPE_COLUMN = "parameter_type"
    const val PARAMETER_DESCRIPTION_COLUMN = "parameter_description"
    const val PARAMETER_EXAMPLE_COLUMN = "parameter_example"
    const val PARAMETER_LAST_UPDATED_COLUMN = "parameter_updated_timestamp_with_time_zone"
}

object TaskParameterTableDescription {
    const val TASK_PARAMETER_TABLE = "task_parameter"
}

object ResponseTableDescription {
    const val RESPONSE_UUID_COLUMN = "response_uuid"
    const val RESPONSE_HTTP_STATUS_CODE_COLUMN = "response_http_status_code"
    const val RESPONSE_CONTENT_TYPE_COLUMN = "response_content_type"
    const val RESPONSE_DESCRIPTION_COLUMN = "response_description"
    const val RESPONSE_EXAMPLE_JSON_COLUMN = "response_example_json"
    const val RESPONSE_LAST_UPDATED_COLUMN = "response_updated_timestamp_with_time_zone"
}

object TaskResponseTableDescription {
    const val TASK_RESPONSE_TABLE = "task_response"
}
