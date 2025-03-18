package com.example.siliconacademy.fragments

import Group
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.databinding.FragmentAddPaymentBinding
import com.example.siliconacademy.models.GroupViewModel
import com.example.siliconacademy.models.Payment
import com.example.siliconacademy.models.PaymentViewModel
import com.example.siliconacademy.models.StudentViewModel
import com.example.siliconacademy.models.Teacher
import com.example.siliconacademy.models.TeacherViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddPaymentFragment : Fragment() {

    private lateinit var binding: FragmentAddPaymentBinding
    private val paymentViewModel: PaymentViewModel by viewModels()
    private val studentViewModel: StudentViewModel by viewModels()
    private val teacherViewModel: TeacherViewModel by viewModels()
    private val groupViewModel: GroupViewModel by viewModels()

    private var selectedTeacher: Teacher? = null
    private var selectedGroup: Group? = null

    private var selectedMonth: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPaymentBinding.inflate(inflater, container, false)

        setupMonthSpinner()

        setupTeacherSpinner()
        binding.save.setOnClickListener {
            onSaveClicked()
        }

        return binding.root
    }
    private fun loadGroupsForSelectedTeacher() {
        groupViewModel.getGroups()
        groupViewModel.groups.observe(viewLifecycleOwner) { allGroups ->
            val filteredGroups = allGroups.filter { it.courseId?.id == selectedTeacher?.id }

            val groupTitles = filteredGroups.map { it.groupTitle }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, groupTitles)
            binding.groupSpinner.adapter = adapter

            binding.groupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedGroup = if (filteredGroups.isNotEmpty()) filteredGroups[position] else null

                    // 💥 Show students for selected group
                    selectedGroup?.id?.let { groupId ->
                        observeStudentListForGroup(groupId)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedGroup = null
                }
            }
        }
    }

    private fun setupTeacherSpinner() {
        teacherViewModel.getTeachers()
        teacherViewModel.teachers.observe(viewLifecycleOwner) { teacherList ->
            val teacherNames = teacherList.map { it.title ?: "No name" }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, teacherNames)
            binding.teacherSpinner.adapter = adapter

            binding.teacherSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedTeacher = teacherList[position]
                    loadGroupsForSelectedTeacher()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedTeacher = null
                }
            }
        }
    }
    private fun observeStudentListForGroup(groupId: Int) {
        studentViewModel.getStudents()
        studentViewModel.students.observe(viewLifecycleOwner) { students ->
            val filteredStudents = students.filter { it.groupId?.id == groupId }
            val studentNames = filteredStudents.map {
                "${it.name.orEmpty()} ${it.surname.orEmpty()}"
            }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, studentNames)
            binding.nameAutoComplete.setAdapter(adapter)
            binding.nameAutoComplete.threshold = 1
        }
    }


    private fun setupMonthSpinner() {
        val months = listOf("Oy tanlang", "Yanvar", "Fevral", "Mart", "Aprel", "May", "Iyun",
            "Iyul", "Avgust", "Sentabr", "Oktabr", "Noyabr", "Dekabr")

        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
        binding.monthSpinner.adapter = monthAdapter

        binding.monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedMonth = if (position == 0) "" else months[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedMonth = ""
            }
        }
    }



    private fun onSaveClicked() {
        val studentName = binding.nameAutoComplete.text.toString().trim()
        val amountText = binding.paymentAmount.text.toString().trim()

        if (studentName.isEmpty()) {
            Toast.makeText(requireContext(), "Talabani tanlang", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedMonth.isEmpty()) {
            Toast.makeText(requireContext(), "Oy tanlang", Toast.LENGTH_SHORT).show()
            return
        }

        if (amountText.isEmpty()) {
            binding.paymentAmount.error = "Toʻlov miqdorini kiriting"
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.paymentAmount.error = "Yaroqli miqdor kiriting"
            return
        }

        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val payment = Payment(fullName = studentName, amount = amount.toString(), month = selectedMonth, date = date, teacher = selectedTeacher?.title ?: "",
            group = selectedGroup?.groupTitle ?: "")

        paymentViewModel.createPayment(payment)

        Toast.makeText(requireContext(), "Toʻlov muvaffaqiyatli qoʻshildi!", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }
}