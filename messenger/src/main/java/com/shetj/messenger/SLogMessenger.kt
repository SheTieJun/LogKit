package com.shetj.messenger

import android.app.Application
import android.content.Context
import android.util.Log


open class SLogMessenger {

    private var msgTag = TAG
    private var isHideLogo = false
    private var isAutoHide = false
    private val activityLifecycleCallbacks by lazy { SLogActivityLifecycleCallbacks() }

    companion object {
        const val TAG = "SLogMessenger"
        const val SERVICE_NAME = "me.shetj.logkit.service.SLogServerService"

        @Volatile
        private var mInstance: SLogMessenger? = null

        @JvmStatic
        fun getInstance(): SLogMessenger {
            return mInstance ?: synchronized(SLogMessenger::class.java) {
                mInstance ?: SLogMessenger().also {
                    mInstance = it
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

    }

    private var mController: IController? = null


    fun bindService(
        context: Context,
        packageName: String,
    ): Int {
        autoHide(context,true)
        mController = ControllerImp.instance
        return mController!!.bindService(context, packageName)
    }

    fun autoHide(context: Context, isAuto: Boolean = true) {
        val application = context.applicationContext as Application
        isAutoHide = if (isAuto && !isAutoHide) {
            application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
            true
        } else {
            application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
            showLogo()
            false
        }
    }

    fun setTag(tag: String) {
        msgTag = tag
    }

    fun sendMsg(msg: String) {
        v(TAG, msg)
    }

    @JvmOverloads
    fun v(tag: String = msgTag, msg: String, pushFile: Boolean = false) {
        mController?.sendToServer(Log.VERBOSE, tag, msg, pushFile)
            ?: Log.i(TAG, "error : sendToServer: u should bindService first")
    }

    @JvmOverloads
    fun d(tag: String = msgTag, msg: String, pushFile: Boolean = false) {
        mController?.sendToServer(Log.DEBUG, tag, msg, pushFile)
            ?: Log.i(TAG, "error : sendToServer: u should bindService first")
    }

    @JvmOverloads
    fun i(tag: String = msgTag, msg: String, pushFile: Boolean = false) {
        mController?.sendToServer(Log.INFO, tag, msg, pushFile)
            ?: Log.i(TAG, "error : sendToServer: u should bindService first")
    }

    @JvmOverloads
    fun w(tag: String = msgTag, msg: String, pushFile: Boolean = false) {
        mController?.sendToServer(Log.WARN, tag, msg, pushFile)
            ?: Log.i(TAG, "error : sendToServer: u should bindService first")
    }

    @JvmOverloads
    fun e(tag: String = msgTag, msg: String, pushFile: Boolean = false) {
        mController?.sendToServer(Log.ERROR, tag, msg, pushFile)
            ?: Log.i(TAG, "error : sendToServer: u should bindService first")
    }

    @JvmOverloads
    fun log(priority: Int, tag: String, msg: String, pushFile: Boolean = false) {
        mController?.sendToServer(priority, tag, msg, pushFile)
            ?: Log.i(TAG, "error : sendToServer: u should bindService first")
    }

    fun unBindService() {
        mController?.unBindService()
    }

    fun setCallBack(callBack: ICallBack?) {
        mController?.setCallBack(callBack)
            ?: Log.i(TAG, "error : setCallBack: u should bindService first")
    }

    fun destroy() {
        mInstance?.unBindService()
        ControllerImp.destroy()
        mInstance = null
    }


    internal fun hideLogo() {
        if (!isHideLogo) {
            isHideLogo = true
            mController?.sendToServer(isHide = true)
                ?: kotlin.run {
                    Log.i(TAG, "error : sendToServer: u should bindService first")
                    isHideLogo = false
                }
        }
    }

    internal fun showLogo() {
        if (isHideLogo) {
            isHideLogo = false
            mController?.sendToServer(isHide = false)
                ?: kotlin.run {
                    Log.i(TAG, "error : sendToServer: u should bindService first")
                    isHideLogo = true
                }
        }
    }

}