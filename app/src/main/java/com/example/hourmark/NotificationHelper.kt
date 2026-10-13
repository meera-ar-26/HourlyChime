package com.example.hourmark

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Calendar

object NotificationHelper {
    private const val CHANNEL_ID = "hourmark_chime"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Hourly chime", NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Short chime at the start of every hour" }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission") // we check areNotificationsEnabled() below
    fun showChime(context: Context, message: String? = null) {
        createChannel(context)
        val nm = NotificationManagerCompat.from(context)
        if (!nm.areNotificationsEnabled()) return

        val hour12 = Calendar.getInstance().get(Calendar.HOUR).let { if (it == 0) 12 else it }
        val text = message ?: "It's $hour12 o'clock"

        val n = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Hourmark")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setTimeoutAfter(5000)   // system removes it after ~2 s, no Handler needed
            .setAutoCancel(true)
            .build()
        nm.notify(1, n)
    }
}