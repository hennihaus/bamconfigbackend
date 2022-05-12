package de.hennihaus.utils

import java.util.logging.Logger
import kotlin.reflect.full.companionObject

fun <R : Any> R.logger(): Lazy<Logger> = lazy { logger(this.javaClass) }

private fun <T : Any> logger(forClass: Class<T>): Logger = Logger.getLogger(unwrapCompanionClass(forClass).name)

private fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return ofClass.enclosingClass?.takeIf {
        ofClass.enclosingClass.kotlin.companionObject?.java == ofClass
    } ?: ofClass
}
