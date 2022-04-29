package de.hennihaus.services

import de.hennihaus.models.Group

interface StatsService {
    suspend fun setHasPassed(group: Group): Group
}
