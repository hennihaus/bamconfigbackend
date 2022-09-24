package de.hennihaus.services

import de.hennihaus.configurations.GithubCommitConfiguration
import de.hennihaus.configurations.GithubFileConfiguration
import de.hennihaus.configurations.GithubFileConfiguration.Companion.BANK_FILE_CONFIG
import de.hennihaus.configurations.GithubFileConfiguration.Companion.SCHUFA_FILE_CONFIG
import de.hennihaus.models.IntegrationStep
import de.hennihaus.models.Task
import de.hennihaus.models.generated.github.UpdateFileCommitter
import de.hennihaus.models.generated.github.UpdateFileRequest
import de.hennihaus.models.generated.openapi.BankApi
import de.hennihaus.models.generated.openapi.SchufaApi
import de.hennihaus.services.callservices.GithubCallService
import de.hennihaus.services.mapperservices.GithubMapperService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import java.util.Base64

@Single
class GithubService(
    private val githubCall: GithubCallService,
    private val githubMapper: GithubMapperService,
    private val commitConfig: GithubCommitConfiguration,
    @Named(SCHUFA_FILE_CONFIG) private val schufaFileConfig: GithubFileConfiguration,
    @Named(BANK_FILE_CONFIG) private val bankFileConfig: GithubFileConfiguration,
) {

    @OptIn(ExperimentalSerializationApi::class)
    private val format = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    suspend fun updateOpenApi(task: Task) = when (task.integrationStep) {
        IntegrationStep.SCHUFA_STEP -> updateSchufa(task = task)
        IntegrationStep.SYNC_BANK_STEP -> updateBank(task = task)
        IntegrationStep.ASYNC_BANK_STEP -> {}
    }

    private suspend fun updateSchufa(task: Task) {
        val (sha, api) = getGithubFileAsEntity<SchufaApi>(fileConfig = schufaFileConfig)
        githubMapper.updateSchufaApi(
            api = api,
            task = task,
        ).also {
            updateGithubFileWithEntity(
                fileConfig = schufaFileConfig,
                sha = sha,
                entity = it,
            )
        }
    }

    private suspend fun updateBank(task: Task) {
        val (sha, api) = getGithubFileAsEntity<BankApi>(fileConfig = bankFileConfig)
        githubMapper.updateBankApi(
            api = api,
            task = task,
        ).also {
            updateGithubFileWithEntity(
                fileConfig = bankFileConfig,
                sha = sha,
                entity = it,
            )
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend inline fun <reified T> getGithubFileAsEntity(fileConfig: GithubFileConfiguration): Pair<String, T> {
        val file = githubCall.getFile(
            fileConfig = fileConfig,
        )
        /**
         * Source: https://github.com/hub4j/github-api/blob/main/src/main/java/org/kohsuke/github/GHContent.java
         */
        return Base64.getMimeDecoder().decode(file.content.toByteArray(charset = Charsets.US_ASCII)).inputStream().use {
            file.sha to format.decodeFromStream(
                stream = it,
            )
        }
    }

    private suspend inline fun <reified T> updateGithubFileWithEntity(
        fileConfig: GithubFileConfiguration,
        sha: String,
        entity: T,
    ) {
        val content = format.encodeToString(value = entity).toByteArray().let {
            /**
             * Source: https://github.com/hub4j/github-api/blob/main/src/main/java/org/kohsuke/github/GHContent.java
             */
            Base64.getEncoder().encodeToString(it)
        }
        githubCall.updateFile(
            fileConfig = fileConfig,
            file = commitConfig.toUpdateFileRequest(
                content = content,
                sha = sha,
            ),
        )
    }

    private fun GithubCommitConfiguration.toUpdateFileRequest(content: String, sha: String) = UpdateFileRequest(
        message = commitMessage,
        committer = UpdateFileCommitter(
            name = committerName,
            email = committerEmail,
        ),
        content = content,
        sha = sha,
        branch = branch,
    )
}
