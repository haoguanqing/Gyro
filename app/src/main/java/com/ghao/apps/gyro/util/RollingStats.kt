package com.ghao.apps.gyro.util

class RollingStats(private val size: Int) {
    private val values = ArrayDeque<Float>()
    private val minQueue = ArrayDeque<Float>()
    private val maxQueue = ArrayDeque<Float>()
    private var sum = 0f

    fun add(value: Float) {
        values.add(value)
        sum += value

        // Remove outdated values from the min queue
        while (minQueue.isNotEmpty() && minQueue.last() > value) {
            minQueue.removeLast()
        }
        minQueue.add(value)

        // Remove outdated values from the max queue
        while (maxQueue.isNotEmpty() && maxQueue.last() < value) {
            maxQueue.removeLast()
        }
        maxQueue.add(value)

        if (values.size > size) {
            val removed = values.removeFirst()
            sum -= removed

            // Remove from min/max queues if they match the removed value
            if (minQueue.first() == removed) {
                minQueue.removeFirst()
            }
            if (maxQueue.first() == removed) {
                maxQueue.removeFirst()
            }
        }
    }

    fun getAverage(): Float {
        return if (values.isNotEmpty()) sum / values.size else 0f
    }

    fun getMin(): Float? {
        return minQueue.firstOrNull()
    }

    fun getMax(): Float? {
        return maxQueue.firstOrNull()
    }

    fun reset() {
        values.clear()
        minQueue.clear()
        maxQueue.clear()
        sum = 0f
    }
}
