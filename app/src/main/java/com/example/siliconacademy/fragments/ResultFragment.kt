package com.example.siliconacademy.fragments

import Group
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.adapters.ResultRvAdapter
import com.example.siliconacademy.databinding.FragmentGroupsBinding
import com.example.siliconacademy.databinding.FragmentResultBinding
import com.example.siliconacademy.models.Results

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ResultFragment : Fragment() {

    private val TAG = "ResultFragment"

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentResultBinding
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
        binding = FragmentResultBinding.inflate(layoutInflater, container, false)
        resultsList = codialDatabase.getAllResultsList()
        resultList = ArrayList()

        resultList.clear()
        if (param1 == "0") {
            resultList.addAll(resultsList.filter { it.resultPosition == 0 })
        } else if (param1 == "1") {
            resultList.addAll(resultsList.filter { it.resultPosition == 1 })
        } else if (param1 == "2") {
            resultList.addAll(resultsList.filter { it.resultPosition == 2 })
        }

        Log.d("ResultFragment", "Loaded results: ${resultList.size}")

        adapter = ResultRvAdapter(resultList, object : ResultRvAdapter.OnItemClick {
            override fun onItemClick(results: Results, position: Int) {
                // Handle click if needed
            }

            override fun onItemEditClick(results: Results, position: Int) {
                // Handle edit if needed
            }

            override fun onItemDeleteClick(results: Results, position: Int) {

            }
        })

        binding.recyclerView.adapter = adapter

        return binding.root
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