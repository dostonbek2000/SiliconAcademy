package com.example.siliconacademy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentCourseInfoBinding
import com.example.siliconacademy.models.Course

class CourseInfoFragment : Fragment() {
    private lateinit var binding: FragmentCourseInfoBinding
    private lateinit var course: Course

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       course=arguments?.getSerializable("course") as Course
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCourseInfoBinding.inflate(layoutInflater,container,false)
        binding.toolBar.title=course.title
        binding.desc.text=course.description
        return  binding.root
    }

}