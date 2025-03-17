package com.example.siliconacademy.deduction

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            context?.let {
                val alarmManager = it.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(it, DeductionAlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    it,
                    1002,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val intervalMillis = 60 * 1000
                val startTime = System.currentTimeMillis() + 5000

                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    startTime,
                    intervalMillis.toLong(),
                    pendingIntent
                )
            }
        }
    }
}
