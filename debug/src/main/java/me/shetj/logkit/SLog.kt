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
import android.os.Build.VERSION_CODES.O
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import java.io.File
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import me.shetj.logkit.LogLevel.DEBUG
import me.shetj.logkit.LogLevel.ERROR
import me.shetj.logkit.LogLevel.INFO
import me.shetj.logkit.LogLevel.VERBOSE
import me.shetj.logkit.LogLevel.WARN
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
    private var mTag = "SLog"
    private val isEnabled = AtomicBoolean(false)
    private var isShowChat = false
    private val mVlogRepository = LogRepository()
    private val mViewModel: ContentViewModel = ContentViewModel(mVlogRepository)
    private val mSLogListeners: CopyOnWriteArrayList<SLogListener> by lazy { CopyOnWriteArrayList() }


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
            getInstance().v(msg = msg)
        }

        fun d(msg: String) {
            getInstance().d(msg = msg)
        }

        fun i(msg: String) {
            getInstance().i(msg = msg)
        }

        fun w(msg: String) {
            getInstance().w(msg = msg)
        }

        fun e(msg: String) {
            getInstance().e(msg = msg)
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
        if (mContext?.get() == null) {
            mContext = WeakReference(context.applicationContext)
            autoClear()
        }
    }

    fun start() {
        if (!isEnable()) {
            val isEnable = showLogLogo()
            isEnabled.set(isEnable)
            mSLogListeners.forEach {
                it.onEnableChange(isEnable)
            }
        }
    }

    /**
     * 是否是开启的状态
     * @return
     */
    fun isEnable(): Boolean {
        return isEnabled.get()
    }

    fun stop() {
        if (isEnable()) {
            isEnabled.set(false)
            hideLogLogo()
            hideLogChat()
            mVlogRepository.reset()
            mSLogListeners.forEach {
                it.onEnableChange(false)
            }
        }
    }

    fun startLogsActivity() {
        mContext?.get()?.let {
            it.startActivity(Intent(it, LogFilesActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    fun addLogListener(listener: SLogListener) {
        if (!mSLogListeners.contains(listener)) {
            mSLogListeners.add(listener)
        }
    }

    fun removeListener(listener: SLogListener) {
        mSLogListeners.remove(listener)
    }

    fun setTag(tag: String) {
        this.mTag = tag
    }

    @JvmOverloads
    fun v(tag: String = mTag, msg: String) {
        val model = LogModel(VERBOSE, tag, msg, nowTs())
        feed(model)
    }

    @JvmOverloads
    fun d(tag: String = mTag, msg: String) {
        val model = LogModel(DEBUG, tag, msg, nowTs())
        feed(model)
    }

    @JvmOverloads
    fun i(tag: String = mTag, msg: String) {
        val model = LogModel(INFO, tag, msg, nowTs())
        feed(model)
    }

    @JvmOverloads
    fun w(tag: String = mTag, msg: String) {
        val model = LogModel(WARN, tag, msg, nowTs())
        feed(model)
    }

    @JvmOverloads
    fun e(tag: String = mTag, msg: String) {
        val model = LogModel(ERROR, tag, msg, nowTs())
        feed(model)
    }


    /**
     * 记录并归档
     * @param log 日志信息
     * @param isCall 是否在view中显示
     */
    fun logWithFile(
        logLevel: LogLevel = INFO,
        tag: String = mTag,
        log: String,
        isCall: Boolean = true
    ) {
        val logModel = LogModel(logLevel, tag, log, nowTs())
        if (isCall) {
            feed(logModel)
        }
        saveLogToFile(logModel)
    }

    /**
     * Save log to file
     * 将日志保存到文件
     * @param log
     */
    internal fun saveLogToFile(log: LogModel) {
        outputToFile(log, getTodayLogFile(log.tag))
    }

    private fun showLogLogo(): Boolean {
        if (mContext?.get()?.checkFloatPermission(true) == true) {
            mContext?.get()?.let {
                if (logView == null) {
                    logView = LogLogo(it).apply {
                        setViewModel(mViewModel)
                    }
                }
                val slogLogoX = SPUtils.get(it, "slog_logo_x", 100) as Int
                val slogLogoY = SPUtils.get(it, "slog_logo_y", 100) as Int
                logView?.addToWindowManager {
                    x = slogLogoX
                    y = slogLogoY
                    width = -2
                    height = -2
                }
            }
            return true
        }
        return false
    }

    private fun hideLogLogo() {
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
            mSLogListeners.forEach {
                it.onChatShowChange(isShowChat)
            }
            return true
        }
        return false
    }

    internal fun hideLogChat() {
        if (isShowChat) {
            isShowChat = false
            logView?.hideChatAnim()
            logChat?.removeForWindowManager()
            mSLogListeners.forEach {
                it.onChatShowChange(isShowChat)
            }
        }
    }


    private fun feed(model: LogModel) {
        if (!isEnabled.get()) return
        mVlogRepository.feedLog(model)
    }

    //endregion logSetting

    @SuppressLint("RestrictedApi")
    internal fun outputToFile(log: LogModel, path: String? = getTodayLogFile(log.tag)) {
        ArchTaskExecutor.getIOThreadExecutor().execute {
            if (path.isNullOrEmpty()) return@execute
            try {
                File(path).appendText("${getLogPriorityInitials(log.logLevel)}:${log.tag}:${log.logMessage}$lineString")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun getTodayLogFile(tag: String): String {
        return getSavePath() + File.separator + "[$tag]" + today()
    }

    private fun getSavePath(): String {
        val logPath = (mContext?.get()?.filesDir?.absolutePath ?: "") + File.separator + "log"
        val dirFile = File(logPath)
        if (!dirFile.exists()) {
            dirFile.mkdir()
        }
        return logPath
    }

    internal fun getSaveLogs(): MutableList<LogFileInfo> {
        return File(getSavePath()).listFiles()
            ?.mapNotNull {
                LogFileInfo(
                    it.absolutePath,
                    it.name,
                    format.format(Date(it.lastModified())).toString()
                )
            }
            ?.toMutableList() ?: mutableListOf()
    }

    @SuppressLint("RestrictedApi")
    internal fun autoClear() {
        ArchTaskExecutor.getIOThreadExecutor().execute {
            File(getSavePath()).listFiles()
                ?.filter { System.currentTimeMillis() - it.lastModified() > 604_800_000 }?.forEach {
                    it.delete()
                }
        }
    }

    @SuppressLint("RestrictedApi")
    internal fun clear() {
        ArchTaskExecutor.getIOThreadExecutor().execute {
            File(getSavePath()).listFiles()
                ?.forEach {
                    it.delete()
                }
        }
    }

    private fun today(): String {
        return SimpleDateFormat("MM-dd HH时mm分", Locale.getDefault()).format(Date()).toString()
    }

    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())


    private fun nowTs(): String {
        return format.format(Date()).toString()
    }

    internal fun isShowing(): Boolean {
        return isShowChat
    }

    interface SLogListener {

        fun onEnableChange(enable: Boolean) {

        }

        fun onChatShowChange(isShow: Boolean) {

        }
    }
}

