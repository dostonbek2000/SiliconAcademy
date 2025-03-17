package com.example.siliconacademy.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.siliconacademy.fragments.ResultFragment

class ResultVpAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle, private val itemCount: Int
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = itemCount

    override fun createFragment(position: Int): Fragment {
        return ResultFragment.newInstance("$position", "")
    }

}
