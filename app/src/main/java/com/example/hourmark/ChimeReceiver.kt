package com.example.hourmark

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ChimeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AlarmScheduler.ACTION_CHIME) {
            NotificationHelper.showChime(context)
        }
        // Chime alarm, reboot, time change or permission change: always re-arm
        if (Prefs.isEnabled(context)) {
            AlarmScheduler.scheduleNext(context)
        }
    }
}