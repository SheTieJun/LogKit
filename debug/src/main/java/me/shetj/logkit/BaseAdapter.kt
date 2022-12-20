package me.shetj.logkit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView


//region 点击接口部分
internal typealias OnItemClickListener = (adapter: BaseAdapter<*>, view: View, position: Int) -> Unit

internal typealias OnItemLongClickListener = (adapter: BaseAdapter<*>, view: View, position: Int) -> Boolean


internal typealias OnItemChildClickListener = (adapter: BaseAdapter<*>, view: View, position: Int) -> Unit

internal typealias OnItemChildLongClickListener = (adapter: BaseAdapter<*>, view: View, position: Int) -> Boolean


//endregion

internal abstract class BaseAdapter<T> @JvmOverloads constructor(
    @LayoutRes private val layoutResId: Int,
    data:  MutableList<T>? = null
) : RecyclerView.Adapter<BaseViewHolder>() {

    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemLongClickListener: OnItemLongClickListener? = null
    private var mOnItemChildClickListener: OnItemChildClickListener? = null
    private var mOnItemChildLongClickListener: OnItemChildLongClickListener? = null

    private val clickViewIds by lazy(LazyThreadSafetyMode.NONE) { ArrayList<Int>() }
    private val longClickViewIds by lazy(LazyThreadSafetyMode.NONE) { ArrayList<Int>() }

    var data: MutableList<T> = data ?: arrayListOf()
        internal set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(parent.getItemView(layoutResId))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        convert(holder,data[position])
        bindClick(holder)
        bindChildClick(holder)
    }


    abstract fun convert(holder: BaseViewHolder, data: T)

    open fun getItem(@IntRange(from = 0) position: Int): T {
        return data[position]
    }

    protected open fun bindClick(viewHolder: BaseViewHolder) {
        if (getOnItemClickListener()!= null) {
            //如果没有设置点击监听，则回调给 itemProvider
            //Callback to itemProvider if no click listener is set
            viewHolder.itemView.setOnClickListener {
                val position = viewHolder.adapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnClickListener
                }

                getOnItemClickListener()!!.invoke(this, it, position)
            }
        }
        if (getOnItemLongClickListener() != null) {
            //如果没有设置长按监听，则回调给itemProvider
            // If you do not set a long press listener, callback to the itemProvider
            viewHolder.itemView.setOnLongClickListener {
                val position = viewHolder.adapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return@setOnLongClickListener false
                }
                return@setOnLongClickListener getOnItemLongClickListener()!!.invoke(
                    this,
                    it,
                    position
                )
            }
        }
    }

    protected open fun bindChildClick(viewHolder: BaseViewHolder) {
        if (getOnItemChildClickListener() != null) {
            val ids = getChildClickViewIds()
            ids.forEach { id ->
                viewHolder.itemView.findViewById<View>(id)?.let {
                    if (!it.isClickable) {
                        it.isClickable = true
                    }
                    it.setOnClickListener { v ->
                        val position: Int = viewHolder.adapterPosition
                        if (position == RecyclerView.NO_POSITION) {
                            return@setOnClickListener
                        }
                        getOnItemChildClickListener()!!.invoke(this, it, position)
                    }
                }
            }
        }
        if (getOnItemChildLongClickListener() != null) {
            val ids = getChildLongClickViewIds()
            ids.forEach { id ->
                viewHolder.itemView.findViewById<View>(id)?.let {
                    if (!it.isLongClickable) {
                        it.isLongClickable = true
                    }
                    it.setOnLongClickListener { v ->
                        val position: Int = viewHolder.adapterPosition
                        if (position == RecyclerView.NO_POSITION) {
                            return@setOnLongClickListener false
                        }
                        return@setOnLongClickListener getOnItemChildLongClickListener()!!.invoke(
                            this,
                            it,
                            position
                        )
                    }
                }
            }
        }
    }


    fun addChildClickViewIds(@IdRes vararg ids: Int) {
        ids.forEach {
            this.clickViewIds.add(it)
        }
    }

    fun addChildLongClickViewIds(@IdRes vararg ids: Int) {
        ids.forEach {
            this.longClickViewIds.add(it)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.mOnItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        this.mOnItemLongClickListener = listener
    }

    fun setOnItemChildClickListener(listener: OnItemChildClickListener?) {
        this.mOnItemChildClickListener = listener
    }

    fun setOnItemChildLongClickListener(listener: OnItemChildLongClickListener?) {
        this.mOnItemChildLongClickListener = listener
    }



    private fun ViewGroup.getItemView(@LayoutRes layoutResId: Int): View {
        return LayoutInflater.from(this.context).inflate(layoutResId, this, false)
    }

    private fun getChildClickViewIds() = this.clickViewIds

    private fun getChildLongClickViewIds() = this.longClickViewIds

    private fun getOnItemClickListener(): OnItemClickListener? = mOnItemClickListener

    private fun getOnItemLongClickListener(): OnItemLongClickListener? = mOnItemLongClickListener

    private fun getOnItemChildClickListener(): OnItemChildClickListener? = mOnItemChildClickListener

    private fun getOnItemChildLongClickListener(): OnItemChildLongClickListener? =
        mOnItemChildLongClickListener

}