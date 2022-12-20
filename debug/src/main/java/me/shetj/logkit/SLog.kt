/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.logkit

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.arch.core.executor.ArchTaskExecutor
import java.io.File
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import me.shetj.logkit.LogPriority.DEBUG
import me.shetj.logkit.LogPriority.ERROR
import me.shetj.logkit.LogPriority.INFO
import me.shetj.logkit.LogPriority.VERBOSE
import me.shetj.logkit.LogPriority.WARN
import me.shetj.logkit.floatview.FloatKit.checkFloatPermission


val lineString: String? = System.getProperty("line.separator")

/**
 * debug 功能扩展 必须开启debug的情况下
 * 1. 对一些日志进行特殊的保留
 * 2. 对http请求日志进行文件输出
 */
class SLog private constructor() {

    private var mContext: WeakReference<Context>? = null
    private var logView: LogLogo? = null
    private var logChat: LogChat? = null
    private var mTag = "Debug"
    private val isEnabled = AtomicBoolean(false)
    private var isShowChat = false
    private val mVlogRepository = LogRepository()
    private val mViewModel: ContentViewModel = ContentViewModel(mVlogRepository)

    companion object {

        @Volatile
        private var sLog: SLog? = null

        @JvmStatic
        fun init(context: Context): SLog {
            return getInstance().also {
                it.initContext(context)
            }
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
            getInstance().v(msg)
        }

        fun d(msg: String) {
            getInstance().d(msg)
        }

        fun i(msg: String) {
            getInstance().i(msg)
        }

        fun w(msg: String) {
            getInstance().w(msg)
        }

        fun e(msg: String) {
            getInstance().e(msg)
        }



        fun v(tag: String, msg: String) {
            getInstance().v(tag, msg)
        }

        fun d(tag: String, msg: String) {
            getInstance().d(tag, msg)
        }

        fun i(tag: String, msg: String) {
            getInstance().i(tag, msg)
        }

        fun w(tag: String, msg: String) {
            getInstance().w(tag, msg)
        }

        fun e(tag: String, msg: String) {
            getInstance().e(tag, msg)
        }
    }


    //region 必须设置
    fun initContext(context: Context) {
        mContext = WeakReference(context.applicationContext)
    }

    fun start() {
        val isEnable = showLogLogo()
        isEnabled.set(isEnable)
    }

    fun stop() {
        isEnabled.set(false)
        hideLogLogo()
        hideLogChat()
        mVlogRepository.reset()
    }

    fun startLogsActivity() {
        mContext?.get()?.let {
            it.startActivity(Intent(it, LogFilesActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    fun setTag(tag: String) {
        this.mTag = tag
    }

    fun v(msg: String) {
        v(mTag, msg)
    }

    fun d(msg: String) {
        d(mTag, msg)
    }

    fun i(msg: String) {
        i(mTag, msg)
    }

    fun w(msg: String) {
        w(mTag, msg)
    }

    fun e(msg: String) {
        e(mTag, msg)
    }


    fun v(tag: String = mTag, msg: String) {
        val model = LogModel(VERBOSE, tag, msg)
        feed(model)
    }

    fun d(tag: String = mTag, msg: String) {
        val model = LogModel(DEBUG, tag, msg)
        feed(model)
    }

    fun i(tag: String = mTag, msg: String) {
        val model = LogModel(INFO, tag, msg)
        feed(model)
    }

    fun w(tag: String = mTag, msg: String) {
        val model = LogModel(WARN, tag, msg)
        feed(model)
    }

    fun e(tag: String = mTag, msg: String) {
        val model = LogModel(ERROR, tag, msg)
        feed(model)
    }


    /**
     * Log and to file
     * 记录并归档
     * @param log 日志信息
     * @param isSave 是否归档
     * @param isCall 是否在view中显示
     */
    fun logIAndToFile(log: String, isSave: Boolean = true, isCall: Boolean = true) {
        val logModel = LogModel(INFO, mTag, log)
        if (isCall) {
            feed(logModel)
        }
        if (isSave) {
            saveLogToFile(logModel)
        }
    }

    /**
     * Save log to file
     * 将日志保存到文件
     * @param log
     */
    internal fun saveLogToFile(log: LogModel) {
        outputToFile(log, getTodayLogFile())
    }

    internal fun showLogLogo(): Boolean {
        if (mContext?.get()?.checkFloatPermission(true) == true) {
            mContext?.get()?.let {
                if (logView == null) {
                    logView = LogLogo(it).apply {
                        setViewModel(mViewModel)
                    }
                }
                logView?.addToWindowManager {
                    x = 100
                    y = 100
                    width = -2
                    height = -2
                }
            }
            return true
        }
        return false
    }

    internal fun hideLogLogo() {
        logView?.removeForWindowManager()
    }

    internal fun showLogChat(): Boolean {
        if (mContext?.get()?.checkFloatPermission(true) == true && !isShowChat) {
            mContext?.get()?.let {
                if (logChat == null) {
                    logChat = LogChat(it).apply {
                        setViewModel(mViewModel)
                    }
                }
                logChat?.addToWindowManager {
                    x = 0
                    y = 0
                    width = -1
                    height = -1
                }
            }
            logView?.showChatAnim()
            isShowChat = true
            return true
        }
        return false
    }

    internal fun hideLogChat() {
        if (isShowChat) {
            isShowChat = false
            logView?.hideChatAnim()
            logChat?.removeForWindowManager()
        }
    }


    private fun feed(model: LogModel) {
        if (!isEnabled.get()) return
        mVlogRepository.feedLog(model)
    }

    //endregion logSetting

    @SuppressLint("RestrictedApi")
    internal  fun outputToFile(log: LogModel, path: String? = getTodayLogFile()) {
        ArchTaskExecutor.getIOThreadExecutor().execute {
            if (path.isNullOrEmpty()) return@execute
            try {
                File(path).appendText("${getLogPriorityInitials(log.logPriority)}:${log.tag}:${log.logMessage}$lineString")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun getTodayLogFile(): String {
        return getSavePath() + File.separator + today()
    }

    private fun getSavePath(): String {
        val logPath = (mContext?.get()?.filesDir?.absolutePath ?: "") + File.separator + "log"
        val dirFile = File(logPath)
        if (!dirFile.exists()) {
            dirFile.mkdir()
        }
        return logPath
    }

    internal fun getSaveLogs(): MutableList<String> {
        return File(getSavePath()).listFiles()?.mapNotNull { it.absolutePath }?.toMutableList() ?: mutableListOf()
    }


    private fun today(): String {
        return SimpleDateFormat("yyyy-MM-dd HH时mm分", Locale.getDefault()).format(Date()).toString()
    }

    internal fun isShowing(): Boolean {
        return isShowChat
    }
}

