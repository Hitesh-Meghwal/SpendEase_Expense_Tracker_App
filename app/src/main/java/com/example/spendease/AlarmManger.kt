package com.example.spendease

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class AlarmManger {
    fun scheduleAfternoonAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        val alarmTime = calendar.timeInMillis

        alarmManager.setRepeating(
            AlarmManager.RTC, alarmTime,
            AlarmManager.INTERVAL_DAY, // Repeat daily
            getPendingIntent(context)
        )
    }
    fun scheduleEveningAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 20)
        val alarmTime = calendar.timeInMillis

        alarmManager.setRepeating(
            AlarmManager.RTC, alarmTime,
            AlarmManager.INTERVAL_DAY, // Repeat daily
            getPendingIntent(context)
        )
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}