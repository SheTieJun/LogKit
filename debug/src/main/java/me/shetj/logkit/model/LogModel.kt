package me.shetj.logkit.model

import me.shetj.logkit.LogLevel
import me.shetj.logkit.LogLevel.DEBUG
import me.shetj.logkit.LogLevel.ERROR
import me.shetj.logkit.LogLevel.INFO
import me.shetj.logkit.LogLevel.VERBOSE
import me.shetj.logkit.LogLevel.WARN


internal data class LogModel(val logLevel: LogLevel, val tag: String, val logMessage: String, val time:String)




internal fun getLogPriorityInitials(logLevel: LogLevel): String {
    return when (logLevel) {
        DEBUG -> "D"
        ERROR -> "E"
        INFO -> "I"
        VERBOSE -> "V"
        WARN -> "W"
    }
}