package de.hennihaus.services.callservices

import de.hennihaus.configurations.GithubConfiguration
import de.hennihaus.configurations.GithubFileConfiguration
import de.hennihaus.models.generated.github.GetFileResponse
import de.hennihaus.models.generated.github.UpdateFileRequest
import de.hennihaus.models.generated.github.UpdateFileResponse
import de.hennihaus.services.callservices.resources.Github
import de.hennihaus.utils.configureMonitoring
import de.hennihaus.utils.configureRetryBehavior
import de.hennihaus.utils.configureSerialization
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.headers
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.util.appendIfNameAbsent
import org.koin.core.annotation.Single

@Single
class GithubCallServiceImpl(
    private val engine: HttpClientEngine,
    private val config: GithubConfiguration,
) : GithubCallService {

    private val client = HttpClient(engine = engine) {
        expectSuccess = true
        configureMonitoring()
        configureSerialization()
        configureRetryBehavior(maxRetries = config.maxRetries)
        configureDefaultRequests()
    }

    override suspend fun getFile(fileConfig: GithubFileConfiguration): GetFileResponse {
        val response = client.get(
            resource = fileConfig.buildFilePath(),
        )
        return response.body()
    }

    override suspend fun updateFile(fileConfig: GithubFileConfiguration, file: UpdateFileRequest): UpdateFileResponse {
        val response = client.put(
            resource = fileConfig.buildFilePath(),
        ) {
            contentType(type = ContentType.Application.Json)
            setBody(body = file)
        }
        return response.body()
    }

    private fun GithubFileConfiguration.buildFilePath() = Github.Repo.File(
        parent = Github.Repo(
            owner = owner,
            repo = repo,
        ),
        path = path,
        ref = branch,
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
