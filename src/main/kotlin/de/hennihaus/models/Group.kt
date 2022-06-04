package de.hennihaus.models

import de.hennihaus.configurations.MongoConfiguration.ID_FIELD
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Serializable
data class Group(
    @Contextual
    @SerialName(ID_FIELD)
    val id: Id<Group> = newId(),
    val username: String,
    val password: String,
    val jmsQueue: String,
    val students: List<String>,
    val stats: Map<String, Int>,
    val hasPassed: Boolean,
)
