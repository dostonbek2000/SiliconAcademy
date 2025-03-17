package com.example.siliconacademy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.siliconacademy.databinding.FragmentTeacherInfoBinding
import com.example.siliconacademy.models.Teacher


class TeacherInfoFragment : Fragment() {

    private lateinit var binding: FragmentTeacherInfoBinding
    private lateinit var teacher: Teacher
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        teacher = arguments?.getSerializable("course") as Teacher
    }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
        ): View? {
            binding = FragmentTeacherInfoBinding.inflate(layoutInflater, container, false)
            binding.toolBar.title = teacher.title
            binding.desc.text = teacher.desc
            binding.age.text=teacher.age
            binding.subject.text=teacher.subject
            return binding.root



    }
}