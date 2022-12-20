package me.shetj.logkit.filter
import me.shetj.logkit.LogModel
import me.shetj.logkit.LogPriority

/**
 * Priority filter
 *
 * @constructor Create empty Priority filter
 */
internal class PriorityFilter : Criteria<LogModel> {

     private var mPriority: LogPriority = LogPriority.VERBOSE

    fun setPriority( constraint: LogPriority) {
        mPriority = constraint
    }

    override fun meetCriteria(input: List<LogModel>): List<LogModel> {
        val filteredList = ArrayList<LogModel>()

        for (item in input) {
            if (item.logPriority >= mPriority) {
                filteredList.add(item)
            }
        }

        return filteredList
    }

    override fun reset() {
        mPriority = LogPriority.VERBOSE
    }
}
