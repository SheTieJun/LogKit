package me.shetj.logkit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import me.shetj.logkit.LogRepository.ResultListener

internal class ContentViewModel(private val mVlogRepository: LogRepository) : ViewModel(), ResultListener {

    val resultObserver = MutableLiveData<List<LogModel>>()
    val unReadCount = MutableLiveData(0)






    init {
        mVlogRepository.setResultListener(this)
    }

    override fun onFilterResults(filterResults: List<LogModel>) {
        resultObserver.setValue(filterResults)
    }

    /**
     * This method is called by the view when user enters filter keyword
     *
     * @param keyword
     */
    fun onKeywordEnter(keyword: String) {
        mVlogRepository.configureKeywordFilter(keyword)
    }

    /**
     * This method is called by the view when user sets the log priority
     *
     * @param priority
     */
    fun onPrioritySet( priority: LogLevel) {
        mVlogRepository.configureLogPriority(priority)
    }

    fun onClearLogs() {
        mVlogRepository.clearLogs()
    }
}

