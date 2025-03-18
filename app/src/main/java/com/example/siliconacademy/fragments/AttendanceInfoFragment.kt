package com.example.siliconacademy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentAttendanceInfoBinding
import com.example.siliconacademy.models.AttendanceRecord
import com.example.siliconacademy.models.AttendanceViewModel

class AttendanceInfoFragment : Fragment() {
    private lateinit var binding: FragmentAttendanceInfoBinding
    private val viewModel: AttendanceViewModel by viewModels()

    private var adapter: AttendanceInfoAdapter? = null
    private var attendanceList: List<AttendanceRecord> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttendanceInfoBinding.inflate(inflater, container, false)

        val attendanceGroupId = arguments?.getInt("attendanceGroupId")
        if (attendanceGroupId == null) {
            Toast.makeText(requireContext(), "Attendance Group ID not found!", Toast.LENGTH_SHORT).show()
            return binding.root
        }
        // ✅ Observe isLoading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // ✅ Observe error message
        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // ✅ Observe success/info messages
        viewModel.message.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.attendanceRecords.observe(viewLifecycleOwner) { records ->
            attendanceList = records
            if (records.isEmpty()) {
                Toast.makeText(requireContext(), "No students found for this group.", Toast.LENGTH_SHORT).show()
            }
            adapter?.submitList(records)
        }

        // Load attendance from GAS
        viewModel.getAttendanceByAttendanceGroupId(attendanceGroupId)

        adapter = AttendanceInfoAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    class AttendanceInfoAdapter : RecyclerView.Adapter<AttendanceInfoViewHolder>() {
        private var data = listOf<AttendanceRecord>()

        fun submitList(newList: List<AttendanceRecord>) {
            data = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceInfoViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.attendance_student_item, parent, false)
            return AttendanceInfoViewHolder(view)
        }

        override fun onBindViewHolder(holder: AttendanceInfoViewHolder, position: Int) {
            val record = data[position]
            holder.fullName.text = record.studentFullName
            holder.checkbox.isChecked = record.isPresent
            holder.checkbox.isEnabled = false
        }

        override fun getItemCount(): Int = data.size
    }

    class AttendanceInfoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullName: TextView = view.findViewById(R.id.fullName)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)
    }
}
