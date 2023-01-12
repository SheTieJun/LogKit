package me.shetj.logkit.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Bitmap.Config.RGB_565
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.WindowManager

internal object Utils {

    private var density = -1f

    private fun getDensity(): Float {
        if (density <= 0f) {
            density = Resources.getSystem().displayMetrics.density
        }
        return density
    }

    @JvmStatic
    fun dp2px(dpValue: Float): Int {
        return (dpValue * getDensity() + 0.5f).toInt()
    }

    @JvmStatic
    fun px2dp(pxValue: Float): Int {
        return (pxValue / getDensity() + 0.5f).toInt()
    }

    @Suppress("DEPRECATION")
    @JvmStatic
    fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) return null else if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val intrinsicWidth = drawable.intrinsicWidth
        val intrinsicHeight = drawable.intrinsicHeight
        return if (!(intrinsicWidth > 0 && intrinsicHeight > 0)) null else try {
            val config = if (drawable.opacity != PixelFormat.OPAQUE) ARGB_8888 else RGB_565
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, config)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            null
        }
    }

    fun copyText(context: Context, text: String) {
        val cm: ClipboardManager? =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText("Label", text)
        // 将ClipData内容放到系统剪贴板里。
        cm?.setPrimaryClip(mClipData)
    }
}

internal val lineString: String? = System.getProperty("line.separator")