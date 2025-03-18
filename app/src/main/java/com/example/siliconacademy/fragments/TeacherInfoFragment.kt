package com.example.siliconacademy.fragments

import android.content.ActivityNotFoundException
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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentTeacherInfoBinding
import com.example.siliconacademy.models.Teacher
import java.io.File

class TeacherInfoFragment : Fragment() {

    private lateinit var binding: FragmentTeacherInfoBinding
    private val TAG = "TeacherInfoFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherInfoBinding.inflate(inflater, container, false)

        val teacher = arguments?.getSerializable("course") as? Teacher
        teacher?.let { t ->
            binding.teacherName.text = t.title
            binding.teacherUniversity.text = t.desc
            binding.teacherAge.text = t.age
            binding.teacherSubject.text = t.subject
            binding.teacherToifa.text = t.toifa
            binding.aName.text = "Sertifikat 1: ${t.cerA}"
            binding.bName.text = "Sertifikat : ${t.cerB}"

            Glide.with(requireContext())
                .load(Uri.parse(teacher.gradeA))
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(binding.teacherCertImage)

            if (!t.gradeB.isNullOrEmpty()) {
                val fileUri = Uri.parse(t.gradeB)
                val fileName = getFileName(fileUri)
                binding.teacherFile.text = "📎 Fayl: $fileName"
                binding.teacherFile.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))

                binding.teacherFile.setOnClickListener {
                openFile(fileUri)
                }

            } else {
                binding.teacherFile.text = "📎 Fayl mavjud emas"
            }


            // Debug Logging
            logUriDetails(Uri.parse(t.gradeA), "Grade A (Image)")
            logUriDetails(Uri.parse(t.gradeB), "Grade B (File)")
        }

        binding.btnClose.setOnClickListener { findNavController().popBackStack() }

        return binding.root
    }
    fun getMimeTypeFromUrl(url: String): String {
        return when {
            url.endsWith(".pdf", true) -> "application/pdf"
            url.endsWith(".doc", true) || url.endsWith(".docx", true) -> "application/msword"
            url.endsWith(".ppt", true) || url.endsWith(".pptx", true) -> "application/vnd.ms-powerpoint"
            url.endsWith(".xls", true) || url.endsWith(".xlsx", true) -> "application/vnd.ms-excel"
            url.endsWith(".txt", true) -> "text/plain"
            else -> "*/*"
        }
    }

    private fun getFileName(uri: Uri): String {
        return try {
            var name = ""
            val cursor: Cursor? = requireContext().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
            if (name.isEmpty()) uri.lastPathSegment ?: "Fayl"
            else name
        } catch (e: Exception) {
            uri.lastPathSegment ?: "Fayl"
        }
    }
    private fun openFile(uri: Uri) {
        try {
            val file = File(uri.path!!)
            if (file.exists()) {
                val fileUri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    file
                )

                val mimeType = getMimeTypeFromUrl(file.name)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(fileUri, mimeType)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                startActivity(Intent.createChooser(intent, "Faylni ochish uchun dasturni tanlang"))
            } else {
                Toast.makeText(requireContext(), "Fayl topilmadi", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Faylni ochishda xatolik: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }





    private fun getMimeType(uri: Uri): String {
        val contentResolver = requireContext().contentResolver
        return contentResolver.getType(uri)
            ?: run {
                // Fallback based on file extension
                val ext = uri.toString().substringAfterLast('.', "")
                when (ext.lowercase()) {
                    "pdf" -> "application/pdf"
                    "doc" -> "application/msword"
                    "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    "txt" -> "text/plain"
                    "jpg", "jpeg" -> "image/jpeg"
                    "png" -> "image/png"
                    else -> "*/*"
                }
            }
    }

    private fun showNoAppDialog(mimeType: String) {
        val viewer = when {
            mimeType.contains("pdf") -> "PDF viewer"
            mimeType.contains("msword") || mimeType.contains("document") -> "Word viewer"
            mimeType.contains("image") -> "image viewer"
            else -> "file viewer"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Dastur topilmadi")
            .setMessage("Bu faylni ochish uchun $viewer kerak. Play Store'dan yuklab oling.")
            .setPositiveButton("Play Store") { _, _ ->
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/search?q=$viewer")
                }
                startActivity(intent)
            }
            .setNegativeButton("Bekor qilish", null)
            .show()
    }

    private fun logUriDetails(uri: Uri?, label: String) {
        if (uri == null) {
            Log.d(TAG, "$label URI is null")
            return
        }
        Log.d(TAG, "$label URI: $uri")
        try {
            val docFile = DocumentFile.fromSingleUri(requireContext(), uri)
            Log.d(TAG, "$label exists: ${docFile?.exists()}, name: ${docFile?.name}, type: ${docFile?.type}")
        } catch (e: Exception) {
            Log.e(TAG, "$label DocumentFile error: ${e.message}")
        }
    }

    companion object {
        fun newInstance(teacher: Teacher): TeacherInfoFragment {
            return TeacherInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("course", teacher)
                }
            }
        }
    }
}
