package de.hennihaus.services.callservices

import de.hennihaus.configurations.GithubConfiguration
import de.hennihaus.configurations.GithubFileConfiguration
import de.hennihaus.models.generated.github.GetFileResponse
import de.hennihaus.models.generated.github.UpdateFileRequest
import de.hennihaus.models.generated.github.UpdateFileResponse
import de.hennihaus.services.callservices.paths.GithubPaths.BRANCH_PARAMETER_NAME
import de.hennihaus.services.callservices.paths.GithubPaths.CONTENTS_PATH
import de.hennihaus.services.callservices.paths.GithubPaths.REPOS_PATH
import de.hennihaus.utils.configureMonitoring
import de.hennihaus.utils.configureRetryBehavior
import de.hennihaus.utils.configureSerialization
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import io.ktor.util.appendIfNameAbsent
import org.koin.core.annotation.Single

@Single
class GithubCallService(private val engine: HttpClientEngine, private val config: GithubConfiguration) {

    private val client = HttpClient(engine = engine) {
        expectSuccess = true
        configureMonitoring()
        configureSerialization()
        configureRetryBehavior(maxRetries = config.maxRetries)
        configureDefaultRequests()
    }

    suspend fun getFile(fileConfig: GithubFileConfiguration): GetFileResponse {
        val response = client.get {
            url {
                appendPathSegments(segments = fileConfig.buildFilePath())
                parameter(key = BRANCH_PARAMETER_NAME, value = fileConfig.branch)
            }
        }
        return response.body()
    }

    suspend fun updateFile(fileConfig: GithubFileConfiguration, file: UpdateFileRequest): UpdateFileResponse {
        val response = client.put {
            url {
                appendPathSegments(segments = fileConfig.buildFilePath())
                parameter(key = BRANCH_PARAMETER_NAME, value = fileConfig.branch)
            }
            contentType(type = ContentType.Application.Json)
            setBody(body = file)
        }
        return response.body()
    }

    private fun GithubFileConfiguration.buildFilePath() = listOf(
        REPOS_PATH,
        owner,
        repo,
        CONTENTS_PATH,
        path,
    )

    private fun HttpClientConfig<*>.configureDefaultRequests() = install(plugin = DefaultRequest) {
        val (protocol, host, _, apiVersionHeader, authorizationHeader) = config
        url {
            this.protocol = URLProtocol.createOrDefault(name = protocol)
            this.host = host
        }
        headers {
            appendIfNameAbsent(name = HttpHeaders.Authorization, value = authorizationHeader)
            appendIfNameAbsent(name = HttpHeaders.Accept, value = apiVersionHeader)
        }
    }
}
