# HourlyChime

A minimal Android app that plays a short chime and shows a brief notification at the start of every hour, even when the app is closed and the screen is off.

Built with Kotlin and Jetpack Compose as a learning project.

## Features

- One-tap **ON/OFF** toggle for the hourly chime
- Status label showing the **next chime time**
- **Test chime** button to try the notification instantly
- Permission status line (precise vs. approximate scheduling, notifications blocked)
- Notification dismisses itself after about 5 seconds
- Re-arms automatically after a phone reboot, time change, or timezone change
- No foreground service and no background thread: one wake-up per hour

## How it works

| Component | Role |
|---|---|
| `MainActivity` | Compose UI: toggle, status label, test button, permission info |
| `AlarmScheduler` | Schedules and cancels the next top-of-the-hour alarm with `AlarmManager` |
| `ChimeReceiver` | `BroadcastReceiver` that shows the chime, then schedules the next hour |
| `NotificationHelper` | Creates the notification channel and shows the chime notification |
| `Prefs` | Stores the ON/OFF setting in `SharedPreferences` |

`AlarmManager` is used instead of a timer because timers stop when the app is closed or the device sleeps. When the exact-alarm permission is not granted, the app falls back to an approximate alarm, which Android may delay by a few minutes.

## Requirements

- Android Studio (current stable version)
- Android 8.0 (API 26) or higher
- A phone or emulator

## Build and run

1. Clone the repository:
   ```
   git clone https://github.com/meera-ar-26/HourlyChime.git
   ```
2. Open the folder in Android Studio and wait for Gradle sync to finish.
3. Connect your phone (USB debugging on) or start an emulator.
4. Click **Run** (▶).

## Install on your phone

Download the latest `app-release.apk` from the [Releases](../../releases) page and open it on your phone. Android will ask you to allow installs from your browser or file manager.

## Permissions

| Permission | Why |
|---|---|
| `POST_NOTIFICATIONS` | Show the chime notification (Android 13+) |
| `SCHEDULE_EXACT_ALARM` | Fire exactly on the hour. Enable it under Settings > Alarms & reminders |
| `RECEIVE_BOOT_COMPLETED` | Re-schedule the chime after a reboot |

## Usage tips

- Turn the toggle **ON** and allow notifications when asked.
- If the app shows "Using approximate scheduling", tap **Allow precise timing** and enable the setting.
- The chime uses the default notification sound, so it stays silent if your phone is on silent or Do Not Disturb.
- Some phone brands have aggressive battery optimisation that can delay alarms. If chimes are late, exclude the app from battery optimisation.

## Known limitations

- If you force-stop the app from Settings, Android cancels its alarms until you open it again.
- Approximate scheduling can be delayed by a few minutes while the device is idle.

## Project structure

```
app/src/main/
  AndroidManifest.xml
  java/com.example.hourmark/
    MainActivity.kt
    AlarmScheduler.kt
    ChimeReceiver.kt
    NotificationHelper.kt
    Prefs.kt
```

## License

LICENSE

## Author

Meera A R · [GitHub profile](https://github.com/meera-ar-26)
