package com.example.siliconacademy.fragments

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
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
            binding.name.text = "👤 Ismi: ${it.name}"
            binding.subject.text = "📚 Fani: ${it.subject}"
            binding.age.text = "🎂 Yoshi: ${it.age}"
            binding.teacher.text = "👨‍🏫 Ustoz: ${it.teacherName}"
            binding.type.text = "📄 Test turi: ${it.testType}"

            Glide.with(requireContext()).load(it.imageUri).into(binding.image)

            if (!it.fileUri.isNullOrEmpty()) {
                val uri = Uri.parse(it.fileUri)
                val fileName = getFileName(uri)
                binding.fileName.text = "📎 Fayl: $fileName"
                binding.fileName.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))

                binding.fileName.setOnClickListener {
                    result.fileUri?.let { it1 -> openFile(it1) }
                }
            } else {
                binding.fileName.text = "📎 Fayl mavjud emas"
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
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
            name.ifEmpty { uri.lastPathSegment ?: "Fayl" }
        } catch (e: Exception) {
            uri.lastPathSegment ?: "Fayl"
        }
    }

    private fun openFile(fileUri: String) {
        try {
            val uri = Uri.parse(fileUri)

            // First check if we can directly access the file
            val docFile = DocumentFile.fromSingleUri(requireContext(), uri)
            if (docFile != null && docFile.exists()) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, docFile.type ?: "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
             /*   if (intent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Faylni ochish uchun dastur topilmadi", Toast.LENGTH_SHORT).show()
                }*/

                try {
                    startActivity(intent)
                    return
                } catch (e: Exception) {
                    // If direct opening fails, continue to the permission retry
                }
            }

            // If we reach here, try to get permission again
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                val newIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, docFile?.type ?: "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                startActivity(newIntent)

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Faylni ochish uchun ruxsat yo'q. Iltimos qayta tanlang.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Faylni ochishda xatolik: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
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
