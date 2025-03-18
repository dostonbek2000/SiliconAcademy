package com.example.siliconacademy.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.R
import com.example.siliconacademy.adapters.CourseRvAdapter
import com.example.siliconacademy.databinding.CourseAddBinding
import com.example.siliconacademy.databinding.FragmentCourseBinding
import com.example.siliconacademy.models.Course
import com.example.siliconacademy.models.CourseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CourseFragment : Fragment() {

    private lateinit var binding: FragmentCourseBinding
    private lateinit var adapter: CourseRvAdapter
    private val viewModel: CourseViewModel by viewModels()
    private val courseList: MutableList<Course> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseBinding.inflate(inflater, container, false)

        binding.toolBar.inflateMenu(R.menu.add)

        adapter = CourseRvAdapter(object : CourseRvAdapter.OnItemClick {
            override fun onItemClick(course: Course, position: Int) {
                val bundle = Bundle().apply {
                    putSerializable("course", course)
                }
                findNavController().navigate(R.id.courseInfoFragment, bundle)
            }

            override fun onItemDeleteClick(course: Course, position: Int) {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("Eslatma!")
                    setMessage("Rostan ham o‘chirmoqchimisiz?")
                    setPositiveButton("Ha") { _, _ ->
                        course.id?.let { viewModel.deleteCourse(it) }
                    }
                    setNegativeButton("Yo‘q", null)
                    show()
                }
            }

            override fun onItemEditClick(course: Course, position: Int) {
                val alertDialog = AlertDialog.Builder(requireContext()).create()
                val dialogBinding = CourseAddBinding.inflate(layoutInflater)
                alertDialog.setView(dialogBinding.root)

                dialogBinding.courseTitle.setText(course.title)
                dialogBinding.courseDesc.setText(course.description)

                dialogBinding.add.setText("Update")

                dialogBinding.add.setOnClickListener {
                    val title = dialogBinding.courseTitle.text.toString().trim()
                    val desc = dialogBinding.courseDesc.text.toString().trim()

                    if (title.isNotEmpty() && desc.isNotEmpty()) {
                        course.id?.let { id -> viewModel.updateCourse(id, title, desc) }
                        alertDialog.dismiss()
                    } else {
                        dialogBinding.courseTitle.error = "Majburiy"
                        dialogBinding.courseDesc.error = "Majburiy"
                    }
                }

                dialogBinding.close.setOnClickListener {
                    alertDialog.dismiss()
                }

                alertDialog.show()
            }
        }, courseList as ArrayList<Course>)

        binding.recyclerView.adapter = adapter

        // 🌟 Collect courses from ViewModel (StateFlow)
        lifecycleScope.launch {
            viewModel.courseList.collectLatest { newList ->
                courseList.clear()
                courseList.addAll(newList)
                adapter.notifyDataSetChanged()
            }
        }
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            }
        }


        // 🔄 Initial course fetch
        viewModel.getCourses()

        // ➕ Add new course
        binding.toolBar.setOnMenuItemClickListener {
            val dialog = AlertDialog.Builder(requireContext()).create()
            val dialogBinding = CourseAddBinding.inflate(layoutInflater)
            dialog.setView(dialogBinding.root)

            dialogBinding.add.setOnClickListener {
                val title = dialogBinding.courseTitle.text.toString().trim()
                val desc = dialogBinding.courseDesc.text.toString().trim()

                if (title.isNotEmpty() && desc.isNotEmpty()) {
                    viewModel.createCourse(title, desc)
                    dialog.dismiss()
                } else {
                    dialogBinding.courseTitle.error = "Majburiy"
                    dialogBinding.courseDesc.error = "Majburiy"
                }
            }

            dialogBinding.close.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
            true
        }

        return binding.root
    }
}
