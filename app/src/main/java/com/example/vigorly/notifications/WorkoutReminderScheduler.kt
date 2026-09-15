package com.example.vigorly.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object WorkoutReminderScheduler {
    private const val REQUEST_CODE = 4101

    fun sync(context: Context, enabled: Boolean, preferredTime: String) {
        if (enabled) schedule(context, preferredTime) else cancel(context)
    }

    fun schedule(context: Context, preferredTime: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pending = pendingIntent(context)
        val triggerAt = nextTriggerMillis(preferredTime)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        } else {
            @Suppress("DEPRECATION")
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        }
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent(context))
    }

    fun hourForPreferredTime(preferredTime: String): Int = when {
        preferredTime.contains("morning") -> 9
        preferredTime.contains("midday") -> 13
        preferredTime.contains("afternoon") -> 17
        preferredTime.contains("evening") -> 19
        else -> 12
    }

    private fun nextTriggerMillis(preferredTime: String): Long {
        val hour = hourForPreferredTime(preferredTime)
        val cal = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.HOUR_OF_DAY, hour)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return cal.timeInMillis
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, WorkoutReminderReceiver::class.java).apply {
            action = WorkoutReminderReceiver.ACTION_REMIND
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
