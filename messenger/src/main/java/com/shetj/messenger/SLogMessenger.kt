package com.shetj.messenger

import android.content.Context
import android.util.Log


open class SLogMessenger {

    private var msgTag = TAG

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
        mController = ControllerImp.instance
        return mController!!.bindService(context, packageName)
    }

    fun setTag(tag: String) {
        msgTag = tag
    }

    fun sendMsg(msg: String) {
        v(TAG, msg)
    }

    @JvmOverloads
    fun v(tag: String = msgTag, msg: String, pushFile: Boolean = false) {
        mController?.sendToServer(0, tag, msg, pushFile)
            ?: Log.i(TAG, "error : sendToServer: u should bindService first")
    }

    @JvmOverloads
    fun d(tag: String = msgTag, msg: String, pushFile: Boolean = false) {
        mController?.sendToServer(1, tag, msg, pushFile)
            ?: Log.i(TAG, "error : sendToServer: u should bindService first")
    }

    @JvmOverloads
    fun i(tag: String = msgTag, msg: String, pushFile: Boolean = false) {
        mController?.sendToServer(2, tag, msg, pushFile)
            ?: Log.i(TAG, "error : sendToServer: u should bindService first")
    }

    @JvmOverloads
    fun w(tag: String = msgTag, msg: String, pushFile: Boolean = false) {
        mController?.sendToServer(3, tag, msg, pushFile)
            ?: Log.i(TAG, "error : sendToServer: u should bindService first")
    }

    @JvmOverloads
    fun e(tag: String = msgTag, msg: String, pushFile: Boolean = false) {
        mController?.sendToServer(4, tag, msg, pushFile)
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

}