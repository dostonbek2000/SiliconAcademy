package com.example.siliconacademy.fragments

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.core.content.FileProvider
import com.example.siliconacademy.databinding.FragmentAddTeacherBinding
import com.example.siliconacademy.models.TeacherViewModel
import java.io.File
import java.io.FileOutputStream

class AddTeacherFragment : Fragment() {

    private var selectedImageUri: Uri? = null
    private var selectedFilePath: String? = null
    private val viewModel: TeacherViewModel by viewModels()
    private lateinit var binding: FragmentAddTeacherBinding
    private val TAG = "AddTeacherFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTeacherBinding.inflate(layoutInflater, container, false)

        binding.downloadImage.setOnClickListener {
            pickImageFromGallery()
        }

        binding.downloadFile.setOnClickListener {
            pickFileFromStorage()
        }

        binding.add.setOnClickListener {
            val title = binding.courseTitle.text.toString().trim()
            val desc = binding.courseDesc.text.toString().trim()
            val age = binding.courseAge.text.toString().trim()
            val subject = binding.courseSubject.text.toString().trim()

            // Optional fields
            val toifa = binding.courseToifa.text.toString().trim().ifEmpty { "Mavjud emas" }
            val cerA = binding.courseCertificate.text.toString().trim().ifEmpty { "Mavjud emas" }
            val cerB = binding.courseCertificateB.text.toString().trim().ifEmpty { "Mavjud emas" }
            val imageUri = selectedImageUri?.toString() ?: "".ifEmpty { "Mavjud emas" }
            val filePath = selectedFilePath ?: "".ifEmpty { "Mavjud emas" }

            if (title.isNotEmpty() && desc.isNotEmpty() && age.isNotEmpty() && subject.isNotEmpty()) {
                viewModel.createTeacher(
                    title,
                    desc,
                    age,
                    subject,
                    toifa,
                    cerA,
                    imageUri,
                    cerB,
                    filePath
                )

                viewModel.getTeachers()
                findNavController().popBackStack()
            } else {
                if (title.isEmpty()) binding.courseTitle.error = "Ism sharif majburiy"
                if (desc.isEmpty()) binding.courseDesc.error = "Universitet majburiy"
                if (age.isEmpty()) binding.courseAge.error = "Yosh majburiy"
                if (subject.isEmpty()) binding.courseSubject.error = "Fan majburiy"
            }
        }

        return binding.root
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
        val mimeTypes = arrayOf("application/pdf")
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
                    val uri = data.data ?: return

                    try {
                        requireContext().contentResolver.takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: SecurityException) {}

                    val fileName = getFileName(uri)
                    if (!fileName.endsWith(".pdf", true)) {
                        Toast.makeText(requireContext(), "Iltimos, faqat PDF fayl tanlang", Toast.LENGTH_SHORT).show()
                        return
                    }

                    try {
                        val inputStream = requireContext().contentResolver.openInputStream(uri)
                        val destFile = File(requireContext().filesDir, fileName)
                        val outputStream = FileOutputStream(destFile)
                        inputStream?.copyTo(outputStream)
                        inputStream?.close()
                        outputStream.close()

                        selectedFilePath = destFile.absolutePath
                        binding.fileName.text = "📎 Fayl: $fileName"

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Faylni nusxalashda xatolik: ${e.message}", Toast.LENGTH_SHORT).show()
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
