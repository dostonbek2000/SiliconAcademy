package com.example.siliconacademy.fragments

import Group
import Student
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.adapters.StudentRvAdapter
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentAddStudentBinding
import com.example.siliconacademy.databinding.FragmentStudentsBinding
import java.util.Calendar

class StudentsFragment : Fragment() {
    private lateinit var binding: FragmentStudentsBinding
    private lateinit var group: Group
    private var groupId: Int? = null
    private var groupTitle: String? = null
    private var groupTime: String? = null
    private var subject:String?=null
    private var fee:String?=null
    private var groupDay:String?=null

    private lateinit var codialDatabase: CodialDatabase
    private lateinit var adapter: StudentRvAdapter
    private lateinit var studentsList: ArrayList<Student>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codialDatabase = CodialDatabase(requireContext())

        groupId = arguments?.getInt("groupId")
        groupTitle = arguments?.getString("groupTitle")
        groupTime = arguments?.getString("groupTime")
        group = codialDatabase.getGroupById(groupId!!)
        subject=arguments?.getString("subject")
        fee=arguments?.getString("fee")
        groupDay=arguments?.getString("groupDay")

    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentsBinding.inflate(layoutInflater, container, false)
        binding.toolBar.title = groupTitle
        binding.toolBar.setOnMenuItemClickListener {
            findNavController().navigate(R.id.addStudentFragment, bundleOf("groupId" to groupId))
            true
        }
        studentsList = codialDatabase.getAllStudentsList()

        val studentList: ArrayList<Student> = ArrayList()
        for (student in studentsList) {
            if (student.groupId?.id == groupId) {
                studentList.add(student)
            }
        }

        binding.groupTitle.text = "Guruh: $groupTitle"
        binding.studentCount.text = "O'quvchilar soni: ${studentList.size}"
        binding.groupTime.text = "Vaqti:$groupTime"
        binding.days.text=groupDay
        binding.subject.text="Fan: $subject"
        binding.fee.text="To'lov: $fee so'm"


        adapter = StudentRvAdapter(
            object : StudentRvAdapter.OnItemClick {
                override fun onItemClick(student: Student, position: Int) {}

                override fun onItemEditClick(student: Student, position: Int) {
                    val alertDialog = AlertDialog.Builder(requireContext()).create()
                    val addStudent = FragmentAddStudentBinding.inflate(requireActivity().layoutInflater)
                    alertDialog.setView(addStudent.root)

                    addStudent.name.setText(student.name)
                    addStudent.surname.setText(student.surname)
                    addStudent.fatherName.setText(student.fatherName)

                    addStudent.save.setOnClickListener {
                        val name = addStudent.name.text.toString().trim()
                        val surname = addStudent.surname.text.toString().trim()
                        val fatherName = addStudent.fatherName.text.toString().trim()

                        if (name.isNotEmpty() && surname.isNotEmpty() && fatherName.isNotEmpty()) {
                            student.name = name
                            student.surname = surname
                            student.fatherName = fatherName
                            codialDatabase.editStudent(student)
                            adapter.notifyItemChanged(position)
                            alertDialog.dismiss()
                        } else {
                            addStudent.name.error = "Required"
                            addStudent.surname.error = "Required"
                            addStudent.fatherName.error = "Required"
                        }
                    }
                    alertDialog.show()
                }

                override fun onItemDeleteClick(student: Student, position: Int) {
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("Eslatma!")
                    alertDialog.setMessage("Rostan ham o'chirmoqchimisiz?")
                    alertDialog.setPositiveButton("Ha") { _, _ ->
                        codialDatabase.deleteStudent(student)
                        studentsList.remove(student)
                        adapter.notifyItemRemoved(position)
                        adapter.notifyDataSetChanged()
                    }
                    alertDialog.setNegativeButton("Yo'q") { dialog,_ ->
                        dialog.dismiss()
                    }
                    alertDialog.show()
                }


            },
            studentList,
            codialDatabase
        )

        binding.recyclerView.adapter = adapter

        if (group.groupPosition == 0) {
            binding.startLesson.visibility = View.GONE
        }

        binding.startLesson.setOnClickListener {
            binding.startLesson.visibility = View.GONE
            group.groupPosition = 0
            codialDatabase.editGroup(group)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        codialDatabase.deductMonthlyFeeForEachStudent(requireContext())

        studentsList.clear()
        studentsList.addAll(codialDatabase.getAllStudentsList())
        adapter.notifyDataSetChanged()
    }
    private fun getCurrentMonthInUzbek(): String {
        val months = listOf(
            "Yanvar", "Fevral", "Mart", "Aprel", "May", "Iyun",
            "Iyul", "Avgust", "Sentabr", "Oktabr", "Noyabr", "Dekabr"
        )
        val calendar = Calendar.getInstance()
        return months[calendar.get(Calendar.MONTH)]
    }
}