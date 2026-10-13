package com.example.hourmark

import android.content.Context
import android.content.SharedPreferences
class AppSettings(
    val enabled: Boolean = false,
    val nextChimeHour: Int = -1, // -1 means “not set”
    val nextChimeMinute: Int = -1
) {
    companion object {
        private const val PREFILE = "app_settings"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_NEXT_HOUR = "next_hour"
        private const val KEY_NEXT_MIN = "next_min"
        fun load(context: Context): AppSettings {
            val sp = context.getSharedPreferences(PREFILE,
                Context.MODE_PRIVATE)
            val enabled = sp.getBoolean(KEY_ENABLED, false)
            val nextHour = sp.getInt(KEY_NEXT_HOUR, -1)
            val nextMin = sp.getInt(KEY_NEXT_MIN, -1)
            return AppSettings(enabled, nextHour, nextMin)
        }
        fun save(context: Context, settings: AppSettings) {
            val sp = context.getSharedPreferences(PREFILE,
                Context.MODE_PRIVATE)
            val editor = sp.edit()
            editor.putBoolean(KEY_ENABLED, settings.enabled)
            editor.putInt(KEY_NEXT_HOUR, settings.nextChimeHour)
            editor.putInt(KEY_NEXT_MIN, settings.nextChimeMinute)
            editor.apply()
        }
    }
}