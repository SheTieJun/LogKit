package me.shetj.logkit

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import androidx.annotation.MainThread
import androidx.arch.core.executor.ArchTaskExecutor
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
import me.shetj.logkit.model.LogFileInfo
import me.shetj.logkit.model.LogModel
import me.shetj.logkit.model.LogRepository
import me.shetj.logkit.ui.ContentViewModel
import me.shetj.logkit.ui.LogChat
import me.shetj.logkit.ui.LogFilesActivity
import me.shetj.logkit.ui.LogLogo
import me.shetj.logkit.utils.SLogActivityLifecycleCallbacks
import me.shetj.logkit.utils.SPUtils
import me.shetj.logkit.utils.isMainThread
import me.shetj.logkit.utils.lineString

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
    private val formatFileName = SimpleDateFormat("MM-dd HH时mm分", Locale.getDefault())
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private var isHideLogo = false
    private var isAutoHide = false
    private val activityLifecycleCallbacks by lazy {  SLogActivityLifecycleCallbacks() }

    companion object {

        @Volatile
        private var sLog: SLog? = null

        @JvmStatic
        @MainThread
        fun init(context: Context): SLog {
            if (!isMainThread()){
                error("请在主线程调用`SLog.init`")
            }
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
    fun initContext(context: Context,autoHide:Boolean = true) {
        if (mContext?.get() == null) {
            mContext = WeakReference(context.applicationContext)
            autoHide(autoHide)
            autoClearLogFile()
        }
    }

    fun autoHide(isAuto:Boolean = true){
        if (mContext?.get() == null) {
            throw IllegalArgumentException(" should init first 需要先初始化 SLog.init(content)")
        }
        val application = mContext?.get()!!.applicationContext as Application
        isAutoHide = if (isAuto && !isAutoHide){
            application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
            true
        }else{
            application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
            showLogo()
            false
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

    internal fun startLogsActivity() {
        mContext?.get()?.let {
            it.startActivity(Intent(it, LogFilesActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    fun addLogListener(listener: SLogListener) {
        if (!mSLogListeners.contains(listener)) {
            mSLogListeners.add(listener)
            listener.onEnableChange(isEnable())
            listener.onChatShowChange(isShowing())
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

    @JvmOverloads
    fun log(level: LogLevel, tag: String = mTag, msg: String) {
        val model = LogModel(level, tag, msg, nowTs())
        feed(model)
    }

    @JvmOverloads
    fun log(priority: Int, tag: String = mTag, msg: String) {
        val model = LogModel(getLogLevelByInt(priority), tag, msg, nowTs())
        feed(model)
    }

    /**
     * 记录并归档
     * @param log 日志信息
     * @param isCall 是否在view中显示
     */
    fun logFile(
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

    fun logFile(
        priority: Int = Log.INFO,
        tag: String = mTag,
        log: String,
        isCall: Boolean = true
    ) {
        val logModel = LogModel(getLogLevelByInt(priority), tag, log, nowTs())
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
    private fun saveLogToFile(log: LogModel) {
        outputToFile(log, getTodayLogFile(log.tag))
    }

    private fun showLogLogo(): Boolean {
        if (mContext?.get() == null) {
            throw IllegalArgumentException(" should init first 需要先初始化 SLog.init(content)")
        }
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
            mViewModel.unReadCount.postValue(0)
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
        if (!isShowing()) {
            val value = mViewModel.unReadCount.value ?: 0
            mViewModel.unReadCount.postValue(value + 1)
        }
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
            ?.filter { System.currentTimeMillis() - it.lastModified() <= 604_800_000 }
            ?.mapNotNull {
                LogFileInfo(
                    it.absolutePath,
                    it.name,
                    format.format(Date(it.lastModified())).toString()
                )
            }?.toMutableList() ?: mutableListOf()
    }

    @SuppressLint("RestrictedApi")
    internal fun autoClearLogFile() {
        ArchTaskExecutor.getIOThreadExecutor().execute {
            File(getSavePath()).listFiles()
                ?.filter { System.currentTimeMillis() - it.lastModified() > 604_800_000 }?.forEach {
                    it.delete()
                }
        }
    }

    @SuppressLint("RestrictedApi")
    internal fun clearLogFile() {
        ArchTaskExecutor.getIOThreadExecutor().execute {
            File(getSavePath()).listFiles()
                ?.forEach {
                    it.delete()
                }
        }
    }

    private fun today(): String {
        return formatFileName.format(Date()).toString()
    }

    private fun nowTs(): String {
        return format.format(Date()).toString()
    }

    internal fun isShowing(): Boolean {
        return isShowChat
    }

    internal fun hideLogo() {
        if (!isHideLogo && isEnable()) {
            hideLogLogo()
            hideLogChat()
            isHideLogo = true
        }
    }

    internal fun showLogo() {
        if (isHideLogo && isEnable()) {
            showLogLogo()
            isHideLogo = false
        }
    }

    interface SLogListener {

        fun onEnableChange(enable: Boolean) {

        }

        fun onChatShowChange(isShow: Boolean) {

        }
    }
}

