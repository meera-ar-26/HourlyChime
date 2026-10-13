package com.example.hourmark

import android.content.Context

object Prefs {
    private fun sp(c: Context) =
        c.getSharedPreferences("hourmark", Context.MODE_PRIVATE)

    fun isEnabled(c: Context) = sp(c).getBoolean("enabled", false)
    fun setEnabled(c: Context, v: Boolean) = sp(c).edit().putBoolean("enabled", v).apply()
}