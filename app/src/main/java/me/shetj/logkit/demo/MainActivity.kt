package me.shetj.logkit.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.delay
import me.shetj.base.ktx.launch
import me.shetj.base.ktx.logE
import me.shetj.base.ktx.logI
import me.shetj.logkit.LogLevel.DEBUG
import me.shetj.logkit.LogLevel.ERROR
import me.shetj.logkit.LogLevel.INFO
import me.shetj.logkit.LogLevel.VERBOSE
import me.shetj.logkit.LogLevel.WARN
import me.shetj.logkit.SLog
import me.shetj.logkit.SLogTree
import me.shetj.logkit.demo.R.layout
import timber.log.Timber

class MainActivity : AppCompatActivity(),SLog.SLogListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        Timber.plant(SLogTree())

        SLog.init(this)

        SLog.getInstance().start()
        findViewById<View>(R.id.addLog).setOnClickListener {
            launch {
                repeat(50) {
                    SLog.v("这是一条vvv日志$it")
                    ("这是一条info;info日志：$it").logI()
                    ("这是一条错误错误日志：$it").logE()
                    SLog.w("这是一条警告日志$it")
                    SLog.d("这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志$it")
                    delay(500)
                }
            }
        }

        findViewById<View>(R.id.removeFloat).setOnClickListener {
            SLog.getInstance().stop()
        }

        findViewById<View>(R.id.addFloat).setOnClickListener {
            SLog.getInstance().start()
        }

        findViewById<View>(R.id.addLogWithFile).setOnClickListener {
            launch {
                SLog.getInstance().logWithFile(VERBOSE,"logWithFile","这是一条错误VERBOSE日志")
                SLog.getInstance().logWithFile(DEBUG,"logWithFile","这是一条错误DEBUG日志")
                SLog.getInstance().logWithFile(INFO,"logWithFile","这是一条错误INFO日志")
                SLog.getInstance().logWithFile(WARN,"logWithFile","这是一条错误WARN日志")
                SLog.getInstance().logWithFile(ERROR,"logWithFile","这是一条错误ERROR日志")
            }
        }
        SLog.getInstance().addLogListener(this)
    }

    override fun onEnableChange(enable: Boolean) {
        //开关监听
    }

    override fun onChatShowChange(isShow: Boolean) {
        //LogChat监听
    }

    override fun onDestroy() {
        super.onDestroy()
        SLog.getInstance().removeListener(this)
    }
}
