package de.hennihaus.configurations

import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule as generatedModule

val defaultModule = module {
    includes(generatedModule)

    single {
        CIO.create()
    }
}

object Configuration {
    const val PASSWORD_LENGTH = "ktor.common.passwordLength"
    const val API_VERSION = "ktor.deployment.apiVersion"
    const val JAVA_UTIL_LOGGING_CONFIGURATION_FILE = "logging.properties"
}
