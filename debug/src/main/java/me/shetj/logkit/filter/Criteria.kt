package me.shetj.logkit.filter

internal interface Criteria<T> {
    fun meetCriteria(input: List<T>): List<T>
    fun reset()
}
