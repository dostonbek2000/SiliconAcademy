package com.example.siliconacademy.fragments

import Group
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.R
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.adapters.ResultRvAdapter
import com.example.siliconacademy.databinding.EditTeacherBinding
import com.example.siliconacademy.databinding.FragmentAddResultBinding
import com.example.siliconacademy.databinding.FragmentGroupsBinding
import com.example.siliconacademy.databinding.FragmentResultBinding
import com.example.siliconacademy.models.Results

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ResultFragment : Fragment() {
    private var imageUri: Uri? = null
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

                val bundle = Bundle()
                bundle.putSerializable("result", results)
                findNavController().navigate(R.id.resultInfoFragment, bundle)
            }

            override fun onItemEditClick(results: Results, position: Int) {
                val alertDialog = AlertDialog.Builder(requireContext()).create()
                val dialogBinding = FragmentAddResultBinding.inflate(LayoutInflater.from(requireContext()), null, false)
                alertDialog.setView(dialogBinding.root)

                // Set old values into input fields
                dialogBinding.name.setText(results.name)
                dialogBinding.subject.setText(results.subject)
                dialogBinding.age.setText(results.age)
                dialogBinding.teacherName.setText(results.teacherName)

                // Setup spinner values
                val types = listOf("DTM", "IELTS", "CEFR")
                val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, types)
                dialogBinding.type.adapter = spinnerAdapter
                results.resultPosition?.let { dialogBinding.type.setSelection(it) }

                // Save button click
                dialogBinding.save.setOnClickListener {
                    val name = dialogBinding.name.text.toString().trim()
                    val subject = dialogBinding.subject.text.toString().trim()
                    val age = dialogBinding.age.text.toString().trim()
                    val teacherName = dialogBinding.teacherName.text.toString().trim()
                    val testType = dialogBinding.type.selectedItem?.toString() ?: ""

                    if (name.isEmpty() || subject.isEmpty() || age.isEmpty() || teacherName.isEmpty() || testType.isEmpty()) {
                        Toast.makeText(requireContext(), "Barcha maydonlarni to'ldiring!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val newPosition = when (testType) {
                        "DTM" -> 0
                        "IELTS" -> 1
                        "CEFR" -> 2
                        else -> -1
                    }

                    if (newPosition == -1) {
                        Toast.makeText(requireContext(), "Test turi noto‘g‘ri!", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    // Update the result object
                    results.name = name
                    results.subject = subject
                    results.age = age
                    results.teacherName = teacherName
                    results.testType = testType
                    results.resultPosition = newPosition

                    codialDatabase.editResult(results)

                    adapter.notifyItemChanged(position)

                    alertDialog.dismiss()
                }

                alertDialog.show()
            }


            override fun onItemDeleteClick(results: Results, position: Int) {
                val alertDialog = AlertDialog.Builder(requireContext())

                alertDialog.setTitle("Eslatma!")
                alertDialog.setMessage("Rostan ham o'chirmoqchimisiz?")
                alertDialog.setPositiveButton("Ha") { _, _ ->
                    codialDatabase.deleteResult(results)
                    resultList.remove(results)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, resultList.size)
                    alertDialog.create().dismiss()
                }
                alertDialog.setNegativeButton("Yo'q") { _, _ ->
                    alertDialog.create().dismiss()
                }

                alertDialog.show()
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