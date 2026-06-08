package be.runeherreman.zuyp.data.repositories.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import be.runeherreman.zuyp.domain.repository.sensors.ShakeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import kotlin.math.sqrt

class ShakeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ShakeRepository {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _shakes = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override fun shakes(): Flow<Unit> = _shakes.asSharedFlow()

    private var shakingStartedAt = 0L
    private var lastShakeAt = 0L
    private var listenerCount = 0

    private val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

            val (x, y, z) = Triple(event.values[0], event.values[1], event.values[2])
            val force = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
            val now = System.currentTimeMillis()

            if (force >= SHAKE_THRESHOLD) {
                lastShakeAt = now
                if (shakingStartedAt == 0L) {
                    shakingStartedAt = now
                }
                if (now - shakingStartedAt >= REQUIRED_DURATION_MS) {
                    shakingStartedAt = 0L
                    _shakes.tryEmit(Unit)
                }
            } else if (now - lastShakeAt > GRACE_PERIOD_MS) {
                shakingStartedAt = 0L
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    }

    override fun startListening() {
        val sensor = accelerometer ?: return
        synchronized(this) {
            listenerCount++
            if (listenerCount == 1) {
                sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    override fun stopListening() {
        synchronized(this) {
            if (listenerCount > 0) {
                listenerCount--
            }
            if (listenerCount == 0) {
                sensorManager.unregisterListener(listener)
                shakingStartedAt = 0L
                lastShakeAt = 0L
            }
        }
    }

    companion object {
        private const val SHAKE_THRESHOLD      = 8f  // meter per seconde
        private const val REQUIRED_DURATION_MS = 1_000L
        private const val GRACE_PERIOD_MS      = 400L  // tolerated gap between shake peaks
    }
}
