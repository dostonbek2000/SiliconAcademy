package com.example.siliconacademy.fragments

import Teacher
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.siliconacademy.adapters.TeacherRvAdapter
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.R
import com.example.siliconacademy.models.Course
import com.example.siliconacademy.databinding.FragmentTeachersBinding
import com.example.siliconacademy.databinding.TeacherAddBinding

class TeachersFragment : Fragment() {

    private lateinit var binding: FragmentTeachersBinding
    private lateinit var codialDatabase: CodialDatabase

    private lateinit var course: Course

    private lateinit var teachersList: ArrayList<Teacher>
    private lateinit var teacherList: ArrayList<Teacher>
    private lateinit var adapter: TeacherRvAdapter

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codialDatabase = CodialDatabase(requireContext())
        course = arguments?.getSerializable("course") as Course

        teachersList = codialDatabase.getAllTeachersList()

        teacherList = ArrayList()
        for (i in teachersList.indices) {
            if (teachersList[i].courseId?.id == course.id) {
                teacherList.add(teachersList[i])
            }
        }

        adapter = TeacherRvAdapter(object : TeacherRvAdapter.OnItemClick {
            override fun onItemClick(teacher: Teacher, position: Int) {

            }

            override fun onItemEditClick(teacher: Teacher, position: Int) {
                val alertDialog = AlertDialog.Builder(requireContext()).create()

                val addTeacher =
                    TeacherAddBinding.inflate(LayoutInflater.from(requireContext()), null, false)

                alertDialog.setView(addTeacher.root)

                addTeacher.name.setText(teacher.name)
                addTeacher.surname.setText(teacher.surname)
                addTeacher.fatherName.setText(teacher.fatherName)

                addTeacher.add.setOnClickListener {
                    val name: String = addTeacher.name.text.toString()
                    val surname: String = addTeacher.surname.text.toString()
                    val fatherName: String = addTeacher.fatherName.text.toString()

                    teacher.name = name
                    teacher.surname = surname
                    teacher.fatherName = fatherName
                    codialDatabase.editTeacher(teacher)
                    adapter.notifyItemChanged(teachersList.size)
                    adapter.notifyItemRangeChanged(position, teachersList.size)
                    alertDialog.dismiss()
                }

                addTeacher.close.setOnClickListener {
                    alertDialog.dismiss()
                }

                alertDialog.show()
            }

            override fun onItemDeleteClick(teacher: Teacher, position: Int) {
                val alertDialog = AlertDialog.Builder(requireContext())

                alertDialog.setTitle("Eslatma!")
                alertDialog.setMessage("Ushbu o'qituvchi o'chirilsa unga tegishli bo'lgan barcha guruh va o'quvchilar o'chiriladi. Rostan ham o'chirmoqchimisiz?")
                alertDialog.setPositiveButton("Ha") { _, _ ->

//                    for (i in 0 until codialDatabase.getAllGroupsList().size) {
//                        if (teacher.id == codialDatabase.getAllGroupsList()[i].teacherId?.id) {
//                            codialDatabase.deleteGroup(codialDatabase.getAllGroupsList()[i])
//                        }
//                    }

                    codialDatabase.deleteTeacher(teacher)
                    teacherList.remove(teacher)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeRemoved(position, teacherList.size)
                    alertDialog.create().dismiss()
                }

                alertDialog.setNegativeButton("Yo'q") { _, _ ->
                    alertDialog.create().dismiss()
                }

                alertDialog.show()
            }
        }, teacherList)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeachersBinding.inflate(layoutInflater, container, false)

        binding.toolBar.inflateMenu(R.menu.add)
        binding.toolBar.setOnMenuItemClickListener {

            val alertDialog = AlertDialog.Builder(requireContext()).create()

            val addTeacher =
                TeacherAddBinding.inflate(LayoutInflater.from(requireContext()), null, false)

            alertDialog.setView(addTeacher.root)

            addTeacher.add.setOnClickListener {
                val name: String = addTeacher.name.text.toString()
                val surname: String = addTeacher.surname.text.toString()
                val fatherName: String = addTeacher.fatherName.text.toString()

                codialDatabase.addTeacher(Teacher(name, surname, fatherName, course))
                teacherList.add(Teacher(name, surname, fatherName, course))
                adapter.notifyItemInserted(teacherList.size)
                alertDialog.dismiss()
            }

            addTeacher.close.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()

            true
        }

        binding.recyclerView.adapter = adapter

        return binding.root
    }
}