package de.hennihaus.plugins

import de.hennihaus.configurations.MongoConfiguration.DATABASE_HOST
import de.hennihaus.configurations.MongoConfiguration.DATABASE_NAME
import de.hennihaus.configurations.MongoConfiguration.DATABASE_PORT
import de.hennihaus.configurations.MongoConfiguration.mongoModule
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.ksp.generated.defaultModule
import org.koin.logger.slf4jLogger

fun Application.configureDependencyInjection(koinModules: List<Module>) {
    val properties = mapOf(
        DATABASE_NAME to (environment.config.propertyOrNull(DATABASE_NAME)?.getString() ?: ""),
        DATABASE_HOST to (environment.config.propertyOrNull(DATABASE_HOST)?.getString() ?: ""),
        DATABASE_PORT to (environment.config.propertyOrNull(DATABASE_PORT)?.getString() ?: "")
    )

    startKoin { initKoin(properties = properties, modules = koinModules) }
    environment.monitor.subscribe(ApplicationStopping) {
        stopKoin()
    }
}

fun KoinApplication.initKoin(
    properties: Map<String, String>,
    modules: List<Module> = listOf(defaultModule, mongoModule),
) {
    slf4jLogger()
    properties(values = properties)
    modules(modules = modules)
}
