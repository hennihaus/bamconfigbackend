package de.hennihaus

import de.hennihaus.configurations.Configuration.JAVA_UTIL_LOGGING_CONFIGURATION_FILE
import de.hennihaus.configurations.brokerModule
import de.hennihaus.configurations.defaultModule
import de.hennihaus.configurations.exposedModule
import de.hennihaus.configurations.githubModule
import de.hennihaus.plugins.configureCors
import de.hennihaus.plugins.configureDependencyInjection
import de.hennihaus.plugins.configureErrorHandling
import de.hennihaus.plugins.configureMonitoring
import de.hennihaus.plugins.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import org.koin.core.module.Module
import java.util.logging.LogManager

object Application {

    init {
        this::class.java.classLoader.getResourceAsStream(JAVA_UTIL_LOGGING_CONFIGURATION_FILE).use {
            LogManager.getLogManager().readConfiguration(it)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        embeddedServer(factory = CIO, port = 8080) {
            module()
        }.start(wait = true)
    }
}

fun Application.module(vararg koinModules: Module = arrayOf(defaultModule, exposedModule, brokerModule, githubModule)) {
    configureMonitoring()
    configureDependencyInjection(koinModules = koinModules)
    configureCors()
    configureRouting()
    configureErrorHandling()
}
