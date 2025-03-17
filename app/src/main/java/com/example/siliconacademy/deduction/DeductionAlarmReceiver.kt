package com.example.siliconacademy.deduction

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.siliconacademy.db.CodialDatabase

class DeductionAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val db = CodialDatabase.getInstance(it)
            db.deductMonthlyFeeForEachStudent(it)
            db.resetAllStudentsRemovedStatus(it)
        }
    }
}