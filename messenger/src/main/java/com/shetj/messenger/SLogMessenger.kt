package com.shetj.messenger

import android.content.Context
import android.util.Log
import me.shetj.logkit.service.SLogServerService.Companion.KEY_MSG


open class SLogMessenger {

    companion object {
        const val TAG = "SLogMessenger"
        const val SERVICE_NAME ="me.shetj.logkit.service.SLogServerService"

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
    }

    private var mController: IController? = null


    fun bindService(
        context: Context,
        packageName: String,
    ): Int {
        mController = ControllerImp.instance
        return mController!!.bindService(context, packageName)
    }

    fun sendMsg(msg: String) {
        mController?.sendToServer(KEY_MSG, msg)
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