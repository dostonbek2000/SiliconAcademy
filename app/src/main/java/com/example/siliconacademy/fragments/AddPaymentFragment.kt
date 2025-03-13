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
        val months = listOf(
            "Yanvar", "Fevral", "Mart", "Aprel", "May", "Iyun",
            "Iyul", "Avgust", "Sentabr", "Oktabr", "Noyabr", "Dekabr"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
        binding.monthSpinner.adapter = adapter

        // Handle Spinner selection
        binding.monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedMonth = months[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedMonth = "" // Default empty
            }
        }

        binding.save.setOnClickListener {
            val amountText = binding.paymentAmount.text.toString().trim()
            val fullName:String=binding.name.text.toString().trim()


            if (amountText.isNotEmpty() && selectedMonth.isNotEmpty()) {
                val amount = amountText.toDoubleOrNull()
                if (amount != null) {
                    val payment = Payment(
                        id = 0, // Auto-increment
                        fullName = fullName,
                        amount = amount.toString(),
                        month = selectedMonth
                    )

                    codialDatabase.addPayment(payment) // Save payment
                    adapter.notifyDataSetChanged()

                    Toast.makeText(requireContext(), "To'lov saqlandi!", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } else {
                    binding.paymentAmount.error = "Invalid amount"
                }
            } else {
                binding.paymentAmount.error = "Required"
                Toast.makeText(requireContext(), "Iltimos, oy tanlang!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}

