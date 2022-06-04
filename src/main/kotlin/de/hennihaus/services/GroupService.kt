package de.hennihaus.services

import de.hennihaus.models.Group

interface GroupService {
    suspend fun getAllGroups(): List<Group>
    suspend fun getGroupById(id: String): Group
    suspend fun checkUsername(id: String, username: String): Boolean
    suspend fun checkPassword(id: String, password: String): Boolean
    suspend fun checkJmsQueue(id: String, jmsQueue: String): Boolean
    suspend fun saveGroup(group: Group): Group
    suspend fun deleteGroupById(id: String)
    suspend fun resetAllGroups(): List<Group>
    suspend fun resetStats(id: String): Group
}
