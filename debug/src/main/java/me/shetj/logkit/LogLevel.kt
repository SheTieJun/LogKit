package me.shetj.logkit

import android.util.Log
import me.shetj.logkit.LogLevel.DEBUG
import me.shetj.logkit.LogLevel.ERROR
import me.shetj.logkit.LogLevel.INFO
import me.shetj.logkit.LogLevel.VERBOSE
import me.shetj.logkit.LogLevel.WARN

enum class LogLevel {
    VERBOSE, DEBUG, INFO, WARN, ERROR
}

internal fun getLogPriorityInitials(logLevel: LogLevel): String {
    return when (logLevel) {
        DEBUG -> "D"
        ERROR -> "E"
        INFO -> "I"
        VERBOSE -> "V"
        WARN -> "W"
    }
}

internal fun getLogIntByLevel(logLevel: LogLevel): Int {
    return when (logLevel) {
        DEBUG -> Log.DEBUG
        ERROR -> Log.ERROR
        INFO -> Log.INFO
        VERBOSE -> Log.VERBOSE
        WARN -> Log.WARN
    }
}

internal fun getLogLevelByInt(logLevel: Int): LogLevel {
    return when (logLevel) {
        Log.DEBUG -> DEBUG
        Log.ERROR -> ERROR
        Log.INFO -> INFO
        Log.VERBOSE -> VERBOSE
        Log.WARN -> WARN
        else -> DEBUG
    }
}