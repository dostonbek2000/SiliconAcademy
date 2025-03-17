package com.example.siliconacademy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.siliconacademy.R
import com.example.siliconacademy.models.AttendanceRecord

class AddAttendanceAdapter(
    private val students: List<AttendanceRecord>,
    private val onStatusChanged: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<AddAttendanceAdapter.AttendanceViewHolder>() {

    inner class AttendanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)
        val fullName: TextView = view.findViewById(R.id.fullName)

        fun bind(record: AttendanceRecord, position: Int) {
            fullName.text = record.studentFullName
            checkbox.isChecked = record.isPresent
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                onStatusChanged(position, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.attendance_student_item, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bind(students[position], position)
    }

    override fun getItemCount(): Int = students.size
}