package me.shetj.logkit.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import me.shetj.logkit.SLog
import me.shetj.logkit.model.getLogLevelByInt

class SLogServerService : Service() {

    companion object{
        const val MESSAGE_FROM_CLIENT = 6001
        const val KEY_MSG = "SLog"
        const val KEY_TAG = "tag"
    }

    override fun onCreate() {
        super.onCreate()
        SLog.init(this)
        SLog.getInstance().start()
    }

    private class MessengerHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                 MESSAGE_FROM_CLIENT -> {
                     val msg = message.data.getString(KEY_MSG)?:return
                     val tag = message.data.getString(KEY_TAG)?:return
                     val pushFile = message.data.getBoolean("pushFile",false)
                     if (pushFile){
                         SLog.getInstance().logFile(getLogLevelByInt(message.arg1),tag,msg, true)
                     }else{
                         SLog.getInstance().log(getLogLevelByInt(message.arg1),tag,msg)
                     }
                }
                else -> super.handleMessage(message)
            }
        }
    }

    private val mMessenger = Messenger(MessengerHandler())


    override fun onBind(intent: Intent): IBinder? {
        return mMessenger.binder
    }
}