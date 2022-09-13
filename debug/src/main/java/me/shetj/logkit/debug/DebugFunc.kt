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
package me.shetj.logkit.debug

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import me.shetj.logkit.debug.floatview.FloatKit.checkFloatPermission


val lineString: String? = System.getProperty("line.separator")

/**
 * debug 功能扩展 必须开启debug的情况下
 * 1. 对一些日志进行特殊的保留
 * 2. 对http请求日志进行文件输出
 */
class DebugFunc private constructor() {

    private var mContext: WeakReference<Context>? = null
    private val list: CopyOnWriteArrayList<LogCurrentCall> = CopyOnWriteArrayList()
    private var logView: LogView? = null

    companion object {

        @Volatile
        private var mDebugFunc: DebugFunc? = null

        @JvmStatic
        fun getInstance(): DebugFunc {
            return mDebugFunc ?: synchronized(DebugFunc::class.java) {
                DebugFunc().also {
                    mDebugFunc = it
                }
            }
        }
    }


    //region 必须设置
    fun initContext(context: Context): Boolean {
        mContext = WeakReference(context.applicationContext)
        return true
    }
    //endregion

    fun addCall(logCurrentCall: LogCurrentCall) {
        list.add(logCurrentCall)
    }

    fun remove(logCurrentCall: LogCurrentCall) {
        list.remove(logCurrentCall)
    }

    /**
     * Log and to file
     * 记录并归档
     * @param log 日志信息
     * @param isSave 是否归档
     * @param isCall 是否在view中显示
     */
    fun logAndToFile(log: String, isSave: Boolean = true, isCall: Boolean = true) {
        Log.i("DebugFunc", log)
        if (isCall) {
            list.forEach { logCurrentCall ->
                logCurrentCall.log(log)
            }
        }
        if (isSave) {
            saveLogToFile(log)
        }
    }

    /**
     * Save log to file
     * 将日志保存到文件
     * @param log
     */
    fun saveLogToFile(log: String) {
        outputToFile(log, getTodayLogFile())
    }

    fun addFlotLogView() {
        if (mContext?.get()?.checkFloatPermission(true) == true) {
            mContext?.get()?.let {
                if (logView == null) {
                    logView = LogView(it)
                }
                logView?.addToWindowManager {
                    x = 0
                    y = 0
                    width = 250 * 3
                    height = 250 * 3
                }
            }
        }
    }

    fun removeFlotLogView() {
        logView?.removeForWindowManager()
    }

    fun startLogsActivity() {
        mContext?.get()?.let {
            it.startActivity(Intent(it, LogFilesActivity::class.java).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }

    //endregion logSetting

    @SuppressLint("RestrictedApi")
    fun outputToFile(info: String?, path: String? = getTodayLogFile()) {
        ArchTaskExecutor.getIOThreadExecutor().execute {
            if (info.isNullOrEmpty()) return@execute
            if (path.isNullOrEmpty()) return@execute
            try {
                File(path).appendText("$info$lineString")
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

    fun getSaveLogs(): MutableList<String> {
        return File(getSavePath()).listFiles()?.mapNotNull { it.absolutePath }?.toMutableList() ?: mutableListOf()
    }


    private fun today(): String {
        return SimpleDateFormat("yyyy-MM-dd HH时mm分", Locale.getDefault()).format(Date()).toString()
    }

}


fun AppCompatActivity.showDebugView() {
    lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Event) {
            if (event == ON_RESUME) {
                DebugFunc.getInstance().addFlotLogView()
            }
            if (event == ON_STOP) {
                DebugFunc.getInstance().removeFlotLogView()
            }
        }
    })
}


interface LogCurrentCall {

    fun log(string: String?)

}
