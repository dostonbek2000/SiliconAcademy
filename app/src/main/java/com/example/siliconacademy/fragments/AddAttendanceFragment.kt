package com.example.siliconacademy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.siliconacademy.R
import com.example.siliconacademy.adapters.AddAttendanceAdapter
import com.example.siliconacademy.databinding.FragmentAddAttendanceBinding
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.models.AttendanceRecord
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddAttendanceFragment : Fragment() {
    private lateinit var binding: FragmentAddAttendanceBinding
    private lateinit var codialDatabase: CodialDatabase
    private var groupId: Int? = null
    private lateinit var attendanceList: MutableList<AttendanceRecord>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddAttendanceBinding.inflate(inflater, container, false)
        codialDatabase = CodialDatabase(requireContext())

        groupId = arguments?.getInt("groupId")
        if (groupId == null) {
            Toast.makeText(requireContext(), "Group ID not found!", Toast.LENGTH_SHORT).show()
            return binding.root
        }

        val students = codialDatabase.getAllStudentsList().filter { it.groupId?.id == groupId }
        attendanceList = students.map {
            AttendanceRecord("${it.name} ${it.surname}", false)
        }.toMutableList()

        val adapter = AddAttendanceAdapter(attendanceList) { position, isChecked ->
            attendanceList[position] = attendanceList[position].copy(isPresent = isChecked)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.save.setOnClickListener {
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
            codialDatabase.addAttendanceGroupWithStudents(date, attendanceList, groupId!!)
            Toast.makeText(requireContext(), "Attendance saved successfully", Toast.LENGTH_SHORT).show()
            //findNavController().navigate(R.id.attendanceFragment, bundleOf("groupId" to groupId))
            findNavController().popBackStack()
        }

        return binding.root
    }
}