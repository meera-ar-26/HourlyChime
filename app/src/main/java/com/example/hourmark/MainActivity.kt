package com.example.hourmark

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.example.hourmark.ui.theme.HourMarkTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private var refreshTick by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createChannel(this)
        setContent {
            HourMarkTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HourmarkScreen(refreshTick)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Re-arm the alarm and re-check permissions when coming back from Settings
        if (Prefs.isEnabled(this)) AlarmScheduler.scheduleNext(this)
        refreshTick++
    }
}

@Composable
fun HourmarkScreen(refreshTick: Int) {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(Prefs.isEnabled(context)) }

    val notifOk = remember(refreshTick) {
        NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
    val exactOk = remember(refreshTick) { AlarmScheduler.canScheduleExact(context) }

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* result ignored; status line refreshes on resume */ }

    val nextText = if (enabled) {
        "Next chime: " + SimpleDateFormat("h:mm a", Locale.getDefault())
            .format(Date(AlarmScheduler.nextHourMillis()))
    } else "Chime OFF"

    Column(
        modifier = Modifier.padding(24.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(if (enabled) "ON" else "OFF", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.width(12.dp))
            Switch(
                checked = enabled,
                onCheckedChange = { on ->
                    enabled = on
                    Prefs.setEnabled(context, on)
                    if (on) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notifOk) {
                            notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        AlarmScheduler.scheduleNext(context)
                    } else {
                        AlarmScheduler.cancel(context)
                    }
                }
            )
        }

        Text(
            nextText,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Button(
            onClick = { NotificationHelper.showChime(context, "Test chime") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Test chime") }

        Spacer(Modifier.width(0.dp))

        if (!notifOk) {
            Text(
                "Notifications are blocked - no chime will show.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (exactOk) {
            Text("Precise scheduling enabled", modifier = Modifier.padding(top = 16.dp))
        } else {
            Text(
                "Using approximate scheduling (may be delayed)",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                TextButton(onClick = {
                    context.startActivity(
                        Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.parse("package:${context.packageName}")
                        )
                    )
                }) { Text("Allow precise timing") }
            }
        }
    }
}