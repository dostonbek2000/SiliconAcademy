package com.example.siliconacademy.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.R
import com.example.siliconacademy.adapters.PaymentRvAdapter
import com.example.siliconacademy.databinding.FragmentPaymentBinding
import com.example.siliconacademy.databinding.FragmentAddPaymentBinding
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.models.Payment
import java.text.SimpleDateFormat
import java.util.*

class PaymentFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding
    private lateinit var codialDatabase: CodialDatabase
    private lateinit var paymentList: ArrayList<Payment>
    private lateinit var adapter: PaymentRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        codialDatabase = CodialDatabase(requireContext())

        paymentList = codialDatabase.getAllPaymentList()

        adapter = PaymentRvAdapter(
            object : PaymentRvAdapter.OnItemClick {
                override fun onItemEditClick(payment: Payment, position: Int) {
                    showEditPaymentDialog(payment, position)
                }

                override fun onItemClick(payment: Payment, position: Int) {
                    findNavController().navigate(R.id.paymentInfoFragment, bundleOf("payment" to payment))
                }

                override fun onItemDeleteClick(payment: Payment, position: Int) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Eslatma")
                        .setMessage("Rostan ham ushbu toʻlovni oʻchirmoqchimisiz?")
                        .setPositiveButton("Ha") { _, _ ->
                            codialDatabase.deletePayment(payment)
                            paymentList.removeAt(position)
                            adapter.notifyItemRemoved(position)
                            adapter.notifyItemRangeChanged(position, paymentList.size)
                            Toast.makeText(requireContext(), "Oʻchirildi", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Yoʻq", null)
                        .show()
                }
            },
            paymentList
        )

        binding.recyclerView.adapter = adapter

        binding.toolBar.setOnMenuItemClickListener{
            findNavController().navigate(R.id.addPaymentFragment)
            true
        }

        return binding.root
    }

    private fun showEditPaymentDialog(payment: Payment, position: Int) {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val dialogBinding = FragmentAddPaymentBinding.inflate(layoutInflater)
        dialog.setView(dialogBinding.root)

        val months = listOf(
            "Yanvar", "Fevral", "Mart", "Aprel", "May", "Iyun",
            "Iyul", "Avgust", "Sentabr", "Oktabr", "Noyabr", "Dekabr"
        )

        val students = codialDatabase.getAllStudentsList()
        val fullNameList = students.map { "${it.name} ${it.surname}".trim() }

        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
        val nameAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, fullNameList)

        dialogBinding.monthSpinner.adapter = monthAdapter
        dialogBinding.nameSpinner.adapter = nameAdapter

        dialogBinding.paymentAmount.setText(payment.amount)
        dialogBinding.monthSpinner.setSelection(months.indexOf(payment.month))
        dialogBinding.nameSpinner.setSelection(fullNameList.indexOf(payment.fullName))

        dialogBinding.save.setOnClickListener {
            val updatedAmount = dialogBinding.paymentAmount.text.toString().trim()
            val updatedMonth = dialogBinding.monthSpinner.selectedItem.toString()
            val updatedName = dialogBinding.nameSpinner.selectedItem.toString()

            if (updatedAmount.isNotEmpty()) {
                payment.amount = updatedAmount
                payment.month = updatedMonth
                payment.fullName = updatedName
                payment.date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                codialDatabase.editPayment(payment)

                // Update student's balance manually (optional: advanced logic could adjust previous balance delta here)
                // For simplicity, skip balance correction in edit mode.

                adapter.notifyItemChanged(position)
                dialog.dismiss()
            } else {
                dialogBinding.paymentAmount.error = "Toʻlov miqdorini kiriting"
            }
        }

        dialog.show()
    }
}
