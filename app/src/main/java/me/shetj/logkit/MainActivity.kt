package me.shetj.logkit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.delay
import me.shetj.base.ktx.launch
import me.shetj.logkit.debug.DebugFunc
import me.shetj.logkit.debug.showDebugView

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DebugFunc.getInstance().initContext(this)

        findViewById<View>(R.id.start).setOnClickListener {
            DebugFunc.getInstance().startLogsActivity()
        }

        findViewById<View>(R.id.addLog).setOnClickListener {
            launch {
                repeat(50){
                    DebugFunc.getInstance().logAndToFile("这是一条日志日志日志：$it")
                    delay(50)
                }
            }
        }

        findViewById<View>(R.id.addFloat).setOnClickListener {
            showDebugView()
        }
    }
}