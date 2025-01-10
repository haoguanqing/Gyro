package com.ghao.apps.gyro.util

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.filters.HighPass
import be.tarsos.dsp.filters.LowPassSP
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import com.ghao.apps.gyro.util.WavFileWriter.Companion.RECORDER_SAMPLE_RATE

object BandPassFilter {

    fun filter(
        audioData: FloatArray,
        sampleRate: Float = RECORDER_SAMPLE_RATE.toFloat(),
        bitDepth: Int = 16,
        highPass: Float = 300f, // Create a high-pass filter at 300 Hz
        lowPass: Float = 3400f, // Create a low-pass filter at 3400 Hz
    ): FloatArray {

        // Create TarsosDSP audio format (16-bit, mono)
        val format = TarsosDSPAudioFormat(sampleRate, bitDepth, 1, true, false)
        val audioEvent = AudioEvent(format)

        // Put our buffer into TarsosDSP’s AudioEvent
        audioEvent.floatBuffer = audioData

        val highPassFilter = HighPass(highPass, sampleRate)
        // Use LowPassSP instead of LowPassFS for better performance
        val lowPassFilter = LowPassSP(lowPass, sampleRate)

        // Process with HighPass first
        highPassFilter.process(audioEvent)
        // Then process with LowPass
        lowPassFilter.process(audioEvent)

        // Retrieve the filtered data
        return audioEvent.floatBuffer
    }
}
