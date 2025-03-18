package com.example.siliconacademy.deduction


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.siliconacademy.models.StudentViewModel

class MonthlyDeductionService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
   private lateinit var viewModel: StudentViewModel

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceNotification()
        startRepeatingDeduction()
    }

    private fun startRepeatingDeduction() {
        runnable = object : Runnable {
            override fun run() {
                viewModel.deductMonthlyFeeForEligibleStudents()
                viewModel.resetAllStudentsRemovedStatus()
                handler.postDelayed(this, 60_000) // Run every 60 seconds
            }
        }
        handler.post(runnable)
    }

    private fun startForegroundServiceNotification() {
        val channelId = "monthly_deduction_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Monthly Deduction Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Monthly Deduction Running")
            .setContentText("Deduction is automatically being checked every minute")
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}