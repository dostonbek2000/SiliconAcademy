package com.example.siliconacademy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.siliconacademy.databinding.FragmentCourseInfoBinding
import com.example.siliconacademy.models.Course

class CourseInfoFragment : Fragment() {

    private var _binding: FragmentCourseInfoBinding? = null
    private val binding get() = _binding!!
    private var course: Course? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        course = arguments?.getSerializable("course") as? Course
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseInfoBinding.inflate(inflater, container, false)

        course?.let {
            binding.toolBar.title = it.title
            binding.desc.text = it.description
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
