package com.example.mobilecomputing

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat

class BackgroundNotificationService : Service() {

    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        Log.d("BackgroundService", "Service created")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Start as foreground service to prevent system killing it
            val notification = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
                .setContentTitle("Background Service")
                .setContentText("Service is running in background")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build()

            startForeground(1002, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("BackgroundService", "Service started")

        Handler(Looper.getMainLooper()).postDelayed({
            notificationHelper.showNotification(
                "App Closed Notification",
                "This notification was triggered 3 seconds after the app was closed."
            )
            Log.d("BackgroundService", "Notification sent")

            // Stop the service after sending the notification
            stopSelf()
        }, 3000) // 3000 milliseconds = 3 seconds

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BackgroundService", "Service destroyed")
    }
}