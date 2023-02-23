package me.shetj.logkit.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.core.content.FileProvider.*
import androidx.core.os.HandlerCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern
import me.shetj.logkit.R
import me.shetj.logkit.R.string
import me.shetj.logkit.SLog
import me.shetj.logkit.utils.lineString
import me.shetj.logkit.utils.shareFile

internal class LogDesActivity : AppCompatActivity(), SLog.SLogListener {


    private var logDes: TextView? = null
    private var searchRoot: View? = null
    private var sourceStr: String = ""
    private var autoHideLiveData = MutableLiveData<Boolean>()

    companion object {
        fun start(context: Context, file: String) {
            val intent = Intent(context, LogDesActivity::class.java).apply {
                putExtra("file", file)
            }
            context.startActivity(intent)
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_des)
        val filePath = intent.getStringExtra("file")
        supportActionBar?.title = getString(string.string_title_log_des)
        if (filePath.isNullOrEmpty()) {
            finish()
            return
        }
        val file = File(filePath)
        supportActionBar?.subtitle = file.name
        if (!file.exists()) {
            Toast.makeText(this, getString(string.string_file_no_exists), Toast.LENGTH_LONG).show()
            finish()
        }
        logDes = findViewById(R.id.log_des)
        searchRoot = findViewById(R.id.search_root)
        val editText = findViewById<EditText>(R.id.editText)

        editText.asTextLiveData().throttleLast(200).observe(this) {
            autoHideLiveData.postValue(false)
            searchHighlight(logDes!!.text.toString(), editText.text.toString())
        }

        autoHideLiveData.throttleLast(30000).observe(this) {
            if (it) {
                searchRoot?.isVisible = false
                windowInsetsController?.hide(WindowInsetsCompat.Type.ime())
            }
        }

        ArchTaskExecutor.getIOThreadExecutor().execute {
            file.forEachLine {
                runOnUiThread {
                    logDes?.append(it + lineString)
                }
            }
            sourceStr = logDes?.text.toString()
        }
        SLog.getInstance().addLogListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSearch -> {
                searchRoot?.isVisible = !searchRoot!!.isVisible
                if (searchRoot!!.isVisible) {
                    autoHideLiveData.postValue(true)
                } else {
                    autoHideLiveData.postValue(false)
                }
            }
            R.id.menuShare -> {
                shareFile(title.toString(),intent.getStringExtra("file"))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun searchHighlight(sourceStr: String, searchString: String) {
        val s = SpannableString(sourceStr);
        val p = Pattern.compile(searchString);//这里的abc为关键字
        val m = p.matcher(s)
        while (m.find()) {
            val start = m.start()
            val end = m.end()
            s.setSpan(ForegroundColorSpan(Color.RED), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        logDes?.text = s
        autoHideLiveData.postValue(true)
    }

    override fun onEnableChange(enable: Boolean) {
        if (!enable) {
            finish()
        }
    }

    override fun onDestroy() {
        SLog.getInstance().removeListener(this)
        super.onDestroy()
    }

}

internal val Activity.windowInsetsController: WindowInsetsControllerCompat?
    get() = WindowCompat.getInsetsController(window, findViewById(android.R.id.content))


internal fun EditText.asTextLiveData(): LiveData<String> {
    return MutableLiveData<String>().apply {
        addTextChangedListener(
            beforeTextChanged =
            { _: CharSequence?, _: Int, _: Int, _: Int ->
            },
            afterTextChanged = {
                postValue(it.toString())
            }, onTextChanged = { _: CharSequence?, _: Int, _: Int, _: Int ->
            }
        )
    }
}

internal fun <T> LiveData<T>.throttleLast(duration: Long = 1000L) = MediatorLiveData<T>().also { mld ->
    val source = this
    val handler = HandlerCompat.createAsync(Looper.getMainLooper())
    val isUpdate = AtomicBoolean(true) // 用来通知发送delay

    val runnable = Runnable {
        if (isUpdate.compareAndSet(false, true)) {
            mld.value = source.value
        }
    }

    mld.addSource(source) {
        if (isUpdate.compareAndSet(true, false)) {
            handler.postDelayed(runnable, duration)
        }
    }
}
