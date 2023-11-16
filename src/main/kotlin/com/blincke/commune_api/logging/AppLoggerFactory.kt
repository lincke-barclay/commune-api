package com.blincke.commune_api.logging

import com.blincke.commune_api.logging.impl.ConsoleLogger

object AppLoggerFactory {
    fun getLoggerByTag(tag: String): AppLogger = ConsoleLogger(tag)
    fun getLogger(instance: Any) = getLoggerByTag(instance::class.java.simpleName)
}