package de.hennihaus.plugins

import de.hennihaus.configurations.brokerModule
import de.hennihaus.configurations.defaultModule
import de.hennihaus.configurations.githubModule
import de.hennihaus.configurations.mongoModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.Route
import io.ktor.server.routing.Routing
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.environmentProperties
import org.koin.fileProperties
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.koin.ktor.ext.getProperty as property

fun Application.configureDependencyInjection(vararg koinModules: Module) = install(plugin = Koin) {
    initKoin(modules = koinModules)
}

fun KoinApplication.initKoin(
    properties: Map<String, String> = emptyMap(),
    vararg modules: Module = arrayOf(defaultModule, mongoModule, brokerModule, githubModule),
) {
    slf4jLogger()
    environmentProperties()
    fileProperties()
    properties(values = properties)
    modules(modules = modules)
}

fun <T> Application.getProperty(key: String): T = property(key) ?: throw PropertyNotFoundException(key = key)

fun <T> Routing.getProperty(key: String): T = property(key) ?: throw PropertyNotFoundException(key = key)

fun <T> Route.getProperty(key: String): T = property(key) ?: throw PropertyNotFoundException(key = key)
