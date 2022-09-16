package de.hennihaus.configurations

import org.koin.core.qualifier.named
import org.koin.dsl.module

val githubModule = module {

    single {
        val protocol = getProperty<String>(key = GithubConfiguration.PROTOCOL)
        val host = getProperty<String>(key = GithubConfiguration.HOST)
        val maxRetries = getProperty<String>(key = GithubConfiguration.RETRIES)
        val apiVersionHeader = getProperty<String>(key = GithubConfiguration.API_VERSION_HEADER)
        val authorizationHeader = getProperty<String>(key = GithubConfiguration.AUTHORIZATION_HEADER)

        GithubConfiguration(
            protocol = protocol,
            host = host,
            maxRetries = maxRetries.toInt(),
            apiVersionHeader = apiVersionHeader,
            authorizationHeader = authorizationHeader,
        )
    }

    single {
        val branch = getProperty<String>(key = GithubCommitConfiguration.BRANCH)
        val commitMessage = getProperty<String>(key = GithubCommitConfiguration.COMMIT_MESSAGE)
        val committerName = getProperty<String>(key = GithubCommitConfiguration.COMMITTER_NAME)
        val committerEmail = getProperty<String>(key = GithubCommitConfiguration.COMMITTER_EMAIL)

        GithubCommitConfiguration(
            branch = branch,
            commitMessage = commitMessage,
            committerName = committerName,
            committerEmail = committerEmail,
        )
    }

    single(
        qualifier = named(name = GithubFileConfiguration.SCHUFA_FILE_CONFIG)
    ) {

        val owner = getProperty<String>(key = GithubFileConfiguration.OWNER)
        val repo = getProperty<String>(key = GithubFileConfiguration.SCHUFA_REPO)
        val path = getProperty<String>(key = GithubFileConfiguration.SCHUFA_PATH)
        val branch = getProperty<String>(key = GithubFileConfiguration.BRANCH)

        GithubFileConfiguration(
            owner = owner,
            repo = repo,
            path = path,
            branch = branch,
        )
    }

    single(
        qualifier = named(name = GithubFileConfiguration.BANK_FILE_CONFIG)
    ) {
        val owner = getProperty<String>(key = GithubFileConfiguration.OWNER)
        val repo = getProperty<String>(key = GithubFileConfiguration.BANK_REPO)
        val path = getProperty<String>(key = GithubFileConfiguration.BANK_PATH)
        val branch = getProperty<String>(key = GithubFileConfiguration.BRANCH)

        GithubFileConfiguration(
            owner = owner,
            repo = repo,
            path = path,
            branch = branch,
        )
    }
}

data class GithubConfiguration(
    val protocol: String,
    val host: String,
    val maxRetries: Int,
    val apiVersionHeader: String,
    val authorizationHeader: String,
) {
    companion object {
        const val PROTOCOL = "github.protocol"
        const val HOST = "github.host"
        const val RETRIES = "github.retries"
        const val API_VERSION_HEADER = "github.headers.apiVersion"
        const val AUTHORIZATION_HEADER = "github.authorizationToken"

        const val DEFAULT_TITLE = "github.openapi.title"
    }
}

data class GithubCommitConfiguration(
    val branch: String,
    val commitMessage: String,
    val committerName: String,
    val committerEmail: String,
) {
    companion object {
        const val BRANCH = "github.openapi.branch"
        const val COMMIT_MESSAGE = "github.openapi.commitMessage"
        const val COMMITTER_NAME = "github.openapi.committer.name"
        const val COMMITTER_EMAIL = "github.openapi.committer.email"
    }
}

data class GithubFileConfiguration(
    val owner: String,
    val repo: String,
    val path: String,
    val branch: String,
) {
    companion object {
        const val SCHUFA_FILE_CONFIG = "schufa"
        const val BANK_FILE_CONFIG = "bank"

        const val OWNER = "github.openapi.owner"
        const val BRANCH = "github.openapi.branch"

        const val SCHUFA_REPO = "github.openapi.repos[0]"
        const val SCHUFA_PATH = "github.openapi.paths[0]"

        const val BANK_REPO = "github.openapi.repos[1]"
        const val BANK_PATH = "github.openapi.paths[1]"
    }
}
