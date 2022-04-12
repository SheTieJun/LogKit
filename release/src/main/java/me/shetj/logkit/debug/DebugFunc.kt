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
import androidx.arch.core.executor.ArchTaskExecutor
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * debug 功能扩展 必须开启debug的情况下
 * 1. 对一些日志进行特殊的保留
 * 2. 对http请求日志进行文件输出
 */
class DebugFunc private constructor() {

    private var mContext: WeakReference<Context>? = null
    private val list: CopyOnWriteArrayList<LogCurrentCall> = CopyOnWriteArrayList()

    companion object {

        @Volatile
        private var mDebugFunc: DebugFunc? = null

        @JvmStatic
        fun getInstance(): DebugFunc {
            return mDebugFunc ?: DebugFunc().also {
                mDebugFunc = it
            }
        }
    }

    //region 必须设置
    fun initContext(context: Context):Boolean {
        return false
    }
    //endregion

    fun addCall(logCurrentCall: LogCurrentCall){
    }

    fun remove(logCurrentCall: LogCurrentCall){
    }

    fun saveLogToFile(info: String?) {
    }

    fun addFlotLogView() {

    }

    fun startLogsActivity(){

    }

    fun removeFlotLogView() {

    }
    //endregion logSetting

    @SuppressLint("RestrictedApi")
    fun outputToFile(info: String?, path: String? = getTodayLogFile()) {
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
        return SimpleDateFormat("yyyy-MM-dd HH时mm分").format(Date()).toString()
    }
}


interface LogCurrentCall {

    fun log(string: String?)

}