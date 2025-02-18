package com.example.mobilecomputing

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlin.math.sqrt

class SensorBackgroundService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var notificationHelper: NotificationHelper

    // For storing sensor data that can be observed
    companion object {
        private val _sensorData = MutableLiveData<SensorValues>()
        val sensorData: LiveData<SensorValues> = _sensorData
        const val THRESHOLD = 15.0f // Threshold for significant movement
    }

    data class SensorValues(
        val x: Float = 0f,
        val y: Float = 0f,
        val z: Float = 0f,
        val magnitude: Float = 0f
    )

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)

        // Initialize sensor
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Register sensor listener
        accelerometer?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                val x = it.values[0]
                val y = it.values[1]
                val z = it.values[2]

                // Calculate the magnitude of movement
                val magnitude = sqrt(x*x + y*y + z*z)

                // Update LiveData
                _sensorData.postValue(SensorValues(x, y, z, magnitude))

                // Check for significant movement
                if (magnitude > THRESHOLD) {
                    notificationHelper.showNotification(
                        "Significant Movement Detected!",
                        "The device detected movement with magnitude: ${magnitude.toInt()}"
                    )
                    Log.d("SensorService", "Significant movement: $magnitude")
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this implementation
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}