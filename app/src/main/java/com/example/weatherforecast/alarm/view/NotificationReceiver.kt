package com.example.weatherforecast.alarm.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weatherforecast.utils.Constant
import com.example.weatherforecast.R


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("message") ?: ""
        context?.let {
            val notificationManager =
                it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    Constant.CHANNEL_ID,
                    Constant.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            // Build and show the notification using NotificationCompat
            val notificationBuilder = NotificationCompat.Builder(it, Constant.CHANNEL_ID)
                .setSmallIcon(R.drawable.cloud)
                .setContentTitle("Weather Status Today is $message")
                .setContentText("Be Careful")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)

            notificationManager.notify(2, notificationBuilder.build())
        }
    }
}