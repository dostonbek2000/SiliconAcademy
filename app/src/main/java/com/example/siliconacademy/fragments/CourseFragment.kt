package com.example.siliconacademy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.R
import com.example.siliconacademy.adapters.CourseRvAdapter
import com.example.siliconacademy.databinding.CourseAddBinding
import com.example.siliconacademy.databinding.FragmentCourseBinding
import com.example.siliconacademy.databinding.TeacherAddBinding
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.models.Course
import com.example.siliconacademy.models.Teacher


class CourseFragment : Fragment() {
private lateinit var binding:FragmentCourseBinding
private lateinit var database:CodialDatabase
private lateinit var courseList:ArrayList<Course>
private lateinit var adapter: CourseRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database=CodialDatabase(requireContext())
        courseList=database.getAllCourseList()
        adapter=CourseRvAdapter(
            object : CourseRvAdapter.OnItemClick {
                override fun onItemClick(course: Course, position: Int) {
                    findNavController().navigate(R.id.courseInfoFragment, bundleOf("course" to course))
                }

                override fun onItemDeleteClick(course: Course, position: Int) {
                    val alertDialog = android.app.AlertDialog.Builder(requireContext())

                    alertDialog.setTitle("Eslatma!")
                    alertDialog.setMessage("Rostan ham o'chirmoqchimisiz?")
                    alertDialog.setPositiveButton("Ha") { _, _ ->

                        database.deleteCourse(course) // Remove from DB
                        courseList.remove(course) // Remove from the list
                        adapter.notifyItemRemoved(position) // Notify the adapter

                        adapter.notifyDataSetChanged()
                        adapter.notifyItemRangeRemoved(position, courseList.size)

                        alertDialog.create().dismiss()
                    }

                    alertDialog.setNegativeButton("Yo'q") { _, _ ->
                        alertDialog.create().dismiss()
                    }

                    alertDialog.show()
                }

                override fun onItemEditClick(course: Course, position: Int) {
                    val alertDialog = android.app.AlertDialog.Builder(requireContext()).create()
                    val binding =
                        CourseAddBinding.inflate(requireActivity().layoutInflater)
                    alertDialog.setView(binding.root)

                    // Set existing student details
                    binding.courseTitle.setText(course.title)
                    binding.courseDesc.setText(course.description)


                    binding.add.setOnClickListener {
                        val title: String = binding.courseTitle.text.toString().trim()
                        val desc: String = binding.courseDesc.text.toString().trim()

                        if (title.isNotEmpty() && desc.isNotEmpty()) {
                            course.title = title
                            course.description = desc

                            database.editCourse(course) // Update student in the database

                            adapter.notifyItemChanged(position) // Refresh only the changed item
                            alertDialog.dismiss()
                        } else {
                            binding.courseTitle.error = "Malumot yo'q"
                            binding.courseDesc.error = "Malumot yo'q"
                        }
                    }

                    alertDialog.show()
                }
            },
            courseList
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

      binding=FragmentCourseBinding.inflate(layoutInflater,container,false)
        binding.toolBar.inflateMenu(R.menu.add)

        binding.toolBar.setOnMenuItemClickListener {
            val alertDialog = AlertDialog.Builder(requireContext()).create()

            val addDialog =
                CourseAddBinding.inflate(LayoutInflater.from(requireContext()), null, false)

            alertDialog.setView(addDialog.root)

            addDialog.add.setOnClickListener {
                val courseTitle = addDialog.courseTitle.text.toString().trim()
                val courseDesc = addDialog.courseDesc.text.toString()

                database.addCourse(Course(courseTitle, courseDesc))
                courseList.add(Course(courseTitle, courseDesc))
                adapter.notifyItemInserted(courseList.size)

                alertDialog.dismiss()
            }

            addDialog.close.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
            true
        }

        binding.recyclerView.adapter = adapter
        return binding.root
    }


}