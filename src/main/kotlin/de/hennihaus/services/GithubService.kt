package de.hennihaus.services

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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

    private val mapper = jacksonObjectMapper().apply {
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        enable(SerializationFeature.INDENT_OUTPUT)
        setDefaultPrettyPrinter(
            object : DefaultPrettyPrinter() {
                override fun createInstance(): DefaultPrettyPrinter = this

                override fun writeObjectFieldValueSeparator(generator: JsonGenerator) = generator.writeRaw(": ")
            }.apply {
                indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
            }
        )
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
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

    private suspend inline fun <reified T> getGithubFileAsEntity(fileConfig: GithubFileConfiguration): Pair<String, T> {
        val file = githubCall.getFile(
            fileConfig = fileConfig,
        )
        /**
         * Source: https://github.com/hub4j/github-api/blob/main/src/main/java/org/kohsuke/github/GHContent.java
         */
        return Base64.getMimeDecoder().decode(file.content.toByteArray(charset = Charsets.US_ASCII)).inputStream().use {
            file.sha to mapper.readValue(
                src = it,
            )
        }
    }

    private suspend inline fun <reified T> updateGithubFileWithEntity(
        fileConfig: GithubFileConfiguration,
        sha: String,
        entity: T,
    ) {
        val content = mapper.writeValueAsString(entity).toByteArray().let {
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
