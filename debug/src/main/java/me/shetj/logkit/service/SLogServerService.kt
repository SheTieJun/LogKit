package me.shetj.logkit.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import androidx.arch.core.executor.ArchTaskExecutor
import me.shetj.logkit.LogLevel
import me.shetj.logkit.SLog

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
                         val level = when(message.arg1){
                             0 ->{
                                 LogLevel.VERBOSE
                             }
                             1 ->{
                                 LogLevel.DEBUG
                             }
                             2 ->{
                                 LogLevel.INFO
                             }
                             3 ->{
                                 LogLevel.WARN
                             }
                             4 ->{
                                 LogLevel.ERROR
                             }
                             else->{
                                 LogLevel.DEBUG
                             }
                         }
                         SLog.getInstance().logWithFile(level,tag,msg,isCall = true)
                     }else{
                         when(message.arg1){
                             0 ->{
                                 SLog.getInstance().v(tag,msg)
                             }
                             1 ->{
                                 SLog.getInstance().d(tag,msg)
                             }
                             2 ->{
                                 SLog.getInstance().i(tag,msg)
                             }
                             3 ->{
                                 SLog.getInstance().w(tag,msg)
                             }
                             4 ->{
                                 SLog.getInstance().e(tag,msg)
                             }
                             else->{
                                 SLog.getInstance().d(tag,msg)
                             }
                         }
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