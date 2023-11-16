package com.blincke.commune_api.logging

interface AppLogger {
    fun info(msg: String, vararg trace: String)
    fun debug(msg: String, vararg trace: String)
    fun warn(msg: String, vararg trace: String)
    fun error(msg: String, vararg trace: String)
}