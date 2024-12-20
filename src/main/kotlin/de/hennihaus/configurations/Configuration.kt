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
    const val DEFAULT_CONFIG_FILE = "application.conf"
    const val DEFAULT_ZONE_ID = "Europe/Berlin"

    const val ALLOWED_FIRST_FRONTEND_PROTOCOL = "ktor.cors.firstFrontend.allowedProtocol"
    const val ALLOWED_FIRST_FRONTEND_HOST = "ktor.cors.firstFrontend.allowedHost"
    const val ALLOWED_SECOND_FRONTEND_PROTOCOL = "ktor.cors.secondFrontend.allowedProtocol"
    const val ALLOWED_SECOND_FRONTEND_HOST = "ktor.cors.secondFrontend.allowedHost"
    const val ALLOWED_SWAGGER_PROTOCOL = "ktor.cors.swagger.allowedProtocol"
    const val ALLOWED_SWAGGER_HOST = "ktor.cors.swagger.allowedHost"
    const val PASSWORD_LENGTH = "common.passwordLength"
    const val API_VERSION = "ktor.application.apiVersion"
    const val JAVA_UTIL_LOGGING_CONFIGURATION_FILE = "logging.properties"
}

object RoutesConfiguration {
    const val CURSOR_QUERY_PARAMETER = "cursor"
    const val LIMIT_QUERY_PARAMETER = "limit"

    const val DEFAULT_LIMIT_PARAMETER = 10
}
