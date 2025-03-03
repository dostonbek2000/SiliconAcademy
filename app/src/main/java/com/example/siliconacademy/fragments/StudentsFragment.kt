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
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.adapters.StudentRvAdapter
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentAddStudentBinding
import com.example.siliconacademy.databinding.FragmentPaymentBinding
import com.example.siliconacademy.databinding.FragmentStudentsBinding
import com.example.siliconacademy.models.Payment

class StudentsFragment : Fragment() {

    private lateinit var binding: FragmentStudentsBinding
    private lateinit var group: Group
    private var groupId: Int? = null
    private var groupTitle: String? = null
    private var groupTime: String? = null

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
        for (i in 0 until studentsList.size) {
            if (studentsList[i].groupId?.id == groupId) {
                studentList.add(studentsList[i])
            }
        }

        binding.groupTitle.text = groupTitle
        binding.groupDesc.text = "O'quvchilar soni: ${studentList.size}"
        binding.groupTime.text = groupTime

        adapter = StudentRvAdapter(
            object : StudentRvAdapter.OnItemClick {
                override fun onItemClick(student: Student, position: Int) {

                }

                override fun onItemEditClick(student: Student, position: Int) {
                    val alertDialog = AlertDialog.Builder(requireContext()).create()
                    val addStudent =
                        FragmentAddStudentBinding.inflate(requireActivity().layoutInflater)
                    alertDialog.setView(addStudent.root)

                    // Set existing student details
                    addStudent.name.setText(student.name)
                    addStudent.surname.setText(student.surname)
                    addStudent.fatherName.setText(student.fatherName)

                    addStudent.save.setOnClickListener {
                        val name: String = addStudent.name.text.toString().trim()
                        val surname: String = addStudent.surname.text.toString().trim()
                        val fatherName: String = addStudent.fatherName.text.toString().trim()

                        if (name.isNotEmpty() && surname.isNotEmpty() && fatherName.isNotEmpty()) {
                            student.name = name
                            student.surname = surname
                            student.fatherName = fatherName

                            codialDatabase.editStudent(student) // Update student in the database

                            adapter.notifyItemChanged(position) // Refresh only the changed item
                            alertDialog.dismiss()
                        } else {
                            addStudent.name.error = "Required"
                            addStudent.surname.error = "Required"
                            addStudent.fatherName.error = "Required"
                        }
                    }

                    alertDialog.show()
                }


                @SuppressLint("NotifyDataSetChanged")
                override fun onItemDeleteClick(student: Student, position: Int) {
                    val alertDialog = AlertDialog.Builder(requireContext())

                    alertDialog.setTitle("Eslatma!")
                    alertDialog.setMessage("Rostan ham o'chirmoqchimisiz?")
                    alertDialog.setPositiveButton("Ha") { _, _ ->

                        codialDatabase.deleteStudent(student) // Remove from DB
                        studentsList.remove(student) // Remove from the list
                        adapter.notifyItemRemoved(position) // Notify the adapter
                        adapter.notifyDataSetChanged()
                        adapter.notifyItemRangeRemoved(position, studentList.size)
                        adapter.notifyItemRangeChanged(
                            position,
                            studentList.size
                        ) // Update positions
                        alertDialog.create().dismiss()
                    }

                    alertDialog.setNegativeButton("Yo'q") { _, _ ->
                        alertDialog.create().dismiss()
                    }

                    alertDialog.show()
                }
                override fun onItemPayClick(student: Student, position: Int) {
                    val alertDialog = AlertDialog.Builder(requireContext()).create()
                    val paymentBinding = FragmentPaymentBinding.inflate(requireActivity().layoutInflater)
                    alertDialog.setView(paymentBinding.root)

                    // Set student details
                    paymentBinding.name.setText(student.name)
                    paymentBinding.surname.setText(student.surname)

                    // List of months
                    val months = listOf(
                        "January", "February", "March", "April", "May", "June",
                        "July", "August", "September", "October", "November", "December"
                    )

                    // Spinner Adapter (renamed to avoid conflicts)
                    val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    paymentBinding.monthSpinner.adapter = spinnerAdapter

                    paymentBinding.save.setOnClickListener {
                        val amountText = paymentBinding.paymentAmount.text.toString().trim()
                        val selectedMonth = paymentBinding.monthSpinner.selectedItem.toString()

                        if (amountText.isNotEmpty()) {
                            val amount = amountText.toDoubleOrNull()
                            if (amount != null && amount > 0) {
                                val payment = Payment(
                                    studentId = student.id!!,
                                    amount = amount.toString(),  // Ensure proper conversion
                                    month = selectedMonth
                                )

                                codialDatabase.addPayment(payment) // Save payment to database

                                // Update UI correctly
                                val updatedPayments = codialDatabase.getPaymentsByStudentId(student.id!!)
                                if (updatedPayments.isNotEmpty()) {
                                    val latestPayment = updatedPayments.last()
                                    student.latestPaymentAmount = latestPayment.amount
                                    student.latestPaymentMonth = latestPayment.month
                                }

                                adapter.notifyItemChanged(position) // Refresh RecyclerView item
                                alertDialog.dismiss()
                            } else {
                                paymentBinding.paymentAmount.error = "Invalid amount"
                            }
                        } else {
                            paymentBinding.paymentAmount.error = "Required"
                        }
                    }

                    alertDialog.show()
                }

                override fun onItemAttendance(student: Student, position: Int) {
                    TODO("Not yet implemented")
                }


            }, studentList,
            codialDatabase=codialDatabase
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
}