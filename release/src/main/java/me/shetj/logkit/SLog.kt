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

import android.content.Context

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

    fun startLogsActivity() {

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

    fun logIAndToFile(log: String, isSave: Boolean = true, isCall: Boolean = true) {
    }

}

