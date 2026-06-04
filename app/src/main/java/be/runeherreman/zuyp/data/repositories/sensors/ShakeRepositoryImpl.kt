package be.runeherreman.zuyp.data.repositories.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import be.runeherreman.zuyp.domain.repository.sensors.ShakeRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.math.sqrt

class ShakeRepositoryImpl @Inject constructor(
    private val sensorManager: SensorManager
) : ShakeRepository {

    override fun shakes(): Flow<Unit> = callbackFlow {
        var shakingStartedAt = 0L
        var lastShakeAt      = 0L

        val listener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val force = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH
                val now = System.currentTimeMillis()

                if (force >= SHAKE_THRESHOLD) {
                    lastShakeAt = now
                    if (shakingStartedAt == 0L) shakingStartedAt = now
                    if (now - shakingStartedAt >= REQUIRED_DURATION_MS) {
                        shakingStartedAt = 0L
                        trySend(Unit)
                    }
                } else if (now - lastShakeAt > GRACE_PERIOD_MS) {
                    shakingStartedAt = 0L
                }
            }
        }

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        awaitClose { sensorManager.unregisterListener(listener) }
    }

    companion object {
        private const val SHAKE_THRESHOLD      = 8f  // meter per seconde
        private const val REQUIRED_DURATION_MS = 1_000L
        private const val GRACE_PERIOD_MS      = 400L  // tolerated gap between shake peaks
    }
}
