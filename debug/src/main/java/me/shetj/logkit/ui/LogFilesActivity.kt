package me.shetj.logkit.ui

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import me.shetj.logkit.adapter.BaseViewHolder
import me.shetj.logkit.model.LogFileInfo
import me.shetj.logkit.R
import me.shetj.logkit.R.id
import me.shetj.logkit.R.layout
import me.shetj.logkit.SLog
import me.shetj.logkit.SLog.SLogListener
import me.shetj.logkit.adapter.BaseAdapter

internal class LogFilesActivity : AppCompatActivity(), SLogListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_log_files)
        supportActionBar?.title = "日志文件管理"
        supportActionBar?.subtitle = "大于7天的日志已自动删除"
        SLog.getInstance().autoClearLogFile()
        findViewById<RecyclerView>(id.recycleView).apply {
            layoutManager = LinearLayoutManager(this@LogFilesActivity)
            adapter = object : BaseAdapter<LogFileInfo>(layout.item_logfile, SLog.getInstance().getSaveLogs()) {
                override fun convert(holder: BaseViewHolder, data: LogFileInfo) {
                    holder.setText(R.id.title, data.name)
                    holder.setText(R.id.last_update_time,"最后更新："+data.time)
                }

            }.apply {
                setOnItemClickListener { _, _, position ->
                    LogDesActivity.start(this@LogFilesActivity, getItem(position).file)
                }
                setOnItemLongClickListener { adapter, _, position ->
                    AlertDialog.Builder(this@LogFilesActivity)
                        .setTitle("是否删除该日志")
                        .setNegativeButton("确定") { _, _ ->
                            File(getItem(position).file).delete()
                            adapter.data.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                        .setPositiveButton("取消") { dialog, _ ->
                            dialog.cancel()
                        }.show()
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