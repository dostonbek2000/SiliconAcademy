package com.example.siliconacademy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.R
import com.example.siliconacademy.adapters.MonthViewPagerAdapter
import com.example.siliconacademy.databinding.FragmentPaymentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.util.Calendar

class PaymentHomeFragment : Fragment() {
    private lateinit var binding: FragmentPaymentHomeBinding
    private lateinit var adapter: MonthViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaymentHomeBinding.inflate(layoutInflater, container, false)

        setupViewPager()
        setupSearchView()
        setupToolbar()

        return binding.root
    }

    private fun setupViewPager() {
        adapter = MonthViewPagerAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = adapter.getMonthName(position)
        }.attach()

        // Optionally, set current month as default tab
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        binding.viewPager.setCurrentItem(currentMonth, false)
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val currentPosition = binding.viewPager.currentItem
                adapter.getFragmentAtPosition(currentPosition)?.filterPayments(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val currentPosition = binding.viewPager.currentItem
                adapter.getFragmentAtPosition(currentPosition)?.filterPayments(newText)
                return true
            }
        })
    }

    private fun setupToolbar() {
        binding.toolBar.setOnClickListener {
            findNavController().navigate(R.id.addPaymentFragment)
        }
    }
}