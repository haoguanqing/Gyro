package com.ghao.apps.gyro.util

import android.content.Context
import android.media.AudioFormat
import android.os.Environment
import android.widget.Toast
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class WavFileWriter(
    private val context: Context,
    private val sampleRate: Int = RECORDER_SAMPLE_RATE,
    private val bitDepth: Short = BITS_PER_SAMPLE,
    private val numChannels: Short = NUMBER_CHANNELS,
) {

    // Byte rate = (Sample Rate * BitsPerSample * Channels) / 8
    private val byteRate = sampleRate * numChannels * 16 / 8

    private val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

    fun writeToWav(audioData: FloatArray, fileName: String) {
        // val filtered = BandPassFilter.filter(audioData, sampleRateF, bitDepth)
        val normalized = Normalizer.normalize(audioData, 0.01f)
        writeAudioDataToFile(normalized, "${outputDir?.absolutePath}/$fileName.wav")
    }

    private fun writeAudioDataToFile(audioData: FloatArray, path: String) {
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(path)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        val fileData = arrayListOf<Byte>()
        for (byte in wavFileHeader()) {
            fileData.add(byte)
        }

        try {
            audioData.to16BitPCM().forEach {
                fileData.add(it)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        updateHeaderInformation(fileData)

        os?.write(fileData.toByteArray())

        try {
            os?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Can verify through AS File Explore:
        //    /storage/emulated/0/Android/data/com.ghao.apps.gyro/files/Documents/gyro_0.wav
        log { "Wrote to $path" }
        Toast.makeText(context, "Wrote to $path", Toast.LENGTH_SHORT).show()
    }

    /**
     * Convert a FloatArray of normalized samples ([-1..1]) to 16-bit PCM (little-endian).
     */
    private fun FloatArray.to16BitPCM(): ByteArray {
        val result = ByteArray(this.size * 2) // 2 bytes per sample
        var index = 0
        for (sample in this) {
            // Clamp to [-1, +1] just in case
            val clamped = sample.coerceIn(-1f, 1f)
            // Scale to [-32767..32767]
            val intVal = (clamped * Short.MAX_VALUE).toInt()

            // Store in little-endian order
            result[index++] = (intVal and 0xFF).toByte()
            result[index++] = ((intVal shr 8) and 0xFF).toByte()
        }
        return result
    }

    /**
     * Constructs header for wav file format
     */
    private fun wavFileHeader(): ByteArray {
        val headerSize = 44
        val header = ByteArray(headerSize)

        header[0] = 'R'.toByte() // RIFF/WAVE header
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()

        header[4] = (0 and 0xff).toByte() // Size of the overall file, 0 because unknown
        header[5] = (0 shr 8 and 0xff).toByte()
        header[6] = (0 shr 16 and 0xff).toByte()
        header[7] = (0 shr 24 and 0xff).toByte()

        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()

        header[12] = 'f'.toByte() // 'fmt ' chunk
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()

        header[16] = 16 // Length of format data
        header[17] = 0
        header[18] = 0
        header[19] = 0

        header[20] = 1 // Type of format (1 is PCM)
        header[21] = 0

        header[22] = numChannels.toByte()
        header[23] = 0

        header[24] = (sampleRate and 0xff).toByte() // Sampling rate
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (sampleRate shr 16 and 0xff).toByte()
        header[27] = (sampleRate shr 24 and 0xff).toByte()

        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()

        header[32] = (numChannels * bitDepth / 8).toByte() //  16 Bits stereo
        header[33] = 0

        header[34] = bitDepth.toByte() // Bits per sample
        header[35] = 0

        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()

        header[40] = (0 and 0xff).toByte() // Size of the data section.
        header[41] = (0 shr 8 and 0xff).toByte()
        header[42] = (0 shr 16 and 0xff).toByte()
        header[43] = (0 shr 24 and 0xff).toByte()

        return header
    }

    private fun updateHeaderInformation(data: ArrayList<Byte>) {
        val fileSize = data.size
        val contentSize = fileSize - 44

        data[4] = (fileSize and 0xff).toByte() // Size of the overall file
        data[5] = (fileSize shr 8 and 0xff).toByte()
        data[6] = (fileSize shr 16 and 0xff).toByte()
        data[7] = (fileSize shr 24 and 0xff).toByte()

        data[40] = (contentSize and 0xff).toByte() // Size of the data section.
        data[41] = (contentSize shr 8 and 0xff).toByte()
        data[42] = (contentSize shr 16 and 0xff).toByte()
        data[43] = (contentSize shr 24 and 0xff).toByte()
    }

    companion object {
        const val RECORDER_SAMPLE_RATE = 430
        const val RECORDER_CHANNELS: Int = AudioFormat.CHANNEL_IN_MONO
        const val RECORDER_AUDIO_ENCODING: Int = AudioFormat.ENCODING_PCM_16BIT
        const val BITS_PER_SAMPLE: Short = 16
        const val NUMBER_CHANNELS: Short = 1
        const val BYTE_RATE = RECORDER_SAMPLE_RATE * NUMBER_CHANNELS * 16 / 8

        var BufferElements2Rec = 1024
    }
}