package com.example.siliconacademy.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.siliconacademy.fragments.PaymentFragment

class MonthViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val months = listOf("Yanvar", "Fevral", "Mart", "Aprel", "May", "Iyun",
        "Iyul", "Avgust", "Sentabr", "Oktabr", "Noyabr", "Dekabr")

    private val fragments = months.map { month ->
        PaymentFragment().apply {
            arguments = androidx.core.os.bundleOf("month" to month)
        }
    }

    override fun getItemCount(): Int = months.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun getFragmentAtPosition(position: Int): PaymentFragment? {
        return if (position in fragments.indices) fragments[position] as PaymentFragment else null
    }

    fun getMonthName(position: Int): String {
        return if (position in months.indices) months[position] else ""
    }
}