package com.example.spendease

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.spendease.fragments.Dashboard

class ReminderReceiver :  BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel("reminder_channel","Reminder Channel",NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Create a PendingIntent to open the app's dashboard
        val dashboardIntent = Intent(context,Dashboard::class.java)
        val pendingIntent = PendingIntent.getActivity(context,0,dashboardIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context!!, "reminder_channel")
            .setSmallIcon(R.drawable.money)
            .setContentTitle("Expense Reminder")
            .setContentText("Don't forget to add today's expense.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val alarmManger = AlarmManger()
        alarmManger.scheduleAfternoonAlarm(context)
        alarmManger.scheduleEveningAlarm(context)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, notification)

    }


}