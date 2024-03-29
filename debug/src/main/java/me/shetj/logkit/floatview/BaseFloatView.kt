/*
 * MIT License
 *
 * Copyright (c) 2019 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.shetj.logkit.floatview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.UiThread
import me.shetj.logkit.ui.LogLogo
import me.shetj.logkit.utils.SPUtils
import me.shetj.logkit.floatview.FloatKit.checkFloatPermission
import me.shetj.logkit.floatview.FloatKit.getWinManager

/**
 * 悬浮view的基类，只保留基础操作，可以继承实现更多
 */
internal abstract class BaseFloatView : FrameLayout {
    protected var windowParams: WindowManager.LayoutParams? = null
    protected var winManager: WindowManager? = null

    /**
     * 获取悬浮窗中的视频播放view
     */
    private var mStatusBarHeight = 24 * 3f // 系统状态栏的高度
    private var mXDownInScreen = 0f // 按下事件距离屏幕左边界的距离
    private var mYDownInScreen = 0f // 按下事件距离屏幕上边界的距离
    private var mXInScreen = 0f // 滑动事件距离屏幕左边界的距离
    private var mYInScreen = 0f // 滑动事件距离屏幕上边界的距离
    private var mXInView = 0f // 滑动事件距离自身左边界的距离
    private var mYInView = 0f // 滑动事件距离自身上边界的距离
    protected var isAttach = false

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    /**
     * 初始化view
     */
    abstract fun initView(context: Context)

    @UiThread
    open fun addToWindowManager(layout: WindowManager.LayoutParams.() -> Unit) {
        if (context.checkFloatPermission()) {
            if (winManager == null) {
                winManager = context.getWinManager()
                windowParams = FloatKit.getWindowParams().apply(layout).apply{
                    if (VERSION.SDK_INT >= VERSION_CODES.S) {
                        blurBehindRadius = 20
                    }
                }
            }
        }
        if (this.parent != null) {
            (parent as? ViewGroup)?.removeView(this) ?: kotlin.run {
                if (winManager != null) {
                    winManager!!.removeView(this)
                }
            }
        }
        isAttach = true
        winManager?.addView(this, windowParams)
    }

    @UiThread
    open fun removeForWindowManager() {
        if (isAttach){
            winManager?.removeView(this)
            isAttach = false
        }
    }

    /**
     * 给当前界面设置的view设置点击事件，不点击的时候，会滑动
     */
    @SuppressLint("ClickableViewAccessibility")
    fun View.setViewClickInFloat(onClickListener: OnClickListener? = null, onLongClickListener: OnLongClickListener?=null) {
        setOnClickListener(onClickListener)
        setOnLongClickListener(onLongClickListener)
        val mGestureListener = object : SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent?) {
                super.onLongPress(e)
                this@setViewClickInFloat.performLongClick()
            }

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                this@setViewClickInFloat.performClick()
                return true
            }
        }
        val mGestureDetector = GestureDetector(context, mGestureListener)
        setOnTouchListener { _, event ->
            mGestureDetector.onTouchEvent(event)
            onTouchEvent(event)
            return@setOnTouchListener true
        }
    }

    /**
     * 重写触摸事件监听，实现悬浮窗随手指移动
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mXInView = event.x
                mYInView = event.y
                mXInScreen = event.rawX
                mYInScreen = event.rawY - mStatusBarHeight
            }
            MotionEvent.ACTION_MOVE -> {
                mXInScreen = event.rawX
                mYInScreen = event.rawY - mStatusBarHeight
                updateViewPosition()
            }
            MotionEvent.ACTION_UP -> {
                if (this is LogLogo){
                    SPUtils.put(context,"slog_logo_x",(mXInScreen - mXInView).toInt())
                    SPUtils.put(context,"slog_logo_y",(mYInScreen - mYInView).toInt())
                }
                if (mXDownInScreen == mXInScreen &&
                    mYDownInScreen == mYInScreen
                ) { // 手指没有滑动视为点击，回到窗口模式
                    performClick()
                }
            }
            else -> {
            }
        }
        return true
    }

    open fun needTouchUpdatePosition(): Boolean {
        return true
    }


    private fun updateViewPosition() {
        if (needTouchUpdatePosition()) {
            val x = (mXInScreen - mXInView).toInt()
            val y = (mYInScreen - mYInView).toInt()
            windowParams?.x = x
            windowParams?.y = y
            winManager?.updateViewLayout(this, windowParams)
        }
    }
}
