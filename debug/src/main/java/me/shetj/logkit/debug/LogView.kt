package me.shetj.logkit.debug

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import me.shetj.logkit.debug.floatview.BaseFloatView

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2022/4/12<br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b>  <br>
 */
class LogView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BaseFloatView(context, attrs),LogCurrentCall {

    private var logCurrentView:TextView ?= null
    private var scroll:NestedScrollView ?= null

    override fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.layout_log_current,this,true).apply {
            logCurrentView =   this.findViewById(R.id.current_log)
            scroll = this.findViewById(R.id.NestedScrollView)
            this.findViewById<View>(R.id.close).setOnClickListener {
                removeForWindowManager()
            }
            this.findViewById<View>(R.id.clean).setOnClickListener {
                logCurrentView?.text = ""
            }
            this.findViewById<View>(R.id.look).setOnClickListener {
                DebugFunc.getInstance().startLogsActivity()
            }
        }
    }

    override fun log(string: String?) {
        logCurrentView?.append("$string$lineString")
        scroll.apply {
            if (scroll?.canScrollVertically(1) == false) {
                postDelayed({
                    scroll?.smoothScrollTo(0,(logCurrentView?.height?:0))
                }, 5)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        DebugFunc.getInstance().addCall(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        DebugFunc.getInstance().remove(this)
    }


}