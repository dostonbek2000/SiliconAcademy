package com.example.siliconacademy.fragments

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.siliconacademy.R
import com.example.siliconacademy.adapters.ResultRvAdapter
import com.example.siliconacademy.databinding.FragmentAddResultBinding
import com.example.siliconacademy.databinding.FragmentResultBinding
import com.example.siliconacademy.models.ResultViewModel
import com.example.siliconacademy.models.Results

class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding
    private lateinit var adapter: ResultRvAdapter
    private val resultList = ArrayList<Results>()
    private val resultViewModel: ResultViewModel by viewModels()

    private var param1: String? = null
    private var param2: String? = null
    private var isUiReady = false

    private var imagePickCallback: ((Uri) -> Unit)? = null
    private var filePickCallback: ((Uri) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentResultBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeResults()

        isUiReady = true
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = ResultRvAdapter(resultList, object : ResultRvAdapter.OnItemClick {
            override fun onItemClick(results: Results, position: Int) {
                val bundle = Bundle().apply {
                    putSerializable("result", results)
                }
                findNavController().navigate(R.id.resultInfoFragment, bundle)
            }

            override fun onItemEditClick(results: Results, position: Int) {
                showEditDialog(results)
            }

            override fun onItemDeleteClick(results: Results, position: Int) {
                showDeleteConfirmation(results)
            }
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun showEditDialog(results: Results) {
        val dialogBinding = FragmentAddResultBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setView(dialogBinding.root)

        dialogBinding.name.setText(results.name)
        dialogBinding.subject.setText(results.subject)
        dialogBinding.age.setText(results.age)
        dialogBinding.teacherName.setText(results.teacherName)

        val testTypes = arrayOf("DTM", "IELTS", "CEFR")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, testTypes)
        dialogBinding.type.adapter = spinnerAdapter
        dialogBinding.type.setSelection(results.resultPosition ?: 0)

        Glide.with(requireContext()).load(Uri.parse(results.imageUri)).into(dialogBinding.image)
        dialogBinding.fileName.text = getFileNameFromUri(results.fileUri)

        var updatedImageUri = Uri.parse(results.imageUri)
        var updatedFileUri = Uri.parse(results.fileUri)

        dialogBinding.downloadImage.setOnClickListener {
            pickImageFromGallery {
                updatedImageUri = it
                dialogBinding.image.setImageURI(it)
            }
        }

        dialogBinding.downloadFile.setOnClickListener {
            pickFileFromStorage {
                updatedFileUri = it
                dialogBinding.fileName.text = getFileName(it)
            }
        }

        dialogBinding.save.setOnClickListener {
            val name = dialogBinding.name.text.toString().trim()
            val subject = dialogBinding.subject.text.toString().trim()
            val age = dialogBinding.age.text.toString().trim()
            val teacherName = dialogBinding.teacherName.text.toString().trim()
            val testType = dialogBinding.type.selectedItem.toString()

            if (name.isEmpty() || subject.isEmpty() || age.isEmpty() || teacherName.isEmpty()) {
                Toast.makeText(requireContext(), "Barcha maydonlarni to'ldiring!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newPos = when (testType) {
                "DTM" -> 0
                "IELTS" -> 1
                "CEFR" -> 2
                else -> -1
            }

            if (newPos == -1) {
                Toast.makeText(requireContext(), "Test turi noto‘g‘ri!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            results.apply {
                this.name = name
                this.subject = subject
                this.age = age
                this.teacherName = teacherName
                this.testType = testType
                this.resultPosition = newPos
                this.imageUri = updatedImageUri.toString()
                this.fileUri = updatedFileUri.toString()
            }

            resultViewModel.updateResult(results)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun observeResults() {
        resultViewModel.resultsLiveData.observe(viewLifecycleOwner) { allResults ->
            resultList.clear()
            val filtered = when (param1) {
                "0" -> allResults.filter { it.resultPosition == 0 }
                "1" -> allResults.filter { it.resultPosition == 1 }
                else -> allResults.filter { it.resultPosition == 2 }
            }
            resultList.addAll(filtered.reversed()) // Show latest results at top
            adapter.notifyDataSetChanged()
        }

        resultViewModel.fetchResults()

        resultViewModel.loading.observe(viewLifecycleOwner) {
            if (isUiReady) {
                binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        resultViewModel.statusMessage.observe(viewLifecycleOwner) {
            it?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun showDeleteConfirmation(results: Results) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eslatma!")
            .setMessage("Rostan ham o'chirmoqchimisiz?")
            .setPositiveButton("Ha") { _, _ ->
                results.id?.let { resultViewModel.deleteResult(it) }
            }
            .setNegativeButton("Yo‘q", null)
            .show()
    }

    private fun pickImageFromGallery(callback: (Uri) -> Unit) {
        imagePickCallback = callback
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun pickFileFromStorage(callback: (Uri) -> Unit) {
        filePickCallback = callback
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            ))
        }
        startActivityForResult(intent, FILE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data
            when (requestCode) {
                IMAGE_PICK_CODE -> uri?.let { imagePickCallback?.invoke(it) }
                FILE_PICK_CODE -> uri?.let {
                    try {
                        requireContext().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                    filePickCallback?.invoke(it)
                }
            }
        }
    }

    private fun getFileNameFromUri(uriString: String?): String {
        return if (!uriString.isNullOrEmpty()) {
            try {
                getFileName(Uri.parse(uriString))
            } catch (e: Exception) {
                uriString.substringAfterLast("/")
            }
        } else "\uD83D\uDCCE Fayl mavjud emas"
    }

    private fun getFileName(uri: Uri): String {
        var name = ""
        val cursor: Cursor? = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex >= 0) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1001
        private const val FILE_PICK_CODE = 1002

        fun newInstance(param1: String, param2: String) =
            ResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"