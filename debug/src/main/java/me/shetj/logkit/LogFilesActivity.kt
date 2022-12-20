package me.shetj.logkit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class LogFilesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_files)
        findViewById<RecyclerView>(R.id.recycleView).apply {
            layoutManager = LinearLayoutManager(this@LogFilesActivity)
            adapter = object : BaseAdapter<String>(R.layout.item_logfile, SLog.getInstance().getSaveLogs()) {
                override fun convert(holder: BaseViewHolder, data: String) {
                    holder.setText(R.id.name, data)
                }

            }.apply {
                setOnItemClickListener { adapter, view, position ->
                    LogDesActivity.start(this@LogFilesActivity, getItem(position))
                }
                setOnItemLongClickListener { adapter, view, position ->
                    AlertDialog.Builder(this@LogFilesActivity)
                        .setTitle("是否删除该日志")
                        .setNegativeButton("确定") { dialog, postion ->
                            File(getItem(position)).delete()
                            adapter.data.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                        .setPositiveButton("取消") { dialog, postion ->
                            dialog.cancel()
                        }.show()
                    return@setOnItemLongClickListener true
                }
            }
        }
    }
}