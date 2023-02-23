package me.shetj.logkit.utils

import android.content.Context
import androidx.annotation.Keep

@Keep
internal class SPUtils {
    init {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated")
    }

    companion object {

        private const val FILE_NAME = "slog_data"

        @JvmStatic
        fun put(context: Context, key: String, `object`: Any) {

            val sp = context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
            )
            val editor = sp.edit()
            when (`object`) {
                is String -> editor.putString(key, `object`)
                is Int -> editor.putInt(key, `object`)
                is Boolean -> editor.putBoolean(key, `object`)
                is Float -> editor.putFloat(key, `object`)
                is Long -> editor.putLong(key, `object`)
                else -> editor.putString(key, `object`.toString())
            }
            editor.apply()
        }

        @JvmStatic
        fun get(context: Context, key: String, defaultObject: Any): Any? {
            val sp = context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
            )
            return when (defaultObject) {
                is String -> sp.getString(key, defaultObject)
                is Int -> sp.getInt(key, defaultObject)
                is Boolean -> sp.getBoolean(key, defaultObject)
                is Float -> sp.getFloat(key, defaultObject)
                is Long -> sp.getLong(key, defaultObject)
                else -> null
            }
        }
    }
}
