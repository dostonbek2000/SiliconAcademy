package com.example.siliconacademy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.adapters.CourseRvAdapter
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.CourseAddBinding
import com.example.siliconacademy.databinding.FragmentCoursesBinding
import com.example.siliconacademy.models.Course
import com.example.siliconacademy.utils.Object.courseId

class CoursesFragment : Fragment() {

    private lateinit var binding: FragmentCoursesBinding
    private lateinit var codialDatabase: CodialDatabase
    private lateinit var coursesList: ArrayList<Course>
    private lateinit var adapter: CourseRvAdapter

    private var query: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codialDatabase = CodialDatabase(requireContext())
        coursesList = codialDatabase.getAllCoursesList()
        query = arguments?.getInt("query")

        adapter = CourseRvAdapter(object : CourseRvAdapter.OnItemClick {
            override fun onItemClick(course: Course, position: Int) {
                courseId = course.id
                when (query) {
                    1 -> {
                    }
                    2 -> {
                        findNavController().navigate(
                            R.id.groupHomeFragment,
                            bundleOf("course" to course)
                        )
                    }
                    3 -> {
                        findNavController().navigate(
                            R.id.teachersFragment,
                            bundleOf("course" to course)
                        )
                    }
                }

            }
        }, coursesList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCoursesBinding.inflate(layoutInflater, container, false)

        when (query) {
            1 -> {
                binding.toolBar.inflateMenu(R.menu.add)
                binding.toolBar.setOnMenuItemClickListener {
                    val alertDialog = AlertDialog.Builder(requireContext()).create()

                    val addDialog =
                        CourseAddBinding.inflate(LayoutInflater.from(requireContext()), null, false)

                    alertDialog.setView(addDialog.root)

                    addDialog.add.setOnClickListener {
                        val courseTitle: String = addDialog.courseTitle.text.toString()
                        val courseDesc: String = addDialog.courseDesc.text.toString()

                        codialDatabase.addCourse(Course(courseTitle, courseDesc))
                        coursesList.add(Course(courseTitle, courseDesc))
                        adapter.notifyItemInserted(coursesList.size)

                        alertDialog.dismiss()
                    }

                    addDialog.close.setOnClickListener {
                        alertDialog.dismiss()
                    }

                    alertDialog.show()
                    true
                }
            }

            2 -> {

            }

            3 -> {

            }
        }

        binding.recyclerView.adapter = adapter

        return binding.root
    }
}