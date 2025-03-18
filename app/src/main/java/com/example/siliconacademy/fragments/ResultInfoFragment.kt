package com.example.siliconacademy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentResultInfoBinding
import com.example.siliconacademy.models.Results

class ResultInfoFragment : Fragment() {

    private lateinit var binding: FragmentResultInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultInfoBinding.inflate(inflater, container, false)
        val result = arguments?.getSerializable("result") as? Results

        result?.let {
            binding.name.text = "\uD83D\uDC64 Ismi: ${it.name}"
            binding.subject.text = "🎂 Fani: ${it.subject}"
            binding.age.text = "\uD83D\uDCC4 Yoshi: ${it.age}"
            binding.teacher.text = "\uD83D\uDC68\u200D\uD83C\uDFEB Ustoz: ${it.teacherName}"
            binding.type.text = "\uD83D\uDCDA Test turi: ${it.testType}"

            Glide.with(requireContext()).load(it.imageUri).into(binding.image) // Load Image
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    companion object {
        fun newInstance(result: Results): ResultInfoFragment {
            val fragment = ResultInfoFragment()
            val bundle = Bundle()
            bundle.putSerializable("result", result)
            fragment.arguments = bundle
            return fragment
        }
    }
}