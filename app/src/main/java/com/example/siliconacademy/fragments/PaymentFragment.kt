package com.example.siliconacademy.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.siliconacademy.R
import com.example.siliconacademy.adapters.PaymentRvAdapter
import com.example.siliconacademy.databinding.FragmentPaymentBinding
import com.example.siliconacademy.models.Payment
import com.example.siliconacademy.models.PaymentViewModel

class PaymentFragment : Fragment() {
    private var isUiReady = false
    private lateinit var binding: FragmentPaymentBinding
    private lateinit var adapter: PaymentRvAdapter
    private val fullList = ArrayList<Payment>()
    private val filteredList = ArrayList<Payment>()
    private val paymentViewModel: PaymentViewModel by viewModels()
    private var currentMonth: String? = null
    private var currentSearchQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentMonth = it.getString("month")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)


        setupRecyclerView()
        observeViewModel()
        paymentViewModel.getPayments()
        isUiReady = true
        return binding.root
    }

    // Public function to be called from PaymentHomeFragment
    fun filterPayments(query: String?) {
        currentSearchQuery = query
        applyFilters()
    }

    private fun applyFilters() {
        if (!isUiReady || !::binding.isInitialized) return
        filteredList.clear()

        val monthFiltered = if (currentMonth != null) {
            fullList.filter { it.month == currentMonth }
        } else {
            fullList
        }

        if (currentSearchQuery.isNullOrEmpty()) {
            filteredList.addAll(monthFiltered)
        } else {
            val searchText = currentSearchQuery!!.lowercase()
            for (payment in monthFiltered) {
                if ((payment.fullName?.lowercase()?.contains(searchText) == true) ||
                    payment.month.lowercase().contains(searchText) ||
                    (payment.teacher?.lowercase()?.contains(searchText) == true) ||
                    (payment.group?.lowercase()?.contains(searchText) == true)
                ) {
                    filteredList.add(payment)
                }
            }
        }

        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }

        // ✅ Only touch binding if it’s initialized
        if (::binding.isInitialized) {
            if (filteredList.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
    }



    private fun setupRecyclerView() {
        adapter = PaymentRvAdapter(object : PaymentRvAdapter.OnItemClick {
            override fun onItemEditClick(payment: Payment, position: Int) {
                // Handle edit if needed
            }

            override fun onItemClick(payment: Payment, position: Int) {
                findNavController().navigate(
                    R.id.paymentInfoFragment,
                    bundleOf("payment" to filteredList[position])
                )
            }

            override fun onItemDeleteClick(payment: Payment, position: Int) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Eslatma")
                    .setMessage("Rostan ham ushbu toʻlovni oʻchirmoqchimisiz?")
                    .setPositiveButton("Ha") { _, _ ->
                        payment.id?.let { paymentViewModel.deletePayment(it) }
                    }
                    .setNegativeButton("Yoʻq", null)
                    .show()
            }
        }, filteredList)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        paymentViewModel.payments.observe(viewLifecycleOwner) { payments ->
            fullList.clear()
            fullList.addAll(payments.reversed())
            applyFilters()
            Log.d("PaymentFragment", "Month: $currentMonth, Payments loaded: ${fullList.size}, filtered: ${filteredList.size}")
        }

        paymentViewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        paymentViewModel.message.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}