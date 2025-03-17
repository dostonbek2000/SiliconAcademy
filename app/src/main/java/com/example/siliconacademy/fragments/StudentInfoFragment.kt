package com.example.siliconacademy.fragments

import Student
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentStudentInfoBinding

class StudentInfoFragment : Fragment() {
 private lateinit var binding: FragmentStudentInfoBinding
private lateinit var student:Student
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         student=arguments?.getSerializable("studentDetails") as Student
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentInfoBinding.inflate(layoutInflater, container, false)

        binding.studentFullName.text= "${student.name} ${student.surname}"
        binding.fatherName.text=student.fatherName.toString()
        binding.studentAge.text=student.age.toString()
        binding.accountBalance.text=student.accountBalance.toString()
        binding.groupName.text=student.groupId?.groupTitle.toString()
        binding.date.text=student.date.toString()
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }


      return  binding.root
    }

}