package de.hennihaus.plugins

import de.hennihaus.configurations.Configuration.DEFAULT_CONFIG_FILE
import de.hennihaus.configurations.brokerModule
import de.hennihaus.configurations.defaultModule
import de.hennihaus.configurations.exposedModule
import de.hennihaus.configurations.githubModule
import de.hennihaus.utils.getHoconFileAsProperties
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDependencyInjection(
    configFilePath: String,
    vararg koinModules: Module,
) = install(plugin = Koin) {
    initKoin(
        configFilePath = configFilePath,
        modules = koinModules,
    )
}

fun KoinApplication.initKoin(
    configFilePath: String = DEFAULT_CONFIG_FILE,
    properties: Map<String, String> = emptyMap(),
    hoconProperties: Map<String, String> = getHoconFileAsProperties(file = configFilePath),
    vararg modules: Module = arrayOf(defaultModule, exposedModule, brokerModule, githubModule),
) {
    slf4jLogger()
    properties(values = hoconProperties)
    properties(values = properties)
    modules(modules = modules)
}
