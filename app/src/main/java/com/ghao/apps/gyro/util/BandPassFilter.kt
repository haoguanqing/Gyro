package com.ghao.apps.gyro.util

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.filters.HighPass
import be.tarsos.dsp.filters.LowPassSP
import be.tarsos.dsp.io.TarsosDSPAudioFormat

fun bandPassFilter300to3400(
    audioData: FloatArray,
    sampleRate: Float = 16000f,
    bitDepth: Int = 16,
): FloatArray {

    // Create TarsosDSP audio format (16-bit, mono)
    val format = TarsosDSPAudioFormat(sampleRate, bitDepth, 1, true, false)
    val audioEvent = AudioEvent(format)

    // Put our buffer into TarsosDSP’s AudioEvent
    audioEvent.floatBuffer = audioData

    // Create a high-pass filter at 300 Hz
    val highPassFilter = HighPass(300f, sampleRate)
    // Create a low-pass filter at 3400 Hz
    // Use LowPassSP instead of LowPassFS for better performance
    val lowPassFilter = LowPassSP(3400f, sampleRate)

    // Process with HighPass first
    highPassFilter.process(audioEvent)
    // Then process with LowPass
    lowPassFilter.process(audioEvent)

    // Retrieve the filtered data
    return audioEvent.floatBuffer
}
