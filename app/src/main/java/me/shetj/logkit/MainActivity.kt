package me.shetj.logkit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.delay
import me.shetj.base.ktx.launch
import me.shetj.logkit.debug.DebugFunc

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
                repeat(100){
                    DebugFunc.getInstance().saveLogToFile("这是一条日志日志日志：$it")
                    delay(50)
                }
            }
        }

        findViewById<View>(R.id.addFloat).setOnClickListener {
            lifecycle.addObserver(object :LifecycleEventObserver{
                override fun onStateChanged(source: LifecycleOwner, event: Event) {
                        if (event == ON_RESUME){
                            DebugFunc.getInstance().addFlotLogView()
                        }
                    if (event == ON_STOP){
                        DebugFunc.getInstance().removeFlotLogView()
                    }
                }
            })
        }
    }
}