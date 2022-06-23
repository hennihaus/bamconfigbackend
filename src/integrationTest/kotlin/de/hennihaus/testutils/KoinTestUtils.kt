package de.hennihaus.testutils

import org.koin.core.KoinApplication
import java.util.Properties

object KoinTestUtils {
    const val KOIN_TEST_PROPERTIES = "koin-test.properties"
}

fun KoinApplication.propertiesAsMap(fileName: String = KoinTestUtils.KOIN_TEST_PROPERTIES): Map<String, String> {
    val file = this::class.java.classLoader.getResourceAsStream(fileName)
    return Properties()
        .apply {
            load(file)
        }
        .map { (key, value) -> "$key" to "$value" }
        .toMap()
}
