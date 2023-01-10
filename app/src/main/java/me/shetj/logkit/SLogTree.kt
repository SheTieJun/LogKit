package me.shetj.logkit

import android.util.Log
import timber.log.Timber

class SLogTree : Timber.Tree() {

    private val slog: SLog = SLog.getInstance()
    private val defaultTag = "SLog"

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val s = tag ?: defaultTag

        when (priority) {
            Log.VERBOSE -> slog.v(s, message)
            Log.INFO -> slog.i(s, message)
            Log.DEBUG -> slog.d(s, message)
            Log.WARN -> slog.w(s, message)
            Log.ERROR -> slog.e(s, message)
            else -> slog.v(s, message)
        }
    }
}