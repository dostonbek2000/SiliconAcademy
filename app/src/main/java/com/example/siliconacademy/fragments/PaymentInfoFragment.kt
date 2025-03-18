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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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
            binding.date.text=formatDateToReadable(it.date.toString())
            binding.teacher.text = it.teacher ?: "-"
            binding.group.text = it.group ?: "—"

        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }
    fun formatDateToReadable(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val date = parser.parse(dateString)

            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formatter.format(date!!)
        } catch (e: Exception) {
            dateString // fallback to original if something goes wrong
        }
    }

}