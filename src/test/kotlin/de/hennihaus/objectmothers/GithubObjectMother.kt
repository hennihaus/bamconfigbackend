package de.hennihaus.objectmothers

import de.hennihaus.configurations.GithubCommitConfiguration
import de.hennihaus.configurations.GithubConfiguration
import de.hennihaus.configurations.GithubFileConfiguration
import de.hennihaus.models.generated.github.GetFileResponse
import de.hennihaus.models.generated.github.UpdateFileRequest
import io.ktor.http.URLProtocol
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

object GithubObjectMother {

    // request configuration
    const val DEFAULT_PROTOCOL = "http"
    const val DEFAULT_HOST = "api.github.com"
    const val DEFAULT_MAX_RETRIES = 1
    const val DEFAULT_API_VERSION_HEADER = "application/vnd.github.v3+json"
    const val DEFAULT_AUTHORIZATION_HEADER = "token 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"

    // commit and file configuration
    const val DEFAULT_BRANCH = "master"

    // commit configuration
    const val DEFAULT_TITLE = "BAM Business Integration"
    const val DEFAULT_COMMIT_MESSAGE = "Updated swagger config #patch"
    const val DEFAULT_COMMITTER_NAME = "hennihaus"
    const val DEFAULT_COMMITTER_EMAIL = "jan-hendrik.hausner@outlook.com"

    // file configuration
    const val DEFAULT_OWNER = "hennihaus"
    const val DEFAULT_REPO = "bamschufarest"
    const val DEFAULT_PATH = "docs/rating.json"

    fun getRatingFileResponse(): GetFileResponse = Json.decodeFromString(
        string = File("./src/test/resources/github/getRatingFileResponse.json").readText(),
    )

    fun getCreditFileResponse(): GetFileResponse = Json.decodeFromString(
        string = File("./src/test/resources/github/getCreditFileResponse.json").readText(),
    )

    fun getRatingUpdateFileRequest(): UpdateFileRequest = Json.decodeFromString(
        string = File("./src/test/resources/github/updateRatingFileRequest.json").readText(),
    )

    fun getCreditUpdateFileRequest(): UpdateFileRequest = Json.decodeFromString(
        string = File("./src/test/resources/github/updateCreditFileRequest.json").readText(),
    )

    fun getGithubConfiguration(
        protocol: URLProtocol = URLProtocol.createOrDefault(name = DEFAULT_PROTOCOL),
        host: String = DEFAULT_HOST,
        maxRetries: Int = DEFAULT_MAX_RETRIES,
        apiVersionHeader: String = DEFAULT_API_VERSION_HEADER,
        authorizationHeader: String = DEFAULT_AUTHORIZATION_HEADER,
    ) = GithubConfiguration(
        protocol = protocol.name,
        host = host,
        maxRetries = maxRetries,
        apiVersionHeader = apiVersionHeader,
        authorizationHeader = authorizationHeader,
    )

    fun getGithubCommitConfiguration(
        branch: String = DEFAULT_BRANCH,
        commitMessage: String = DEFAULT_COMMIT_MESSAGE,
        committerName: String = DEFAULT_COMMITTER_NAME,
        committerEmail: String = DEFAULT_COMMITTER_EMAIL,
    ) = GithubCommitConfiguration(
        branch = branch,
        commitMessage = commitMessage,
        committerName = committerName,
        committerEmail = committerEmail,
    )

    fun getGithubFileConfiguration(
        owner: String = DEFAULT_OWNER,
        repo: String = DEFAULT_REPO,
        path: String = DEFAULT_PATH,
        branch: String = DEFAULT_BRANCH,
    ) = GithubFileConfiguration(
        owner = owner,
        repo = repo,
        path = path,
        branch = branch,
    )

    fun getInvalidGithubFileConfiguration(
        owner: String = "invalid",
        repo: String = "invalid",
        path: String = "invalid/invalid.json",
        branch: String = "invalid",
    ) = GithubFileConfiguration(
        owner = owner,
        repo = repo,
        path = path,
        branch = branch,
    )
}
