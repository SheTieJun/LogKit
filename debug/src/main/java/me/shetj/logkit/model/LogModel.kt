package me.shetj.logkit.model

import me.shetj.logkit.LogLevel


internal data class LogModel(val logLevel: LogLevel, val tag: String, val logMessage: String, val time: String)
