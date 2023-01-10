package me.shetj.logkit.model

import android.os.Handler
import android.os.Looper
import android.widget.Filter
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import me.shetj.logkit.LogLevel
import me.shetj.logkit.filter.Criteria
import me.shetj.logkit.filter.KeywordFilter
import me.shetj.logkit.filter.PriorityFilter


@Suppress("UNCHECKED_CAST")
internal class LogRepository(private val mFilterDelay: Long = 100) : Filter() {

    private val handler: Handler = Handler(Looper.getMainLooper())
    private val mKeywordFilter = KeywordFilter()
    private val mPriorityFilter = PriorityFilter()
    private val mFilters: List<Criteria<LogModel>>
    private val mVlogs: MutableList<LogModel>
    private var mResultListener: ResultListener? = null

    init {
        mFilters = ArrayList()
        mFilters.add(mKeywordFilter)
        mFilters.add(mPriorityFilter)
        mVlogs = mutableListOf()
    }

    /**
     * Initiate filter process
     *
     */
    private fun initiateFilter() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({ this@LogRepository.filter(null) }, mFilterDelay)
    }

    /**
     * For listening the filtered results
     *
     * @param resultListener
     */
    fun setResultListener(resultListener: ResultListener) {
        mResultListener = resultListener
        initiateFilter()
    }

    /**
     * pre-configures the keyword
     *
     * @param keyword
     */
    fun configureKeywordFilter(keyword: String) {
        mKeywordFilter.setKeyword(keyword)
        initiateFilter()
    }

    /**
     * pre-configures the log priority
     *
     * @param priority
     */
    fun configureLogPriority(priority: LogLevel) {
        mPriorityFilter.setPriority(priority)
        initiateFilter()
    }

    /**
     * Result listener
     *
     * @constructor Create empty Result listener
     */
    interface ResultListener {
        /**
         * On filter results
         *
         * @param filterResults
         */
        fun onFilterResults(filterResults: List<LogModel>)
    }

    @WorkerThread
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var filteredList: List<LogModel> = mVlogs
        for (filter in mFilters) {
            filteredList = filter.meetCriteria(filteredList)
        }

        val filterResult = FilterResults()
        filterResult.values = filteredList
        filterResult.count = filterResult.count
        return filterResult
    }

    @UiThread
    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        mResultListener?.onFilterResults(results?.values as List<LogModel>)
    }

    /**
     * Add log to the existing log repository
     *
     * @param model
     */
    fun feedLog(model: LogModel) {
        mVlogs.add(model)
        initiateFilter()
    }

    /**
     * Clear logs
     *
     */
    fun clearLogs() {
        mVlogs.clear()
        initiateFilter()
    }

    fun reset() {
        mVlogs.clear()
        for (filter in mFilters) {
            filter.reset()
        }
        initiateFilter()
    }
}
