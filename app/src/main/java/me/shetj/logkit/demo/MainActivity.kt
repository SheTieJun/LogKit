package me.shetj.logkit.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import me.shetj.logkit.service.SLogServerService
import kotlinx.coroutines.delay
import me.shetj.base.ktx.launch
import me.shetj.base.ktx.logE
import me.shetj.base.ktx.logI
import me.shetj.logkit.SLog
import me.shetj.logkit.SLogTree
import me.shetj.logkit.demo.R.layout
import timber.log.Timber

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        Timber.plant(SLogTree())

        startService(Intent(this, SLogServerService::class.java))

        findViewById<View>(R.id.start).setOnClickListener {
            SLog.getInstance().startLogsActivity()
        }

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
    }
}
