package com.example.siliconacademy.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.ActivityMainBinding
import com.example.siliconacademy.deduction.DeductionAlarmReceiver

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.mainColor)
      scheduleRepeatingDeductionCheck()
    }

    private fun scheduleRepeatingDeductionCheck() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, DeductionAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            1002,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val intervalMillis: Long = 60 * 1000
        val startTime = System.currentTimeMillis() + 5000

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startTime,
            intervalMillis,
            pendingIntent
        )
    }


}