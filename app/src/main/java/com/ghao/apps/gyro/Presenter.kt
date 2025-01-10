package com.ghao.apps.gyro

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Stable
import com.ghao.apps.gyro.util.MyFFT
import com.ghao.apps.gyro.util.Normalizer
import com.ghao.apps.gyro.util.RollingStats
import com.ghao.apps.gyro.util.WavFileWriter
import com.ghao.apps.gyro.util.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.sqrt

/**
 * Ideal Range: 300–3400 Hz for general speech recognition.
 * Noise-Resistant Range: 300–1000 Hz for basic speech patterns and less environmental overlap.
 * For gyroscope-based systems, focus on the 85–255 Hz range, as this is most viable given hardware constraints.
 */
@Stable
class Presenter(private val context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val wavFileWriter = WavFileWriter(context)
    private var fileNameNumber = 0

    private val rollingStats = RollingStats(430)
    private var count = 0
    private var ts = 0L

    private val cache = mutableListOf<Float>()

    private val gyroscopeListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val magnitude = sqrt(x * x + y * y + z * z)
            rollingStats.add(magnitude)
            cache.add(magnitude)

            _uiState.value = _uiState.value
                .copy(
                    x = x,
                    y = y,
                    z = z,
                    magnitude = magnitude,
                    avg = rollingStats.getAverage(),
                    min = rollingStats.getMin() ?: Float.NaN,
                    max = rollingStats.getMax() ?: Float.NaN,
                )

            // If it's been 1 second since the last update, update the count
            val current = System.currentTimeMillis()
            if (current - ts >= 1000) {
                _uiState.value = _uiState.value.copy(freq = count)
                count = 0
                ts = current
                // log { "FFT: ${rollingStats.performFFT().asList()}" }
            } else {
                count++
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private val _uiState: MutableStateFlow<GyroscopeData> = MutableStateFlow(GyroscopeData())
    val uiState = _uiState.asStateFlow()

    fun startRecording() {
        sensorManager.registerListener(
            gyroscopeListener,
            gyroscope,
            SensorManager.SENSOR_DELAY_FASTEST
        )
        _uiState.value = _uiState.value.copy(isRecording = true)
    }

    fun stopRecording() {
        sensorManager.unregisterListener(gyroscopeListener)
        _uiState.value = GyroscopeData()
        rollingStats.reset()
        count = 0
        ts = 0L

        CoroutineScope(Dispatchers.IO).launch {
            log { "Recorded: ${cache.toList()}" }
            // val normalized = wavFileWriter.bandPassFilter300to3400(Normalizer.normalize(cache).toFloatArray())
            // log { "FFT: ${MyFFT.performFFT(normalized.toList()).toList()}" }
            wavFileWriter.writeToWav(cache.toFloatArray(), "gyro_${fileNameNumber++}")
            cache.clear()
        }
    }

    fun dispose() {
        stopRecording()
    }
}

@Stable
data class GyroscopeData(
    val isRecording: Boolean = false, // move out
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val magnitude: Float = 0f,
    val avg: Float = 0f,
    val min: Float = 0f,
    val max: Float = 0f,
    val freq: Int = 0,
)