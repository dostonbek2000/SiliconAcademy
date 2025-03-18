package com.example.siliconacademy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.siliconacademy.adapters.AddAttendanceAdapter
import com.example.siliconacademy.databinding.FragmentAddAttendanceBinding
import com.example.siliconacademy.models.AttendanceRecord
import com.example.siliconacademy.models.AttendanceViewModel
import com.example.siliconacademy.models.StudentViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddAttendanceFragment : Fragment() {
    private lateinit var binding: FragmentAddAttendanceBinding
    private var groupId: Int? = null
    private lateinit var attendanceList: MutableList<AttendanceRecord>
    private var groupTitle:String?=null

    private val attendanceViewModel: AttendanceViewModel by viewModels()
    private val studentViewModel: StudentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddAttendanceBinding.inflate(inflater, container, false)

        arguments?.let {
            groupId=it.getInt("groupId")

        }
        if (groupId == null) {
            Toast.makeText(requireContext(), "Group ID not found!", Toast.LENGTH_SHORT).show()
            return binding.root
        }

        // Observe students and filter them by groupId
        studentViewModel.students.observe(viewLifecycleOwner) { studentList ->
            attendanceList = studentList.filter {
                it.groupId?.id == groupId
            }.map {
                AttendanceRecord("${it.name} ${it.surname}", false)
            }.toMutableList()

            val adapter = AddAttendanceAdapter(attendanceList) { position, isChecked ->
                attendanceList[position] = attendanceList[position].copy(isPresent = isChecked)
            }

            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter
        }

        // Load students (in case not loaded yet)
        studentViewModel.getStudents()

        binding.save.setOnClickListener {
            val date = SimpleDateFormat("dd/MM/yyyy hh:MM", Locale.getDefault()).format(Calendar.getInstance().time)
            attendanceViewModel.createGroupWithStudents(date, groupId!!, attendanceList)
            Toast.makeText(requireContext(), "Attendance saved successfully", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        return binding.root
    }
}
