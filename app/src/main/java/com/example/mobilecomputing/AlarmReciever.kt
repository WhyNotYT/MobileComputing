package com.example.mobilecomputing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm received")
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(
            "Scheduled Notification",
            "This notification was triggered 3 seconds after the app was closed."
        )
    }
}