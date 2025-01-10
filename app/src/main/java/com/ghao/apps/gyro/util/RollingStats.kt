package com.ghao.apps.gyro.util

import be.tarsos.dsp.util.fft.FloatFFT
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType

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

    fun performFFT(): FloatArray {
        return MyFFT.performFFT(values)
    }

    // java_vm_ext.cc:598] JNI DETECTED ERROR IN APPLICATION: JNI CallObjectMethodV called with pending exception org.apache.commons.math3.exception.MathIllegalArgumentException:
    // java_vm_ext.cc:598]   at void org.apache.commons.math3.transform.FastFourierTransformer.transformInPlace(double[][], org.apache.commons.math3.transform.DftNormalization, org.apache.commons.math3.transform.TransformType) (FastFourierTransformer.java:228)
    private fun performFFT2(data: DoubleArray): DoubleArray {
        val transformer = FastFourierTransformer(DftNormalization.STANDARD)
        val transformed = transformer.transform(data, TransformType.FORWARD)
        return transformed.map { it.abs() }.toDoubleArray() // Extract magnitudes
    }

    fun reset() {
        values.clear()
        minQueue.clear()
        maxQueue.clear()
        sum = 0f
    }
}
