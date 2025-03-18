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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.databinding.FragmentAddResultBinding
import com.example.siliconacademy.models.ResultViewModel
import com.example.siliconacademy.models.Results

class AddResultFragment : Fragment() {

    private lateinit var binding: FragmentAddResultBinding
    //private lateinit var codialDatabase: CodialDatabase
    private var selectedImageUri: Uri? = null
    private var selectedFileUri: Uri? = null
    private val resultViewModel: ResultViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddResultBinding.inflate(inflater, container, false)
        //codialDatabase = CodialDatabase(requireContext())

        val testTypes = arrayOf("DTM", "IELTS", "CEFR")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, testTypes)
        binding.type.adapter = spinnerAdapter

        binding.downloadImage.setOnClickListener {
            pickImageFromGallery()
        }

        binding.downloadFile.setOnClickListener {
            pickFileFromStorage()
        }

        binding.save.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val subject = binding.subject.text.toString().trim()
            val age = binding.age.text.toString().trim()
            val teacherName = binding.teacherName.text.toString().trim()
            val testType = binding.type.selectedItem?.toString() ?: ""

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

            val result = Results(
                name = name,
                subject = subject,
                age = age,
                teacherName = teacherName,
                testType = testType,
                resultPosition = newPosition,
                imageUri = selectedImageUri?.toString(),
                fileUri = selectedFileUri?.toString()
            )

            resultViewModel.createResult(result)
            binding.progressBar.visibility = View.VISIBLE
        }

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {
        resultViewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })

        resultViewModel.success.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Saqlandi!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        })

        resultViewModel.statusMessage.observe(viewLifecycleOwner, Observer { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun pickFileFromStorage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        val mimeTypes = arrayOf(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, FILE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                IMAGE_PICK_CODE -> {
                    selectedImageUri = data.data
                    binding.image.setImageURI(selectedImageUri)
                }
                FILE_PICK_CODE -> {
                    selectedFileUri = data.data
                    selectedFileUri?.let {
                        try {
                            requireContext().contentResolver.takePersistableUriPermission(
                                it,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                        }
                        val fileName = getFileName(it)
                        binding.fileName.text = "📎 Fayl: $fileName"
                    }
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = ""
        val cursor: Cursor? = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
        return name
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1001
        private const val FILE_PICK_CODE = 1002
    }
}
