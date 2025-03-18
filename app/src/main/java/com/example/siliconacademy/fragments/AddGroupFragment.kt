package com.example.siliconacademy.fragments

import Group
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.databinding.FragmentAddGroupBinding
import com.example.siliconacademy.models.GroupViewModel
import com.example.siliconacademy.models.Teacher

class AddGroupFragment : Fragment() {

    private lateinit var binding: FragmentAddGroupBinding
    private val groupViewModel: GroupViewModel by viewModels()
    private lateinit var course: Teacher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        course = arguments?.getSerializable("course") as Teacher
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddGroupBinding.inflate(layoutInflater, container, false)



        binding.save.setOnClickListener {
            val groupTitle = binding.groupTitle.text.toString().trim()
            val subject = binding.groupSubject.text.toString().trim()
            val fee = binding.groupFee.text.toString().trim()
            val groupTime = binding.groupTime.text.toString().trim()
            val groupDay = binding.groupday.text.toString()

            if (groupTitle.isEmpty() || subject.isEmpty() || fee.isEmpty()) {
                Toast.makeText(requireContext(), "Barcha maydonlarni to'ldiring!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
val teacherId=Teacher()
            val group = Group(
                groupPosition = 1,
                groupTitle = groupTitle,
                groupSubject = subject,
                groupTime = groupTime,
                groupDay = groupDay,
                courseId =course,
                fee = fee.toDouble()
            )

            groupViewModel.createGroup(group)
            Toast.makeText(requireContext(), "Saqlandi", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        return binding.root
    }
}
