package com.example.siliconacademy.fragments

import Group
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.adapters.GroupRvAdapter
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.R
import com.example.siliconacademy.adapters.ResultRvAdapter
import com.example.siliconacademy.databinding.EditTeacherBinding
import com.example.siliconacademy.databinding.FragmentGroupsBinding
import com.example.siliconacademy.models.Results
import com.example.siliconacademy.utils.Object.courseId

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ResultFragment : Fragment() {

    private val TAG = "ResultFragment"

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentGroupsBinding
    private lateinit var codialDatabase: CodialDatabase
    private lateinit var adapter: ResultRvAdapter
    private lateinit var resultsList:ArrayList<Results>
    private lateinit var resultList:ArrayList<Results>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        codialDatabase=CodialDatabase(requireContext())

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupsBinding.inflate(layoutInflater, container, false)
        resultsList=codialDatabase.getAllResultsList()
        resultList=ArrayList()
        resultList.clear()
        if (param1 == "0")
            for (i in resultsList.indices) {
                if (resultsList[i].resultPosition == 0) {
                    resultList.add(resultsList[i])
                }
            }
        else if (param1=="1") {
            for (i in resultsList.indices) {
                if (resultsList[i].resultPosition == 1) {
                    resultList.add(resultsList[i])
                }
            }
        }else
            for (i in resultsList.indices){
                if (resultsList[i].resultPosition==2){
                    resultList.add(resultsList[i])
                }
            }




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}