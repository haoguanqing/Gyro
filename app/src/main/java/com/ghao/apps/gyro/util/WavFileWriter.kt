package com.ghao.apps.gyro.util

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.filters.HighPass
import be.tarsos.dsp.filters.LowPassSP
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.jvm.WaveformWriter

class WavFileWriter(
    private val sampleRate: Float = 16000f,
    private val bitDepth: Int = 16,
) {

    // Create TarsosDSP audio format (16-bit, mono)
    private val audioFormat = TarsosDSPAudioFormat(
        sampleRate,           // float version of sample rate
        bitDepth,             // bits per sample
        1,                   // channels (mono)
        true,                 // signed
        false                // little-endian
    )

    /**
     * Writes a float array in [-1..1] to a WAV file at the given sample rate and bit depth.
     *
     * @param audioData   FloatArray containing samples normalized to [-1..1].
     * @param sampleRate  Sample rate in Hz (e.g., 16000 for speech).
     * @param bitDepth    Bits per sample (usually 16).
     * @param fileName    Name of the output WAV file (e.g., "output.wav").
     */
    fun writeFloatArrayToWav(
        audioData: FloatArray,
        fileName: String
    ) {
        val filteredData = bandPassFilter300to3400(audioData)

        val audioEvent = AudioEvent(audioFormat)
        audioEvent.floatBuffer = filteredData

        // 3) Initialize WaveformWriter
        val writer = WaveformWriter(audioFormat, fileName)

        // 4) Write the data and close the file
        writer.process(audioEvent)
        writer.processingFinished()
    }

    private fun bandPassFilter300to3400(audioData: FloatArray): FloatArray {
        val audioEvent = AudioEvent(audioFormat)
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
}