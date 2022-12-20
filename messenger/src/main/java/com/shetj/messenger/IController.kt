package com.shetj.messenger

import android.content.Context
import com.shetj.messenger.ICallBack

internal interface IController {
    fun bindService(context: Context, packageName: String): Int
    fun sendToServer(key: String, msg: String)
    fun unBindService()
    fun setCallBack(callBack: ICallBack?)
}