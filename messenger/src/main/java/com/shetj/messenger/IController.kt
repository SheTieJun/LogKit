package com.shetj.messenger

import android.content.Context
import com.shetj.messenger.ICallBack
import java.util.logging.Level

internal interface IController {
    fun bindService(context: Context, packageName: String): Int
    fun sendToServer(level: Int,tag:String, msg: String,pushFile:Boolean)
    fun sendToServer(isHide:Boolean)
    fun unBindService()
    fun setCallBack(callBack: ICallBack?)
}