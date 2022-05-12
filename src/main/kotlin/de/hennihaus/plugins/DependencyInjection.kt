package de.hennihaus.plugins

import de.hennihaus.configurations.BrokerConfiguration.brokerModule
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
    val properties = environment.config.keys().flatMap { key ->
        runCatching {
            listOf(key to environment.config.property(path = key).getString())
        }.getOrElse {
            environment.config.property(path = key).getList().mapIndexed { index, property ->
                "$key[$index]" to property
            }
        }
    }

    startKoin {
        initKoin(properties = properties.toMap(), modules = koinModules)
    }
    environment.monitor.subscribe(ApplicationStopping) {
        stopKoin()
    }
}

fun KoinApplication.initKoin(
    properties: Map<String, String>,
    modules: List<Module> = listOf(defaultModule, mongoModule, brokerModule),
) {
    slf4jLogger()
    properties(values = properties)
    modules(modules = modules)
}
