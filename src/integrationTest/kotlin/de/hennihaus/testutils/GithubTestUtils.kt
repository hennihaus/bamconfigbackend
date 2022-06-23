package de.hennihaus.testutils

import de.hennihaus.configurations.GithubConfiguration
import de.hennihaus.configurations.GithubFileConfiguration
import de.hennihaus.models.generated.github.GetFileResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json

object GithubTestUtils {

    suspend fun getCurrentSha(fileConfig: GithubFileConfiguration, githubConfig: GithubConfiguration): String {
        val (owner, repo, path, branch) = fileConfig
        val (protocol, host, _, apiVersionHeader, authorizationHeader) = githubConfig

        return HttpClient(CIO) { install(plugin = ContentNegotiation) { json() } }.use {
            val response = it.get {
                url {
                    this.protocol = URLProtocol.createOrDefault(name = protocol)
                    this.host = host
                    this.encodedPath = "/repos/$owner/$repo/contents/$path"
                    this.parameters.append(name = "ref", value = branch)
                }
                headers {
                    header(key = HttpHeaders.Authorization, value = authorizationHeader)
                    header(key = HttpHeaders.Accept, value = apiVersionHeader)
                }
            }
            response.body<GetFileResponse>().sha
        }
    }
}
