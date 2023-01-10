package me.shetj.logkit.filter
import me.shetj.logkit.LogLevel
import me.shetj.logkit.model.LogModel

/**
 * Priority filter
 *
 * @constructor Create empty Priority filter
 */
internal class PriorityFilter : Criteria<LogModel> {

     private var mPriority: LogLevel = LogLevel.VERBOSE

    fun setPriority( constraint: LogLevel) {
        mPriority = constraint
    }

    override fun meetCriteria(input: List<LogModel>): List<LogModel> {
        val filteredList = ArrayList<LogModel>()

        for (item in input) {
            if (item.logLevel >= mPriority) {
                filteredList.add(item)
            }
        }

        return filteredList
    }

    override fun reset() {
        mPriority = LogLevel.VERBOSE
    }
}
