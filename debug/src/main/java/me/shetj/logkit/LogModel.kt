package me.shetj.logkit

import me.shetj.logkit.LogLevel.DEBUG
import me.shetj.logkit.LogLevel.ERROR
import me.shetj.logkit.LogLevel.INFO
import me.shetj.logkit.LogLevel.VERBOSE
import me.shetj.logkit.LogLevel.WARN


data class LogModel(val logLevel: LogLevel, val tag: String, val logMessage: String,val time:String)


enum class LogLevel {
    VERBOSE, DEBUG, INFO, WARN, ERROR
}

fun getLogPriorityInitials(logLevel: LogLevel): String {
    return when (logLevel) {
        DEBUG -> "D"
        ERROR -> "E"
        INFO -> "I"
        VERBOSE -> "V"
        WARN -> "W"
    }
}