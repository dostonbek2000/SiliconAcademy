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
import java.text.SimpleDateFormat
import java.util.*

class AddPaymentFragment : Fragment() {

    private lateinit var binding: FragmentAddPaymentBinding
    private lateinit var codialDatabase: CodialDatabase
    private var selectedMonth: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codialDatabase = CodialDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPaymentBinding.inflate(inflater, container, false)

        val months = listOf(
            "Yanvar", "Fevral", "Mart", "Aprel", "May", "Iyun",
            "Iyul", "Avgust", "Sentabr", "Oktabr", "Noyabr", "Dekabr"
        )

        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
        binding.monthSpinner.adapter = monthAdapter

        val studentList = codialDatabase.getAllStudentsList()
        val studentNames = studentList.map { "${it.name} ${it.surname}".trim() }

        val nameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, studentNames)
        binding.nameSpinner.adapter = nameAdapter

        binding.monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedMonth = months[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedMonth = ""
            }
        }

        binding.save.setOnClickListener {
            val selectedStudentIndex = binding.nameSpinner.selectedItemPosition
            val amountText = binding.paymentAmount.text.toString().trim()

            if (selectedStudentIndex in studentList.indices && amountText.isNotEmpty() && selectedMonth.isNotEmpty()) {
                val selectedStudent = studentList[selectedStudentIndex]
                val fullName = "${selectedStudent.name} ${selectedStudent.surname}".trim()
                val amount = amountText.toDoubleOrNull()

                if (amount != null && amount > 0) {
                    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    val payment = Payment(
                        id = 0,
                        fullName = fullName,
                        amount = amount.toString(),
                        month = selectedMonth,
                        date = currentDate
                    )
                    codialDatabase.addPayment(payment)
                    Toast.makeText(requireContext(), "Toʻlov muvaffaqiyatli qoʻshildi!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    binding.paymentAmount.error = "Yaroqli miqdor kiriting"
                }
            } else {
                if (amountText.isEmpty()) binding.paymentAmount.error = "Toʻlov miqdorini kiriting"
                if (selectedMonth.isEmpty()) Toast.makeText(requireContext(), "Oy tanlang", Toast.LENGTH_SHORT).show()
                if (selectedStudentIndex !in studentList.indices) Toast.makeText(requireContext(), "Talabani tanlang", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}
