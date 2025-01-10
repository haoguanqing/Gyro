package com.ghao.apps.gyro.util

import be.tarsos.dsp.util.fft.FloatFFT

object MyFFT {

    fun performFFT(data: Collection<Float>): FloatArray {
        val a = data.toFloatArray()
        FloatFFT(a.size).realForward(a)
        return a
    }

    fun performFFT(data: FloatArray) {
        FloatFFT(data.size).realForward(data)
    }
}
