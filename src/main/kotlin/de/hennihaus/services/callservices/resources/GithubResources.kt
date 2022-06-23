package de.hennihaus.services.callservices.resources

import de.hennihaus.services.callservices.resources.GithubPaths.DEFAULT_BRANCH_PARAMETER
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

object GithubPaths {
    const val REPO_PATH = "/repos/{owner}/{repo}"
    const val CONTENT_PATH = "/contents/{path}"

    const val DEFAULT_BRANCH_PARAMETER = "master"
}

@Serializable
class Github {

    @Serializable
    @Resource(GithubPaths.REPO_PATH)
    data class Repo(val parent: Github = Github(), val owner: String, val repo: String) {

        @Serializable
        @Resource(GithubPaths.CONTENT_PATH)
        data class File(val parent: Repo, val path: String, val ref: String? = DEFAULT_BRANCH_PARAMETER)
    }
}
