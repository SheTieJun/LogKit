package me.shetj.logkit.service

import android.app.Service
import android.os.Messenger
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import me.shetj.logkit.SLog

class SLogServerService : Service() {

    companion object{
        const val MESSAGE_FROM_CLIENT = 6001
        const val KEY_MSG = "SLog"
    }

    override fun onCreate() {
        super.onCreate()
        SLog.init(this)
        SLog.getInstance().start()
    }

    private class MessengerHandler : Handler() {
        override fun handleMessage(message: Message) {
            when (message.what) {
                 MESSAGE_FROM_CLIENT -> {
                    val string = message.data.getString(KEY_MSG)
                    string?.let { SLog.getInstance().d(it) }
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