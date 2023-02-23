package me.shetj.logkit.ui

import android.R.layout
import android.app.AlertDialog
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import me.shetj.logkit.LogLevel
import me.shetj.logkit.LogLevel.DEBUG
import me.shetj.logkit.LogLevel.ERROR
import me.shetj.logkit.LogLevel.INFO
import me.shetj.logkit.LogLevel.VERBOSE
import me.shetj.logkit.LogLevel.WARN
import me.shetj.logkit.R
import me.shetj.logkit.R.array
import me.shetj.logkit.SLog
import me.shetj.logkit.adapter.SlogAdapter
import me.shetj.logkit.floatview.BaseFloatView
import me.shetj.logkit.floatview.FloatKit.checkFloatPermission
import me.shetj.logkit.floatview.FloatKit.getWinManager

/**
 *
 */
internal class LogChat @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BaseFloatView(context, attrs) {

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var mAdapter: SlogAdapter? = null
    private var viewModel: ContentViewModel? = null

    override fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.log_content_view, this, true).apply {
            findViewById<RecyclerView>(R.id.events).also {
                it.layoutManager = LinearLayoutManager(context)
                it.adapter = SlogAdapter().apply {
                    mAdapter = this
                }
                it.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
            }
            findViewById<View>(R.id.clear_logs).setOnClickListener {
                viewModel?.onClearLogs()
            }

            findViewById<EditText>(R.id.editText)?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    viewModel?.onKeywordEnter(s?.trimEnd().toString())
                }
            })
            findViewById<TextView>(R.id.log_priority_txtvw).apply {
                setOnClickListener {
                    showPriorityOptions(context, this)
                }
            }

            findViewById<View>(R.id.container).setOnClickListener {
                SLog.getInstance().hideLogChat()
            }

            findViewById<View>(R.id.layout_bottom_sheet).also {
                bottomSheetBehavior = BottomSheetBehavior.from(it)
                bottomSheetBehavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == STATE_HIDDEN) {
                            SLog.getInstance().hideLogChat()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    }
                })
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        when (event?.keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                SLog.getInstance().hideLogChat()
            }
            else -> {}
        }
        return super.dispatchKeyEvent(event)
    }

    override fun needTouchUpdatePosition(): Boolean {
        return false
    }

    @Suppress("DEPRECATION")
    override fun addToWindowManager(layout: WindowManager.LayoutParams.() -> Unit) {
        if (context.checkFloatPermission()) {
            if (winManager == null) {
                winManager = context.getWinManager()
                val mWindowParams = WindowManager.LayoutParams()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE
                }
                mWindowParams.flags = (FLAG_DIM_BEHIND or FLAG_FULLSCREEN or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                mWindowParams.format = PixelFormat.TRANSLUCENT
                mWindowParams.gravity = Gravity.START or Gravity.TOP
                mWindowParams.dimAmount = 0.45f
                windowParams = mWindowParams.apply(layout)
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
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    fun setViewModel(model: ContentViewModel) {
        viewModel = model
        model.resultObserver.observeForever {
            post {
                mAdapter?.addLogs(it)
            }
        }
    }


    private fun showPriorityOptions(context: Context, logPriorityTxtVw: TextView) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Select Log Level")
        val priorityList: List<String> = resources.getStringArray(array.log_priority_names).toMutableList()
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            context,
            layout.simple_list_item_1,
            priorityList
        )
        builder.setAdapter(arrayAdapter) { _, selectedIndex ->
            logPriorityTxtVw.text = priorityList[selectedIndex]
            viewModel?.onPrioritySet(getLogPriority(selectedIndex))
        }
        builder.setPositiveButton(
            "Cancel"
        ) { dialog, _ -> dialog?.dismiss() }
        val dialog: AlertDialog = builder.create()
        dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        dialog.show()
    }

    private fun getLogPriority(selectedIndex: Int): LogLevel {
        var priority: LogLevel = VERBOSE

        when (selectedIndex) {
            0 -> priority = VERBOSE
            1 -> priority = DEBUG
            2 -> priority = INFO
            3 -> priority = WARN
            4 -> priority = ERROR
        }

        return priority
    }

}