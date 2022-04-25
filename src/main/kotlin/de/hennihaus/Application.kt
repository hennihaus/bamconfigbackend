package de.hennihaus

import de.hennihaus.configurations.MongoConfiguration.mongoModule
import de.hennihaus.plugins.configureCors
import de.hennihaus.plugins.configureDependencyInjection
import de.hennihaus.plugins.configureErrorHandling
import de.hennihaus.plugins.configureMonitoring
import de.hennihaus.plugins.configureRouting
import io.ktor.server.application.Application
import io.ktor.server.cio.EngineMain
import org.koin.core.module.Module
import org.koin.ksp.generated.defaultModule

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module(
    koinModules: List<Module> = listOf(defaultModule, mongoModule)
) {
    configureCors()
    configureDependencyInjection(koinModules = koinModules)
    configureRouting()
    configureErrorHandling()
    configureMonitoring()
}
