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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.siliconacademy.databinding.FragmentEditTeacherBinding
import com.example.siliconacademy.models.Teacher
import com.example.siliconacademy.models.TeacherViewModel
import java.io.File
import java.io.FileOutputStream

class EditTeacherFragment : Fragment() {

    private lateinit var binding: FragmentEditTeacherBinding
    private val viewModel: TeacherViewModel by viewModels()
    private var selectedImageUri: Uri? = null
    private var selectedFilePath: String? = null
    private var currentTeacher: Teacher? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditTeacherBinding.inflate(inflater, container, false)

        currentTeacher = arguments?.getSerializable("teacher") as? Teacher

        currentTeacher?.let { teacher ->
            binding.courseTitle.setText(teacher.title)
            binding.courseDesc.setText(teacher.desc)
            binding.courseAge.setText(teacher.age)
            binding.courseSubject.setText(teacher.subject)
            binding.courseToifa.setText(teacher.toifa)
            binding.courseCertificate.setText(teacher.cerA)
            binding.courseCertificateB.setText(teacher.cerB)

            if (!teacher.gradeA.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(Uri.parse(teacher.gradeA))
                    .into(binding.image)
            }

            if (!teacher.gradeB.isNullOrEmpty()) {
                val fileName = getFileName(Uri.parse(teacher.gradeB))
                binding.fileName.text = "\uD83D\uDCCE Fayl: $fileName"
            }
        }

        binding.downloadImage.setOnClickListener { pickImageFromGallery() }
        binding.downloadFile.setOnClickListener { pickFileFromStorage() }

        binding.add.setOnClickListener {
            val title = binding.courseTitle.text.toString().trim()
            val desc = binding.courseDesc.text.toString().trim()
            val age = binding.courseAge.text.toString().trim()
            val subject = binding.courseSubject.text.toString().trim()
            val toifa = binding.courseToifa.text.toString().trim().ifEmpty { "Mavjud emas" }
            val cerA = binding.courseCertificate.text.toString().trim().ifEmpty { "Mavjud emas" }
            val cerB = binding.courseCertificateB.text.toString().trim().ifEmpty { "Mavjud emas" }
            val imageUri = selectedImageUri?.toString() ?: currentTeacher?.gradeA ?: ""
            val filePath = selectedFilePath ?: currentTeacher?.gradeB ?: ""

            if (title.isNotEmpty() && desc.isNotEmpty() && age.isNotEmpty() && subject.isNotEmpty()) {
                currentTeacher?.id?.let { id ->
                    viewModel.updateTeacher(id, title, desc, age, subject, toifa, cerA, imageUri, cerB, filePath)
                    viewModel.getTeachers()
                    findNavController().popBackStack()
                }
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
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf"))
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
                        requireContext().contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
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
                        binding.fileName.text = "\uD83D\uDCCE Fayl: $fileName"
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
        private const val IMAGE_PICK_CODE = 2001
        private const val FILE_PICK_CODE = 2002
    }
}
