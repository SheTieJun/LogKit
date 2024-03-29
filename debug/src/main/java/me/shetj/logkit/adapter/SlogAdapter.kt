package me.shetj.logkit.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.shetj.logkit.LogLevel.DEBUG
import me.shetj.logkit.LogLevel.ERROR
import me.shetj.logkit.LogLevel.INFO
import me.shetj.logkit.LogLevel.VERBOSE
import me.shetj.logkit.LogLevel.WARN
import me.shetj.logkit.R
import me.shetj.logkit.model.LogModel
import me.shetj.logkit.R.drawable
import me.shetj.logkit.R.layout
import me.shetj.logkit.adapter.SlogAdapter.LogViewHolder
import me.shetj.logkit.getLogPriorityInitials
import me.shetj.logkit.utils.Utils

internal class SlogAdapter : RecyclerView.Adapter<LogViewHolder>() {
    private var mFilteredLogList: List<LogModel>?
    private var mExpandedPosition: Int? = null
    private var lastPosition = -1
    private val errorColor = Color.parseColor("#E64A19")
    private val warnColor = Color.parseColor("#FFC107")
    private val infoColor = Color.parseColor("#4CAF50")
    private val debugColor = Color.parseColor("#34363A")
    private val defaultColor = Color.parseColor("#1976D2")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        return LogViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layout.list_item_log,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val model = mFilteredLogList!![position]
        when (model.logLevel) {
            ERROR -> {
                holder.logTag.setTextColor(errorColor)
                holder.logMessage.setTextColor(errorColor)
            }
            WARN -> {
                holder.logTag.setTextColor(warnColor)
                holder.logMessage.setTextColor(warnColor)
            }
            INFO -> {
                holder.logTag.setTextColor(infoColor)
                holder.logMessage.setTextColor(infoColor)
            }
            VERBOSE -> {
                holder.logTag.setTextColor(defaultColor)
                holder.logMessage.setTextColor(defaultColor)
            }
            DEBUG -> {
                holder.logTag.setTextColor(debugColor)
                holder.logMessage.setTextColor(debugColor)
            }
        }
        val levelAndTag = getLogPriorityInitials(model.logLevel) + "/" + model.tag + ": "
        holder.logTag.text = levelAndTag
        val isExpanded = position == mExpandedPosition
        holder.logMessage.text = model.logMessage
        if (isExpanded){
            holder.logMessage.maxLines = (100)
            holder.logMessage.isSingleLine = false
        } else{
            holder.logMessage.setLines(1)
            holder.logMessage.maxLines = (1)
            holder.logMessage.isSingleLine = true
        }
        holder.expandCollapseArrow.setImageResource(if (isExpanded) drawable.log_ic_arrow_up else drawable.log_ic_arrow_down)
        holder.itemView.setOnClickListener {
            mExpandedPosition = if (isExpanded) null else position
            if (lastPosition != -1) {
                notifyItemChanged(lastPosition, 1)
            }
            lastPosition = position
            notifyItemChanged(position, 1)
        }
        holder.copy.setOnClickListener {
            Utils.copyText(it.context, model.logMessage)
        }
        holder.logTime.text = model.time
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        val model = mFilteredLogList!![position]
        when (model.logLevel) {
            ERROR -> {
                holder.logTag.setTextColor(errorColor)
                holder.logMessage.setTextColor(errorColor)
            }
            WARN -> {
                holder.logTag.setTextColor(warnColor)
                holder.logMessage.setTextColor(warnColor)
            }
            INFO -> {
                holder.logTag.setTextColor(infoColor)
                holder.logMessage.setTextColor(infoColor)
            }
            VERBOSE -> {
                holder.logTag.setTextColor(defaultColor)
                holder.logMessage.setTextColor(defaultColor)
            }
            DEBUG -> {
                holder.logTag.setTextColor(debugColor)
                holder.logMessage.setTextColor(debugColor)
            }
        }
        val levelAndTag = getLogPriorityInitials(model.logLevel) + "/" + model.tag + ": "
        holder.logTag.text = levelAndTag
        val isExpanded = position == mExpandedPosition
        holder.logMessage.text = model.logMessage
        if (isExpanded){
            holder.logMessage.maxLines = (100)
            holder.logMessage.isSingleLine = false
        } else{
            holder.logMessage.setLines(1)
            holder.logMessage.maxLines = (1)
            holder.logMessage.isSingleLine = true
        }
        holder.logTime.text = model.time
        holder.expandCollapseArrow.setImageResource(if (isExpanded) drawable.log_ic_arrow_up else drawable.log_ic_arrow_down)
    }


    override fun getItemCount(): Int {
        return if (mFilteredLogList != null) mFilteredLogList!!.size else 0
    }

    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var logTag: TextView
        var logTime: TextView
        var logMessage: TextView
        var expandCollapseArrow: ImageView
        var copy: ImageView

        init {
            logTag = itemView.findViewById(R.id.log_tag)
            logMessage = itemView.findViewById(R.id.log_message)
            logTime = itemView.findViewById(R.id.log_time)
            expandCollapseArrow = itemView.findViewById(R.id.arrow_img)
            copy = itemView.findViewById(R.id.copy)
        }
    }

    fun addLogs(logs: List<LogModel>?) {
        mFilteredLogList = logs
        notifyDataSetChanged()
    }

    init {
        mFilteredLogList = ArrayList()
    }
}
