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
        const val PROTOCOL = "ktor.github.protocol"
        const val HOST = "ktor.github.host"
        const val RETRIES = "ktor.github.retries"
        const val API_VERSION_HEADER = "ktor.github.headers.apiVersion"
        const val AUTHORIZATION_HEADER = "GITHUB_AUTHORIZATION_TOKEN"

        const val DEFAULT_TITLE = "ktor.github.openapi.title"
    }
}

data class GithubCommitConfiguration(
    val branch: String,
    val commitMessage: String,
    val committerName: String,
    val committerEmail: String,
) {
    companion object {
        const val BRANCH = "ktor.github.openapi.branch"
        const val COMMIT_MESSAGE = "ktor.github.openapi.commitMessage"
        const val COMMITTER_NAME = "ktor.github.openapi.committer.name"
        const val COMMITTER_EMAIL = "ktor.github.openapi.committer.email"
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

        const val OWNER = "ktor.github.openapi.owner"
        const val BRANCH = "ktor.github.openapi.branch"

        const val SCHUFA_REPO = "ktor.github.openapi.files[0].repo"
        const val SCHUFA_PATH = "ktor.github.openapi.files[0].path"

        const val BANK_REPO = "ktor.github.openapi.files[1].repo"
        const val BANK_PATH = "ktor.github.openapi.files[1].path"
    }
}
