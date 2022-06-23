package de.hennihaus.services.mapperservices

import de.hennihaus.models.Task
import de.hennihaus.models.generated.openapi.BankApi
import de.hennihaus.models.generated.openapi.SchufaApi

interface GithubMapperService {
    fun updateSchufaApi(api: SchufaApi, task: Task): SchufaApi

    fun updateBankApi(api: BankApi, task: Task): BankApi
}
