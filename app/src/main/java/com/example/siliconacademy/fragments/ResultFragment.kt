package com.example.siliconacademy.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentResultBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class ResultFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
private lateinit var binding: FragmentResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentResultBinding.inflate(layoutInflater,container,false)
binding.add.setOnClickListener {
    findNavController().navigate(R.id.addResultFragment)
}

        return binding.root
    }
  companion object{
      fun newInstance(param1:String,param2:String){
          ResultFragment().apply {
              arguments=Bundle().apply {
                  putString(ARG_PARAM1,param1)
                  putString(ARG_PARAM2,param2)
              }
          }
      }
  }


}