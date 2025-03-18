package com.example.siliconacademy.fragments

import Group
import Student
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.databinding.FragmentAddStudentBinding
import com.example.siliconacademy.models.StudentViewModel
import com.example.siliconacademy.models.Teacher
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddStudentFragment : Fragment() {

    private lateinit var binding: FragmentAddStudentBinding
    private var groupId: Int? = null
    private val studentViewModel: StudentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupId = arguments?.getInt("groupId")

        if (groupId == null) {
            Toast.makeText(requireContext(), "Group ID not found", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddStudentBinding.inflate(inflater, container, false)

        binding.save.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val surname = binding.surname.text.toString().trim()
            val fatherName = binding.fatherName.text.toString().trim()
            val age = binding.age.text.toString().trim()

            if (name.isEmpty() || surname.isEmpty() || fatherName.isEmpty()) {
                Toast.makeText(requireContext(), "Iltimos barcha maydonlarni to'ldiring!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)

            val newStudent = Student(
                name = name,
                surname = surname,
                fatherName = fatherName,
                age = age,
                groupId = Group(id = groupId, groupPosition = 1, "", "", "", "", Teacher(), 0.0),
                accountBalance = 0.0,
                date = currentDate,
                removedStatus = "not removed" // This was missing in your code
            )

            studentViewModel.createStudent(newStudent)

            Toast.makeText(requireContext(), "O'quvchi muvaffaqiyatli saqlandi", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        return binding.root
    }

}
