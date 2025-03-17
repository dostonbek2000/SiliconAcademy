package com.example.siliconacademy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.R
import com.example.siliconacademy.adapters.TeacherRvAdapter
import com.example.siliconacademy.databinding.FragmentTeacherBinding
import com.example.siliconacademy.databinding.TeacherAddBinding
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.models.Teacher
import com.example.siliconacademy.utils.Object.teacherId

class TeacherFragment : Fragment() {

    private lateinit var binding: FragmentTeacherBinding
    private lateinit var codialDatabase: CodialDatabase
    private lateinit var teacherList: ArrayList<Teacher>
    private lateinit var adapter: TeacherRvAdapter

    private var query: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codialDatabase = CodialDatabase(requireContext())
        teacherList = codialDatabase.getAllTeachersList()
        query = arguments?.getInt("query")

        adapter = TeacherRvAdapter(object : TeacherRvAdapter.OnItemClick {
            override fun onItemClick(teacher: Teacher, position: Int) {
                teacherId = teacher.id
                when (query) {
                    1 -> {
                        findNavController().navigate(
                            R.id.teacherInfoFragment,
                            bundleOf("course" to teacher)
                        )
                    }

                    2 -> {
                        findNavController().navigate(
                            R.id.groupHomeFragment,
                            bundleOf("course" to teacher)
                        )


                    }

                    3 -> {
                        findNavController().navigate(
                            R.id.teacherFragment,
                            bundleOf("course" to teacher)
                        )
                    }
                }

            }

            override fun onItemDeleteClick(teacher: Teacher, position: Int) {
                val alertDialog = android.app.AlertDialog.Builder(requireContext())

                alertDialog.setTitle("Eslatma!")
                alertDialog.setMessage("Rostan ham o'chirmoqchimisiz?")
                alertDialog.setPositiveButton("Ha") { _, _ ->

                    codialDatabase.deleteTeacher(teacher) // Remove from DB
                    teacherList.remove(teacher) // Remove from the list
                    adapter.notifyItemRemoved(position) // Notify the adapter

                    adapter.notifyDataSetChanged()
                    adapter.notifyItemRangeRemoved(position, teacherList.size)

                    alertDialog.create().dismiss()
                }

                alertDialog.setNegativeButton("Yo'q") { _, _ ->
                    alertDialog.create().dismiss()
                }

                alertDialog.show()
            }

            override fun onItemEditClick(teacher: Teacher, position: Int) {
                val alertDialog = android.app.AlertDialog.Builder(requireContext()).create()
                val binding =
                    TeacherAddBinding.inflate(requireActivity().layoutInflater)
                alertDialog.setView(binding.root)

                // Set existing student details
                binding.courseTitle.setText(teacher.title)
                binding.courseDesc.setText(teacher.desc)
                binding.courseAge.setText(teacher.age)
                binding.courseSubject.setText(teacher.subject)


                binding.add.setOnClickListener {
                    val name: String = binding.courseTitle.text.toString().trim()
                    val fatherName: String = binding.courseDesc.text.toString().trim()
                    val age:String=binding.courseAge.text.toString().trim()
                    val subject:String=binding.courseSubject.text.toString().trim()

                    if (name.isNotEmpty() && fatherName.isNotEmpty()&&age.isNotEmpty()&&subject.isNotEmpty()) {
                        teacher.title = name
                        teacher.desc = fatherName
                        teacher.age=age
                        teacher.subject=subject

                        codialDatabase.editTeacher(teacher) // Update student in the database

                        adapter.notifyItemChanged(position) // Refresh only the changed item
                        alertDialog.dismiss()
                    } else {
                        binding.courseTitle.error = "Malumot yo'q"
                        binding.courseDesc.error = "Malumot yo'q"
                    }
                }

                alertDialog.show()
            }
        }, teacherList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherBinding.inflate(layoutInflater, container, false)

        when (query) {
            1 -> {
                binding.toolBar.inflateMenu(R.menu.add)
                binding.toolBar.setOnMenuItemClickListener {
                    val alertDialog = AlertDialog.Builder(requireContext()).create()

                    val addDialog =
                        TeacherAddBinding.inflate(LayoutInflater.from(requireContext()), null, false)

                    alertDialog.setView(addDialog.root)

                    addDialog.add.setOnClickListener {
                        val courseTitle: String = addDialog.courseTitle.text.toString()
                        val courseDesc: String = addDialog.courseDesc.text.toString()
                        val age:String=addDialog.courseAge.text.toString().trim()
                        val subject:String=addDialog.courseSubject.text.toString().trim()

                        codialDatabase.addTeacher(Teacher(courseTitle, courseDesc,age,subject))
                        teacherList.add(Teacher(courseTitle, courseDesc,age,subject))
                        adapter.notifyItemInserted(teacherList.size)

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