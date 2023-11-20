package com.blincke.commune_api.logging.impl

import com.blincke.commune_api.logging.AppLogger
import org.slf4j.LoggerFactory

class ConsoleLogger(private val tag: String) : AppLogger {
    private val logger = LoggerFactory.getLogger(tag)

    override fun info(msg: String, vararg trace: String) {
        logger.info("{} - Trace: {}", msg, *trace)
    }

    override fun debug(msg: String, vararg trace: String) {
        logger.debug("{} - Trace: {}", msg, *trace)
    }

    override fun warn(msg: String, vararg trace: String) {
        logger.warn("{} - Trace: {}", msg, *trace)
    }

    override fun error(msg: String, vararg trace: String) {
        logger.error("{} - Trace: {}", msg, *trace)
    }
}