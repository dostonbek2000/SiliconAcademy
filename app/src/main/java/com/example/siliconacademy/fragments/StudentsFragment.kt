package com.example.siliconacademy.fragments

import Group
import Student
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.adapters.StudentRvAdapter
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentAddStudentBinding
import com.example.siliconacademy.databinding.FragmentStudentsBinding
import com.example.siliconacademy.models.GroupViewModel
import com.example.siliconacademy.models.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

class StudentsFragment : Fragment() {
    private lateinit var binding: FragmentStudentsBinding
    private var group: Group? = null
    private var groupId: Int = 0
    private var groupTitle: String? = null
    private var groupTime: String? = null
    private var subject: String? = null
    private var fee: String? = null
    private val groupViewModel: GroupViewModel by viewModels()
    private val studentViewModel: StudentViewModel by viewModels()
    private var groupDay: String? = null
    private lateinit var adapter: StudentRvAdapter
    private val studentList: ArrayList<Student> = ArrayList()
    private var isUiReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getInt("groupId", 0)
            groupTitle = it.getString("groupTitle")
            groupTime = it.getString("groupTime")
            subject = it.getString("subject")
            fee = it.getString("fee") ?: "0"
            groupDay = it.getString("groupDay")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudentsBinding.inflate(layoutInflater, container, false)

        binding.toolBar.title = groupTitle

        binding.toolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_add -> {
                    findNavController().navigate(R.id.addStudentFragment, bundleOf("groupId" to groupId))
                    true
                }
                R.id.menu_attendance -> {
                    findNavController().navigate(R.id.attendanceFragment, bundleOf("groupId" to groupId))
                    true
                }
                else -> false
            }
        }

        binding.groupTitle.text = "Guruh: $groupTitle"
        binding.groupTime.text = "Vaqti: $groupTime"
        binding.days.text = formatDateToReadable(groupDay.toString())
        binding.subject.text = "Fan: $subject"
        binding.fee.text = "To'lov: $fee so'm"

        setupAdapter()
        observeGroupData()
        loadData()

        isUiReady = true

        return binding.root
    }

    private fun formatDateToReadable(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(dateString)
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatter.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    private fun setupAdapter() {
        adapter = StudentRvAdapter(
            object : StudentRvAdapter.OnItemClick {
                override fun onItemClick(student: Student, position: Int) {
                    findNavController().navigate(R.id.studentInfoFragment, bundleOf("studentDetails" to student))
                }

                override fun onItemEditClick(student: Student, position: Int) {
                    showEditStudentDialog(student, position)
                }

                override fun onItemDeleteClick(student: Student, position: Int) {
                    showDeleteConfirmationDialog(student, position)
                }
            }, studentList
        )
        binding.recyclerView.adapter = adapter
    }

    private fun showEditStudentDialog(student: Student, position: Int) {
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
                studentViewModel.updateStudent(student)
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

    private fun showDeleteConfirmationDialog(student: Student, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eslatma!")
            .setMessage("Rostan ham o'chirmoqchimisiz?")
            .setPositiveButton("Ha") { _, _ ->
                student.id?.let { studentViewModel.deleteStudent(it) }
                studentList.remove(student)
                adapter.notifyItemRemoved(position)
                adapter.notifyDataSetChanged()
                if (isUiReady) {
                    binding.studentCount.text = "O'quvchilar soni: ${studentList.size}"
                }
            }
            .setNegativeButton("Yo'q") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun loadData() {
        studentViewModel.getStudents()
        studentViewModel.students.observe(viewLifecycleOwner) { allStudents ->
            studentList.clear()
            val groupStudents = allStudents.filter { it.groupId?.id == groupId }
            studentList.addAll(groupStudents.reversed()) // Newest students at top
            adapter.notifyDataSetChanged()
            if (isUiReady) {
                binding.studentCount.text = "O'quvchilar soni: ${studentList.size}"
            }
        }

        studentViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isUiReady) {
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        studentViewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            msg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeGroupData() {
        groupViewModel.groups.observe(viewLifecycleOwner) { allGroups ->
            val foundGroup = allGroups.find { it.id == groupId }
            foundGroup?.let {
                group = it
                binding.startLesson.visibility = if (it.groupPosition == 0) View.GONE else View.VISIBLE
            }
        }

        binding.startLesson.setOnClickListener {
            group?.let {
                it.groupPosition = 0
                groupViewModel.updateGroup(it)
                binding.startLesson.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        studentViewModel.deductMonthlyFeeForEligibleStudents()
        studentViewModel.setGroupViewModel(groupViewModel)
        groupViewModel.getGroups()
        studentViewModel.getStudents()
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