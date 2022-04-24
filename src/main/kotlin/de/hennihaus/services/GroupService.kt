package de.hennihaus.services

import de.hennihaus.models.Group

interface GroupService {
    suspend fun getAllGroups(): List<Group>
    suspend fun getGroupById(id: String): Group
    suspend fun checkUsername(id: String, username: String): Boolean
    suspend fun checkPassword(id: String, password: String): Boolean
    suspend fun checkJmsTopic(id: String, jmsTopic: String): Boolean
    suspend fun createGroup(group: Group): Group
    suspend fun updateGroup(group: Group): Group
    suspend fun deleteGroupById(id: String)
    suspend fun resetStats(id: String): Group
}
