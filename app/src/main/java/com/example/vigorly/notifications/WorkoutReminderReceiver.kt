package com.example.vigorly.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.vigorly.MainActivity
import com.example.vigorly.R
import com.example.vigorly.VigorlyApplication
import com.example.vigorly.core.testing.UiTestEnvironment
import com.example.vigorly.data.local.VigorlyPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class WorkoutReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            ACTION_REMIND -> handle(context, intent.action == ACTION_REMIND)
        }
    }

    private fun handle(context: Context, showNow: Boolean) {
        if (UiTestEnvironment.isInstrumentedTest) return
        val appContext = context.applicationContext
        val (enabled, preferredTime) = resolveReminderPrefs(appContext)
        if (!enabled) {
            WorkoutReminderScheduler.cancel(appContext)
            return
        }
        if (showNow) {
            showNotification(appContext)
        }
        WorkoutReminderScheduler.schedule(appContext, preferredTime)
    }

    private fun resolveReminderPrefs(context: Context): Pair<Boolean, String> {
        val repo = (context as? VigorlyApplication)?.repository
            ?: (context.applicationContext as? VigorlyApplication)?.repository
        if (repo != null) {
            return repo.notificationsEnabled.value to repo.preferredTime.value
        }
        return runBlocking {
            val prefs = VigorlyPreferencesDataStore(context)
            prefs.notificationsEnabled.first() to prefs.preferredTime.first()
        }
    }

    private fun showNotification(context: Context) {
        ensureChannel(context)
        val open = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(context.getString(R.string.reminder_notification_title))
            .setContentText(context.getString(R.string.reminder_notification_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        runCatching {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        }
    }

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.reminder_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.reminder_channel_desc)
        }
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val ACTION_REMIND = "com.example.vigorly.action.WORKOUT_REMIND"
        private const val CHANNEL_ID = "vigorly_workout_reminders"
        private const val NOTIFICATION_ID = 4101
    }
}
