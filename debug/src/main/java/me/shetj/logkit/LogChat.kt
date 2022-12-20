package me.shetj.logkit

import android.R.layout
import android.app.AlertDialog
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import com.google.android.material.textfield.TextInputLayout
import me.shetj.logkit.LogPriority.DEBUG
import me.shetj.logkit.LogPriority.ERROR
import me.shetj.logkit.LogPriority.INFO
import me.shetj.logkit.LogPriority.VERBOSE
import me.shetj.logkit.LogPriority.WARN
import me.shetj.logkit.floatview.BaseFloatView
import me.shetj.logkit.floatview.FloatKit.checkFloatPermission
import me.shetj.logkit.floatview.FloatKit.getWinManager
import me.shetj.logkit.floatview.ViewRect

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2022/12/19<br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b>  <br>
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
                it.addItemDecoration(DividerItemDecoration(context,LinearLayout.VERTICAL))
            }
            findViewById<View>(R.id.clear_logs).setOnClickListener {
                viewModel?.onClearLogs()
            }

            findViewById<EditText>(R.id.editText)?.addTextChangedListener {
                viewModel?.onKeywordEnter(it.toString())
            }

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

    override fun needUpdatePosition(): Boolean {
        return false
    }

    override fun addToWindowManager(layout: ViewRect.() -> Unit) {
        if (context.checkFloatPermission()) {
            if (winManager == null) {
                winManager = context.getWinManager()
                val rect = ViewRect(0, 0, 0, 0).apply(layout)
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
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        this.animation = AnimationUtils.loadAnimation(context,  androidx.appcompat.R.anim.abc_slide_in_bottom)
    }


    override fun removeForWindowManager() {
        super.removeForWindowManager()
        this.animation = AnimationUtils.loadAnimation(context,  androidx.appcompat.R.anim.abc_slide_out_bottom)
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
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, com.google.android.material.R.style.Theme_Material3_DayNight_Dialog)
        builder.setTitle("Select Log priority")
        val priorityList: List<String> = resources.getStringArray(R.array.log_priority_names).toMutableList()
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

    private fun getLogPriority(selectedIndex: Int): LogPriority {
        var priority: LogPriority = VERBOSE

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