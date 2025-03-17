package com.example.siliconacademy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentPaymentInfoBinding
import com.example.siliconacademy.models.Payment

class PaymentInfoFragment : Fragment() {
private lateinit var binding:FragmentPaymentInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPaymentInfoBinding.inflate(layoutInflater,container,false)
        val payment=arguments?.getSerializable("payment" ) as Payment

        payment?.let {
            binding.fullName.text=it.fullName.toString()
            binding.amount.text=it.amount
            binding.month.text=it.month
            binding.date.text=it.date

        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

}