package me.shetj.logkit

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.google.android.material.badge.BadgeDrawable.*
import me.shetj.logkit.R.string
import me.shetj.logkit.Utils.drawableToBitmap
import me.shetj.logkit.floatview.BaseFloatView
import me.shetj.logkit.floatview.FloatKit.checkFloatPermission
import me.shetj.logkit.floatview.FloatKit.getWinManager
import me.shetj.logkit.floatview.ViewRect


internal class LogLogo @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BaseFloatView(context, attrs) {


    private var imageView: ImageView? = null

    override fun initView(context: Context) {

        LayoutInflater.from(context).inflate(R.layout.logo_log_image, this, true).apply {
            val imageBitmap: Bitmap = try {
                val drawableIcon =
                    context.packageManager.getApplicationIcon(context.applicationContext.packageName)
                drawableToBitmap(drawableIcon) ?: throw PackageManager.NameNotFoundException()
            } catch (e: PackageManager.NameNotFoundException) {
                BitmapFactory.decodeResource(resources, R.drawable.ic_launcher)
            }
            val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, imageBitmap)
            roundedBitmapDrawable.setAntiAlias(true)
            imageView = findViewById<ImageView>(R.id.image)
            imageView!!.setImageDrawable(roundedBitmapDrawable)
            setViewClickInFloat (onClickListener = {
                if (SLog.getInstance().isShowing()) {
                    showChatAnim()
                    SLog.getInstance().hideLogChat()
                } else {
                    hideChatAnim()
                    SLog.getInstance().showLogChat()
                }
            },onLongClickListener = {
                AlertDialog.Builder(imageView!!.context)
                    .setTitle(context.getString(string.close_tip))
                    .setNegativeButton("确定") { dialog, postion ->
                        SLog.getInstance().stop()
                    }
                    .setPositiveButton("取消") { dialog, postion ->
                        dialog.cancel()
                    }.create().apply {
                        window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                    }
                    .show()
                return@setViewClickInFloat true
            })
        }
    }


    @SuppressLint("UnsafeOptInUsageError")
    fun setViewModel(model: ContentViewModel) {
        model.unReadCount.observeForever {

        }
    }


    override fun addToWindowManager(layout: ViewRect.() -> Unit) {
        if (context.checkFloatPermission()) {
            if (winManager == null) {
                winManager = context.getWinManager()
                val rect = ViewRect(0, 0, 0, 0).apply(layout)
                val mWindowParams = LayoutParams()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE
                }
                mWindowParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or FLAG_NOT_FOCUSABLE
                mWindowParams.format = PixelFormat.TRANSLUCENT
                mWindowParams.gravity = Gravity.START or Gravity.TOP
                mWindowParams.dimAmount = 0.45f
                windowParams = mWindowParams.apply {
                    x = rect.x
                    y = rect.y
                    width = rect.width
                    height = rect.height
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
        winManager?.addView(this, windowParams)
    }


    fun showChatAnim() {
        animate().scaleX(0.9f).scaleY(0.9f).start()
        addToWindowManager { }
        needUpdatePosition()
    }

    fun hideChatAnim() {
        animate().scaleX(1f).scaleY(1f).start()
        needUpdatePosition()
    }
}