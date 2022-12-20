package me.shetj.logkit

import android.util.Log
import timber.log.Timber

class SLogTree : Timber.Tree() {

    private val slog: SLog = SLog.getInstance()
    private val defaultTag = ""

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val tag = tag ?: defaultTag

        when (priority) {
            Log.VERBOSE -> slog.v(tag, message)
            Log.INFO -> slog.i(tag, message)
            Log.DEBUG -> slog.d(tag, message)
            Log.WARN -> slog.w(tag, message)
            Log.ERROR -> slog.e(tag, message)
            else -> slog.v(tag, message)
        }
    }
}