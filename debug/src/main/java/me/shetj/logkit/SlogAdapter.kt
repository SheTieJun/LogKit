package me.shetj.logkit

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.shetj.logkit.LogPriority.DEBUG
import me.shetj.logkit.LogPriority.ERROR
import me.shetj.logkit.LogPriority.INFO
import me.shetj.logkit.LogPriority.VERBOSE
import me.shetj.logkit.LogPriority.WARN
import me.shetj.logkit.SlogAdapter.LogViewHolder

internal class SlogAdapter : RecyclerView.Adapter<LogViewHolder>() {
    private var mFilteredLogList: List<LogModel>?
    private var mExpandedModel: LogModel? = null
    private var lastPosition = -1
    private val errorColor = Color.parseColor("#E64A19")
    private val warnColor =  Color.parseColor("#FFC107")
    private val infoColor = Color.parseColor("#4CAF50")
    private val debugColor =  Color.parseColor("#34363A")
    private val defaultColor = Color.parseColor("#1976D2")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        return LogViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_log,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {

        val model = mFilteredLogList!![position]
        val priority = model.logPriority

        when (priority) {
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
        holder.logTag.text = getLogPriorityInitials(model.logPriority) + "/" + model.tag + ": "
        val isExpanded = model == mExpandedModel
        holder.logMessage.text =
            if (isExpanded)
                model.logMessage
            else if (model.logMessage.length > 50)
                model.logMessage.substring(0, 49) + "..."
            else
                model.logMessage

        holder.expandCollapseArrow.setImageResource(if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)
        holder.itemView.setOnClickListener {
            mExpandedModel = if (isExpanded) null else model
            if (lastPosition != -1) {
                notifyItemChanged(lastPosition, 1)
            }
            lastPosition = position
            notifyItemChanged(position, 1)
        }
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        val model = mFilteredLogList!![position]
        val priority = model.logPriority


        when (priority) {
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
        holder.logTag.text = getLogPriorityInitials(model.logPriority) + "/" + model.tag + ": "
        val isExpanded = model == mExpandedModel
        holder.logMessage.text =
            if (isExpanded)
                model.logMessage
            else if (model.logMessage.length > 50)
                model.logMessage.substring(0, 49) + "..."
            else
                model.logMessage

        holder.expandCollapseArrow.setImageResource(if (isExpanded) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down)
    }


    override fun getItemCount(): Int {
        return if (mFilteredLogList != null) mFilteredLogList!!.size else 0
    }

    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var logTag: TextView
        var logMessage: TextView
        var expandCollapseArrow: ImageView

        init {
            logTag = itemView.findViewById(R.id.log_tag)
            logMessage = itemView.findViewById(R.id.log_message)
            expandCollapseArrow = itemView.findViewById(R.id.arrow_img)
        }
    }

    fun addLogs(logs: List<LogModel>?) {
        mFilteredLogList = logs
        notifyDataSetChanged()
    }

    companion object {
        private val TAG = SlogAdapter::class.java.simpleName
    }

    init {
        mFilteredLogList = ArrayList()
    }
}
