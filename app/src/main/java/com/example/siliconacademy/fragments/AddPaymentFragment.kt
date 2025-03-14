package com.example.siliconacademy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.databinding.FragmentAddPaymentBinding
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.models.Payment

class AddPaymentFragment : Fragment() {
    private lateinit var binding: FragmentAddPaymentBinding
    private lateinit var codialDatabase: CodialDatabase
    private var studentId: Int? = null
    private var selectedMonth: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            studentId = it.getInt("studentId")
        }
        codialDatabase = CodialDatabase(requireContext()) // Initialize Database
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPaymentBinding.inflate(inflater, container, false)

        // Month Spinner setup
        val months = listOf(
            "Yanvar", "Fevral", "Mart", "Aprel", "May", "Iyun",
            "Iyul", "Avgust", "Sentabr", "Oktabr", "Noyabr", "Dekabr"
        )

        val monthAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            months
        )
        binding.monthSpinner.adapter = monthAdapter

        // Student full name list
        val studentList = codialDatabase.getAllStudentsList()
        val studentNames = ArrayList<String>()
        for (student in studentList) {
            val name = student.name ?: ""
            val surname = student.surname ?: ""
            studentNames.add("$name $surname".trim())
        }

        val nameAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            studentNames
        )
        binding.nameSpinner.adapter = nameAdapter

        // Spinner month selection listener
        binding.monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedMonth = months[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedMonth = ""
            }
        }


        binding.save.setOnClickListener {
            val selectedStudentIndex = binding.nameSpinner.selectedItemPosition

            if (selectedStudentIndex >= 0 && selectedStudentIndex < studentList.size) {
                val selectedStudent = studentList[selectedStudentIndex]
                val name = selectedStudent.name ?: ""
                val surname = selectedStudent.surname ?: ""
                val fullName = "$name $surname".trim()

                val amountText = binding.paymentAmount.text.toString().trim()

                if (amountText.isNotEmpty() && selectedMonth.isNotEmpty()) {
                    val amount = amountText.toDoubleOrNull()
                    if (amount != null) {
                        val payment = Payment(
                            id = 0,
                            fullName = fullName,
                            amount = amount.toString(),
                            month = selectedMonth
                        )

                        codialDatabase.addPayment(payment)
                        val student = selectedStudent
                        student.paymentStatus = "To'lov qilingan"
                        codialDatabase.editStudent(student)
                        Toast.makeText(requireContext(), "To'lov saqlandi!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        binding.paymentAmount.error = "To'g'ri miqdorni kiriting"
                    }
                } else {
                    if (amountText.isEmpty()) {
                        binding.paymentAmount.error = "To'lov miqdorini kiriting"
                    }
                    if (selectedMonth.isEmpty()) {
                        Toast.makeText(requireContext(), "Iltimos, oy tanlang!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Iltimos, talabani tanlang!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}
