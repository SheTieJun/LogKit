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

class MainActivity : AppCompatActivity(), SLog.SLogListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        Timber.plant(SLogTree())

        SLog.init(this)

        findViewById<View>(R.id.addLog).setOnClickListener {
            launch {
                repeat(50) {
                    SLog.v(
                        "ERROR:Uncaught TypeError: window.VConsole is not a constructor\n" +
                                "sourceID: 1 ,lineNumber:  1"
                    )
                    ("这是一条info;info日志：$it").logI()
                    ("这是一条错误错误日志：$it").logE()
                    SLog.w("[INFO:CONSOLE(1)] \"Uncaught TypeError: window.VConsole is not a constructor\", source:  (1)")
                    SLog.d(
                        "这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志\"" +
                                "这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志" +
                                "这是一\"这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一这是一条Debug日志这是一条Debug日志这是一条Deb" +
                                "ug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一这是一条Debug日志这是一条Debug日志这是一条Deb" +
                                "ug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一这是一条Debug日志这是一条Debug日志这是一条Debu" +
                                "g日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一这是一条Debug日志这是一条Debug日志这是" +
                                "一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一这是一条Debug日志这是一条Debug日" +
                                "志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一这是一条Debug日志这是一条Debu" +
                                "g日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一这是一条Debug日志这是一条D" +
                                "这是一条Debug日志这是一条Debug日志这是一\"这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是" +
                                "一条Debug日志这是一这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志这是一条Debug日志$it"
                    )
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
                SLog.getInstance().logFile(VERBOSE, "logWithFile", "这是一条错误VERBOSE日志")
                SLog.getInstance().logFile(DEBUG, "logWithFile", "这是一条错误DEBUG日志")
                SLog.getInstance().logFile(INFO, "logWithFile", "这是一条错误INFO日志")
                SLog.getInstance().logFile(WARN, "logWithFile", "这是一条错误WARN日志")
                SLog.getInstance().logFile(ERROR, "logWithFile", "这是一条错误ERROR日志")
            }
        }
        SLog.getInstance().addLogListener(this)
    }

    override fun onEnableChange(enable: Boolean) {
        SLog.getInstance().w(msg = "enable=$enable")
    }

    override fun onChatShowChange(isShow: Boolean) {
        //LogChat监听
    }

    override fun onDestroy() {
        super.onDestroy()
        SLog.getInstance().removeListener(this)
    }
}
