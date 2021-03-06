package de.hennihaus

import de.hennihaus.configurations.brokerModule
import de.hennihaus.configurations.defaultModule
import de.hennihaus.configurations.githubModule
import de.hennihaus.configurations.mongoModule
import de.hennihaus.plugins.configureCors
import de.hennihaus.plugins.configureDependencyInjection
import de.hennihaus.plugins.configureErrorHandling
import de.hennihaus.plugins.configureMonitoring
import de.hennihaus.plugins.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import org.koin.core.module.Module

fun main() {
    embeddedServer(CIO, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module(vararg koinModules: Module = arrayOf(defaultModule, mongoModule, brokerModule, githubModule)) {
    configureMonitoring()
    configureDependencyInjection(koinModules = koinModules)
    configureCors()
    configureRouting()
    configureErrorHandling()
}
