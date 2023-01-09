package me.shetj.logkit

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

internal class LogFilesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_files)
        supportActionBar?.title = "日志文件管理"
        supportActionBar?.subtitle = "大于7天的日志已自动删除"
        SLog.getInstance().autoClear()
        findViewById<RecyclerView>(R.id.recycleView).apply {
            layoutManager = LinearLayoutManager(this@LogFilesActivity)
            adapter = object : BaseAdapter<LogFileInfo>(R.layout.item_logfile, SLog.getInstance().getSaveLogs()) {
                override fun convert(holder: BaseViewHolder, data: LogFileInfo) {
                    holder.setText(R.id.title, data.name)
                    holder.setText(R.id.last_update_time,"最后更新："+data.time)
                }

            }.apply {
                setOnItemClickListener { adapter, view, position ->
                    LogDesActivity.start(this@LogFilesActivity, getItem(position).file)
                }
                setOnItemLongClickListener { adapter, view, position ->
                    AlertDialog.Builder(this@LogFilesActivity)
                        .setTitle("是否删除该日志")
                        .setNegativeButton("确定") { dialog, postion ->
                            File(getItem(position).file).delete()
                            adapter.data.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                        .setPositiveButton("取消") { dialog, postion ->
                            dialog.cancel()
                        }.show()
                    return@setOnItemLongClickListener true
                }
            }
            addItemDecoration(DividerItemDecoration(this@LogFilesActivity,LinearLayout.VERTICAL))
        }
    }


}