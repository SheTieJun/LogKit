package me.shetj.logkit.ui

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import me.shetj.logkit.R
import me.shetj.logkit.R.array
import me.shetj.logkit.R.id
import me.shetj.logkit.R.layout
import me.shetj.logkit.R.string
import me.shetj.logkit.SLog
import me.shetj.logkit.SLog.SLogListener
import me.shetj.logkit.adapter.BaseAdapter
import me.shetj.logkit.adapter.BaseViewHolder
import me.shetj.logkit.model.LogFileInfo
import me.shetj.logkit.utils.shareFile

internal class LogFilesActivity : AppCompatActivity(), SLogListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_log_files)
        supportActionBar?.title = getString(string.title_log)
        supportActionBar?.subtitle = getString(string.sub_title_log)
        SLog.getInstance().autoClearLogFile()
        findViewById<RecyclerView>(id.recycleView).apply {
            layoutManager = LinearLayoutManager(this@LogFilesActivity)
            adapter = object : BaseAdapter<LogFileInfo>(layout.item_logfile, SLog.getInstance().getSaveLogs()) {
                override fun convert(holder: BaseViewHolder, data: LogFileInfo) {
                    holder.setText(R.id.title, data.name)
                    holder.setText(R.id.last_update_time,context.getString(string.string_last_update)+data.time)
                }

            }.apply {
                setOnItemClickListener { _, _, position ->
                    LogDesActivity.start(this@LogFilesActivity, getItem(position).file)
                }
                setOnItemLongClickListener { adapter, _, position ->
                    val item = getItem(position)

                    val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)
                    val priorityList: List<String> = resources.getStringArray(array.log_case).toMutableList()
                    val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                        context,
                        android.R.layout.simple_list_item_1,
                        priorityList
                    )
                    builder.setAdapter(arrayAdapter) { _, selectedIndex ->
                        when(priorityList[selectedIndex]){
                            context.getString(string.string_open_file)->{
                                LogDesActivity.start(this@LogFilesActivity, item.file)
                            }
                            context.getString(string.string_del_file) ->{
                                AlertDialog.Builder(this@LogFilesActivity)
                                    .setTitle(context.getString(string.string_title_del_log))
                                    .setNegativeButton(context.getString(string.string_sure)) { _, _ ->
                                        File(item.file).delete()
                                        adapter.data.removeAt(position)
                                        adapter.notifyItemRemoved(position)
                                    }
                                    .setPositiveButton(context.getString(string.string_cancel)) { dialog, _ ->
                                        dialog.cancel()
                                    }.show()
                            }
                            context.getString(string.string_share_file) ->{
                                shareFile(item.name,item.file)
                            }
                            else ->{}
                        }
                    }
                    val dialog: android.app.AlertDialog = builder.create()
                    if (VERSION.SDK_INT >= VERSION_CODES.O) {
                        dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                    }
                    dialog.show()
                    return@setOnItemLongClickListener true
                }
            }
            addItemDecoration(DividerItemDecoration(this@LogFilesActivity,LinearLayout.VERTICAL))
        }
        SLog.getInstance().addLogListener(this)
    }

    override fun onEnableChange(enable: Boolean) {
        if (!enable){
            finish()
        }
    }

    override fun onDestroy() {
        SLog.getInstance().removeListener(this)
        super.onDestroy()
    }

}