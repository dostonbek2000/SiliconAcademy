package com.example.siliconacademy.deduction

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.siliconacademy.models.StudentViewModel

class DeductionAlarmReceiver : BroadcastReceiver() {
   private var studentViewModel=StudentViewModel()
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {

          //  val db = CodialDatabase.getInstance(it)
            studentViewModel.deductMonthlyFeeForEligibleStudents()
            studentViewModel.resetAllStudentsRemovedStatus()
           // db.deductMonthlyFeeForEachStudent(it)
            //db.resetAllStudentsRemovedStatus(it)
        }
    }
}