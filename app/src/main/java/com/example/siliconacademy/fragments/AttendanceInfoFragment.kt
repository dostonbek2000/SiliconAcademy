package com.example.siliconacademy.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentAttendanceHomeBinding
import com.example.siliconacademy.databinding.FragmentAttendanceInfoBinding
import com.example.siliconacademy.db.CodialDatabase

class AttendanceInfoFragment : Fragment() {
    private lateinit var binding: FragmentAttendanceInfoBinding
    private lateinit var codialDatabase: CodialDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAttendanceInfoBinding.inflate(inflater, container, false)
        codialDatabase = CodialDatabase(requireContext())

        val attendanceGroupId = arguments?.getInt("attendanceGroupId")
        if (attendanceGroupId == null) {
            Toast.makeText(requireContext(), "Attendance Group ID not found!", Toast.LENGTH_SHORT).show()
            return binding.root
        }

        val students = codialDatabase.getAttendanceByGroupId(attendanceGroupId)

        Log.d("DEBUG_INFO", "Group ID = $attendanceGroupId, Students Count = ${students.size}")
        students.forEach {
            Log.d("DEBUG_INFO", it.studentFullName + " | Present: " + it.isPresent)
        }
        if (students.isEmpty()) {
            Toast.makeText(requireContext(), "No students found for this attendance group.", Toast.LENGTH_LONG).show()
        }

        val adapter = object : RecyclerView.Adapter<AttendanceInfoViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceInfoViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.attendance_student_item, parent, false)
                return AttendanceInfoViewHolder(view)
            }

            override fun onBindViewHolder(holder: AttendanceInfoViewHolder, position: Int) {
                val record = students[position]
                holder.fullName.text = record.studentFullName
                holder.checkbox.isChecked = record.isPresent
                holder.checkbox.isEnabled = false
            }

            override fun getItemCount(): Int = students.size
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    class AttendanceInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullName: TextView = view.findViewById(R.id.fullName)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)
    }
}