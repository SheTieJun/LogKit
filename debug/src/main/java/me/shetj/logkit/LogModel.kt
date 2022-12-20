package me.shetj.logkit

import me.shetj.logkit.LogPriority.DEBUG
import me.shetj.logkit.LogPriority.ERROR
import me.shetj.logkit.LogPriority.INFO
import me.shetj.logkit.LogPriority.VERBOSE
import me.shetj.logkit.LogPriority.WARN


data class LogModel(val logPriority: LogPriority, val tag: String, val logMessage: String)


enum class LogPriority{
    VERBOSE, DEBUG, INFO, WARN, ERROR
}

  fun getLogPriorityInitials(logPriority: LogPriority): String {
    return when (logPriority) {
        DEBUG -> "D"
        ERROR -> "E"
        INFO -> "I"
        VERBOSE -> "V"
        WARN -> "W"
        else -> ""
    }
}