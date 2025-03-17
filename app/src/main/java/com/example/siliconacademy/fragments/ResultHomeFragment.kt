package com.example.siliconacademy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.siliconacademy.R
import com.example.siliconacademy.adapters.ResultVpAdapter
import com.example.siliconacademy.adapters.VpAdapter
import com.example.siliconacademy.databinding.FragmentResultBinding
import com.example.siliconacademy.databinding.FragmentResultHomeBinding
import com.example.siliconacademy.models.Results
import com.google.android.material.tabs.TabLayoutMediator


class ResultHomeFragment : Fragment() {
 private lateinit var binding:FragmentResultHomeBinding
 private lateinit var adapter: ResultVpAdapter
 private lateinit var results: Results

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentResultHomeBinding.inflate(layoutInflater,container,false)
        binding.add.setOnClickListener {
            findNavController().navigate(R.id.addResultFragment)
        }

        adapter = ResultVpAdapter(childFragmentManager, lifecycle,3)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

            }
        })

        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab, position -> // Styling each tab here
            if (position == 0) {
                tab.text = "DTM"
            } else if (position==1) {
                tab.text = "IELTS"
            } else{
                tab.text="CEFR"
            }
        }.attach()
        return binding.root
    }


}