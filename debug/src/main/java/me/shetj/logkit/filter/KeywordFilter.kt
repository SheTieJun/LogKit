package me.shetj.logkit.filter

import me.shetj.logkit.model.LogModel

internal class KeywordFilter : Criteria<LogModel> {

    private var mKeyword: String = ""

    fun setKeyword(keyword: String) {
        mKeyword = keyword
    }

    override fun meetCriteria(input: List<LogModel>): List<LogModel> {
        if (mKeyword.isEmpty()) {
            return input
        }

        val normalizedKeyword = mKeyword.lowercase().trim()
        val filteredLogs = ArrayList<LogModel>()

        for (item in input) {

            val normalizedLog = item.logMessage.lowercase().trim()
            val normalizedTag = item.tag.lowercase().trim()

            if (normalizedLog.contains(normalizedKeyword) || normalizedTag.contains(normalizedKeyword)) {
                filteredLogs.add(item)
            }
        }

        return filteredLogs
    }

    override fun reset() {
        mKeyword = ""
    }
}
