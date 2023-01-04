package com.shetj.messenger

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import com.shetj.messenger.SLogMessenger.Companion.TAG
import me.shetj.logkit.service.SLogServerService.Companion.MESSAGE_FROM_CLIENT

internal class ControllerImp private constructor() : Controller {

    private var callBack: ICallBack? = null
    private var isBind = false
    private var mContext: Context? = null
    private val mClientMessenger: Messenger = Messenger(MessengerHandler())
    private var mService: Messenger? = null
    private var packageName: String? = null

    inner class MessengerHandler : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            callBack?.onCall(msg)
        }
    }


    private fun initService(packageName: String): Int {
        try {
            Log.d(TAG, " sdk  initService  :packageName = $packageName, service = $SLogMessenger.SERVICE_NAME")
            val intent = Intent(SLogMessenger.SERVICE_NAME)
            intent.setPackage(packageName)
            intent.setClassName(packageName, SLogMessenger.SERVICE_NAME)
            isBind = mContext!!.bindService(
                intent, this, Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT
                        or Context.BIND_WAIVE_PRIORITY or Context.BIND_ABOVE_CLIENT
            )
            Log.d(TAG, " sdk  initService  :isBind = $isBind")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    override fun bindService(
        context: Context,
        packageName: String,
    ): Int {
        return if (!isBind) {
            mContext = context
            this.packageName = packageName
            initService(packageName)
        } else {
            Log.d(TAG, " sdk is has bindService ")
            0
        }
    }

    override fun sendToServer( level: Int, tag: String, msg: String) {
        if (!isBind) {
            Log.d(TAG, " sdk u must should bindService ")
            if (packageName != null && mContext != null) {
                bindService(mContext!!, packageName!!)
            }
            return
        }
        try {
            Message.obtain(null, MESSAGE_FROM_CLIENT).apply {
                arg1 = level
                data = Bundle().apply {
                    putString("SLog", msg)
                    putString("tag", tag)
                }
                replyTo = mClientMessenger
            }.also {
                mService?.send(it)
            }
        } catch (var3: RemoteException) {
            var3.printStackTrace()
        }
    }

    /**
     * unbind
     */
    override fun unBindService() {
        if (isBind) {
            packageName = null
            mContext!!.unbindService(this)
            isBind = false
        }
    }

    override fun setCallBack(callBack: ICallBack?) {
        this.callBack = callBack
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        mService = Messenger(service);
    }

    override fun onServiceDisconnected(name: ComponentName) {
        isBind = false
        mService = null
    }

    companion object {
        val instance: Controller?
            get() {
                if (mInstance == null) {
                    synchronized(ControllerImp::class.java) {
                        if (mInstance == null) {
                            mInstance = ControllerImp()
                        }
                    }
                }
                return mInstance
            }

        fun destroy() {
            mInstance?.unBindService()
            mInstance = null
        }

        @Volatile
        private var mInstance: Controller? = null
    }
}