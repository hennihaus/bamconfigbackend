package de.hennihaus.models

import de.hennihaus.configurations.MongoConfiguration.ID_FIELD
import de.hennihaus.models.serializer.UriSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import java.net.URI

@Serializable
data class Task(
    @Contextual
    @SerialName(ID_FIELD)
    val id: Id<Task>,
    val title: String,
    val description: String,
    val step: Int,
    val endpoints: List<Endpoint>,
    val parameters: List<Parameter>,
    val banks: List<Bank>
)

@Serializable
data class Endpoint(
    val type: EndpointType,
    @Serializable(with = UriSerializer::class)
    val url: URI,
    @Serializable(with = UriSerializer::class)
    val docsUrl: URI
)

enum class EndpointType {
    REST,
    SOAP,
    JMS
}

@Serializable
data class Parameter(
    val name: String,
    val type: ParameterType,
    val description: String
)

enum class ParameterType {
    STRING,
    INTEGER,
    LONG,
    CHARACTER
}
