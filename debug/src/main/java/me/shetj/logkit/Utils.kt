package me.shetj.logkit

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

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2022/12/19<br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b>  <br>
 */
internal object Utils {

    internal const val FILTER_KEY_LIST = "filter_key_list"

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

    @JvmStatic
    fun getOverlayFlag(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }
}