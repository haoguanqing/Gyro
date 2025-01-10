package com.ghao.apps.gyro.util

import kotlin.math.abs

object Normalizer {
    /**
     * Normalizes the input data to a range between -1 and 1.
     */
    fun normalize(data: Collection<Float>, maxMagnitude: Float = 0.006f): Collection<Float> {
        val max = maxOf(data.maxOf { abs(it) }, maxMagnitude)
        val filtered = data.map { it.coerceAtMost(max) }
        val mean = filtered.average()
        val centered = filtered.map { it - mean }
        val maxAbs = centered.maxOf { abs(it) }
        val scale = if (maxAbs > 0.0) 1.0 / maxAbs else 1.0
        return centered.map { (it * scale).toFloat() }
    }

    /**
     * Normalizes the input data to a range between -1 and 1.
     */
    fun normalize(data: FloatArray, maxMagnitude: Float = 0.006f): FloatArray {
        return normalize(data.asList(), maxMagnitude).toFloatArray()
    }
}

