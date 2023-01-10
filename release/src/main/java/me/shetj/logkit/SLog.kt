package me.shetj.logkit

import android.content.Context
import me.shetj.logkit.LogLevel.INFO

@Suppress("unused", "UNUSED_PARAMETER")
class SLog private constructor() {
    private var mTag = "Debug"

    companion object {

        @Volatile
        private var sLog: SLog? = null

        @JvmStatic
        fun init(context: Context): SLog {
            return getInstance()
        }

        @JvmStatic
        fun getInstance(): SLog {
            return sLog ?: synchronized(SLog::class.java) {
                SLog().also {
                    sLog = it
                }
            }
        }

        fun v(msg: String) {
        }

        fun d(msg: String) {
        }

        fun i(msg: String) {
        }

        fun w(msg: String) {
        }

        fun e(msg: String) {
        }

        fun v(tag: String, msg: String) {
        }

        fun d(tag: String, msg: String) {
        }

        fun i(tag: String, msg: String) {
        }

        fun w(tag: String, msg: String) {
        }

        fun e(tag: String, msg: String) {
        }
    }


    //region 必须设置
    fun initContext(context: Context) {
    }

    fun start() {

    }

    fun stop() {

    }

    fun setTag(tag: String) {
    }

    fun v(msg: String) {
    }

    fun d(msg: String) {
    }

    fun i(msg: String) {
    }

    fun w(msg: String) {
    }

    fun e(msg: String) {
    }


    fun v(tag: String = mTag, msg: String) {
    }

    fun d(tag: String = mTag, msg: String) {
    }

    fun i(tag: String = mTag, msg: String) {
    }

    fun w(tag: String = mTag, msg: String) {
    }

    fun e(tag: String = mTag, msg: String) {
    }

    fun logWithFile(
        logLevel: LogLevel = INFO,
        tag: String = mTag,
        log: String,
        isCall: Boolean = true
    ) {}

    fun addLogListener(listener: SLogListener) {

    }

    fun removeListener(listener: SLogListener) {

    }


    interface SLogListener {

        fun onEnableChange(enable: Boolean) {

        }

        fun onChatShowChange(isShow: Boolean) {

        }
    }
}

