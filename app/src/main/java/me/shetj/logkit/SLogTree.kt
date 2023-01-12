package me.shetj.logkit

import timber.log.Timber

class SLogTree : Timber.Tree() {

    private val slog: SLog = SLog.getInstance()
    private val defaultTag = "SLog"

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val s = tag ?: defaultTag
        slog.log(priority,s,message)
    }
}