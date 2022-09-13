package me.shetj.logkit.debug

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import java.io.File

class LogDesActivity : AppCompatActivity() {


    companion object {
        fun start(context: Context, file: String) {
            val intent = Intent(context, LogDesActivity::class.java).apply {
                putExtra("file", file)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_des)

        val filePath = intent.getStringExtra("file")

        if (filePath.isNullOrEmpty()) {
            finish()
            return
        }

        val file = File(filePath)

        if (!file.exists()) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show()
            finish()
        }

        val logDes = findViewById<TextView>(R.id.log_des)

        file.forEachLine {
            logDes.append(it + lineString)
        }
    }
}