package com.shetj.messenger

import android.os.Message

interface ICallBack {
    fun onCall(msg: Message?)
}