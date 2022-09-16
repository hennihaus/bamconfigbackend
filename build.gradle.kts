import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import kotlinx.kover.api.CounterType
import kotlinx.kover.api.DefaultIntellijEngine
import kotlinx.kover.api.VerificationValueType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jetbrains.kotlinx.kover")
    id("com.google.devtools.ksp")
    id("io.gitlab.arturbosch.detekt")
    id("com.github.johnrengelman.shadow")
    id("org.openapi.generator")
}

group = "de.hennihaus"
version = "0.0.1"

application {
    mainClass.set("de.hennihaus.Application")
}

tasks.shadowJar {
    manifest {
        attributes("Main-Class" to "de.hennihaus.Application")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
    }
}

sourceSets {
    main {
        java.srcDirs(
            "build/generated/ksp/main/kotlin",
            "build/generated/openapi/src/main/kotlin",
        )
    }
}

repositories {
    mavenCentral()
}

configurations.all {
    // exclude kotlin test libraries
    exclude("org.jetbrains.kotlin", "kotlin-test")
    exclude("org.jetbrains.kotlin", "kotlin-test-common")
    exclude("org.jetbrains.kotlin", "kotlin-test-annotations-common")
    exclude("org.jetbrains.kotlin", "kotlin-test-junit")
}

dependencies {
    val ktorVersion: String by project
    val logbackVersion: String by project
    val exposedVersion: String by project
    val postgresVersion: String by project
    val passayVersion: String by project
    val kotestVersion: String by project
    val kotestLibrariesVersion: String by project
    val mockkVersion: String by project
    val junitVersion: String by project
    val koinVersion: String by project
    val koinAnnotationsVersion: String by project
    val kotlinDateTimeVersion: String by project
    val julToSlf4jVersion: String by project

    // ktor common plugins
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // ktor server plugins
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-resources-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cio-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")

    // ktor client plugins
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-resources-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")

    // koin plugins
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    compileOnly("io.insert-koin:koin-annotations:$koinAnnotationsVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    ksp("io.insert-koin:koin-ksp-compiler:$koinAnnotationsVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion")
    testImplementation("io.insert-koin:koin-test-junit5:$koinVersion")

    // exposed plugins
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("org.postgresql:postgresql:$postgresVersion")

    // utility plugins
    implementation("org.passay:passay:$passayVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:$kotlinDateTimeVersion")
    implementation("org.slf4j:jul-to-slf4j:$julToSlf4jVersion")

    // test plugins
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-ktor:$kotestLibrariesVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

ktlint {
    ignoreFailures.set(false)
    baseline.set(file("config/ktlint/baseline.xml"))
    filter {
        exclude("**/generated/**")
    }
}

detekt {
    config = files("config/detekt/detekt.yml")
    baseline = file("config/detekt/detekt-baseline.xml")
    source = files(
        DetektExtension.DEFAULT_SRC_DIR_JAVA,
        DetektExtension.DEFAULT_TEST_SRC_DIR_JAVA,
        "src/integrationTest/java",
        DetektExtension.DEFAULT_SRC_DIR_KOTLIN,
        DetektExtension.DEFAULT_TEST_SRC_DIR_KOTLIN,
        "src/integrationTest/kotlin",
    )
}

kover {
    engine.set(DefaultIntellijEngine)
}

koverMerged {
    enable()
    xmlReport {
        onCheck.set(true)
    }
    htmlReport {
        onCheck.set(true)
    }
    verify {
        onCheck.set(true)
        rule {
            bound {
                val minTestCoverageInPercent: String by project
                minValue = minTestCoverageInPercent.toInt()
                counter = CounterType.LINE
                valueType = VerificationValueType.COVERED_PERCENTAGE
            }
        }
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        val integrationTest by registering(JvmTestSuite::class) {
            dependencies {
                val testcontainersVersion: String by project

                implementation(project)
                implementation(sourceSets.test.get().output)
                implementation("org.testcontainers:testcontainers:$testcontainersVersion")
                implementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

tasks {
    init {
        dependsOn(ktlintApplyToIdea)
    }

    withType(GenerateTask::class) {
        inputSpec.set("$projectDir/config/openapi/spec/bamconfigbackend.json")
        skipValidateSpec.set(false)
        globalProperties.set(
            mapOf(
                "models" to "",
                "modelDocs" to "false",
                "apis" to "false",
            )
        )
    }

    val kotlinTypeMappings = mapOf(
        "string+date-time" to "KotlinxDateTime",
        "string+uuid" to "JavaUUID",
        "string+content-type" to "ContentType",
        "integer+http-status" to "HttpStatusCode",
        "string+url" to "JavaURI",
    )

    val kotlinImportMappings = mapOf(
        "KotlinxDateTime" to "kotlinx.datetime.LocalDateTime",
        "JavaUUID" to "java.util.UUID",
        "ContentType" to "io.ktor.http.ContentType",
        "HttpStatusCode" to "io.ktor.http.HttpStatusCode",
        "JavaURI" to "java.net.URI",
    )

    val generateCompleteOpenApi by registering(GenerateTask::class) {
        generatorName.set("openapi")
        outputDir.set("$projectDir/docs")
        configFile.set("$projectDir/config/openapi/spec/config.json")
    }

    val generateKotlinModel by registering(GenerateTask::class) {
        generatorName.set("kotlin")
        outputDir.set("$buildDir/generated/openapi")
        configFile.set("$projectDir/config/openapi/kotlin/config.json")
        modelPackage.set("de.hennihaus.models.generated")
        typeMappings.set(kotlinTypeMappings)
        importMappings.set(kotlinImportMappings)
    }

    val generateKotlinDataModel by registering(GenerateTask::class) {
        generatorName.set("kotlin")
        outputDir.set("$buildDir/bamconfigbackend/kotlin")
        templateDir.set("$projectDir/config/openapi/kotlin")
        configFile.set("$projectDir/config/openapi/kotlin/config.json")
        typeMappings.set(kotlinTypeMappings)
        importMappings.set(kotlinImportMappings)
        supportingFilesConstrainedTo.set(
            listOf(
                "settings.gradle",
                "gradlew.bat",
                "gradlew",
                "build.gradle",
                "gradle-wrapper.jar",
                "gradle-wrapper.properties",
            )
        )
    }

    val generateTypescriptDataModel by registering(GenerateTask::class) {
        generatorName.set("typescript-angular")
        outputDir.set("$buildDir/bamconfigbackend/typescript")
        templateDir.set("$projectDir/config/openapi/typescript")
        configFile.set("$projectDir/config/openapi/typescript/config.json")
        typeMappings.set(
            mapOf(
                "set" to "Array",
            )
        )
        supportingFilesConstrainedTo.set(
            listOf(
                "package.json",
                "tsconfig.json",
                "models.ts",
                "index.ts",
            )
        )
    }

    compileKotlin {
        dependsOn(generateKotlinModel)
        dependsOn(generateKotlinDataModel)
        dependsOn(generateTypescriptDataModel)
        dependsOn(generateCompleteOpenApi)
    }

    check {
        dependsOn(testing.suites.named("integrationTest"))
    }
}
