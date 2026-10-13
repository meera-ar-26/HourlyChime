package com.example.hourmark

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object AlarmScheduler {
    const val ACTION_CHIME = "com.example.hourmark.CHIME"

    fun canScheduleExact(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ChimeReceiver::class.java).setAction(ACTION_CHIME)
        return PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun nextHourMillis(): Long =
        Calendar.getInstance().apply {
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.HOUR_OF_DAY, 1)
        }.timeInMillis

    fun scheduleNext(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java)
        val pi = pendingIntent(context)
        val t = nextHourMillis()
        if (canScheduleExact(context)) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pi)
        } else {
            // Not allowed to be exact: may be delayed a few minutes
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pi)
        }
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java)
        am.cancel(pendingIntent(context))
    }
}