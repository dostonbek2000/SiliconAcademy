package com.example.siliconacademy.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.siliconacademy.databinding.FragmentAddResultBinding
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.models.Results
class AddResultFragment : Fragment() {

    private lateinit var binding: FragmentAddResultBinding
    private lateinit var database: CodialDatabase
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddResultBinding.inflate(inflater, container, false)

        // Initialize the database here
        database = CodialDatabase.getInstance(requireContext()) // Ensure this method exists in CodialDatabase

        binding.imageButton.setOnClickListener {
            pickImageFromGallery()
        }

        binding.save.setOnClickListener {
            saveResult()
        }

        return binding.root
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.image.setImageURI(imageUri)
        }
    }

    private fun saveResult() {
        val name = binding.name.text.toString().trim()
        val subject = binding.subject.text.toString().trim()
        val desc = binding.desc.text.toString().trim()
        val teacherName = binding.teacherName.text.toString().trim()

        if (name.isEmpty() || subject.isEmpty() || desc.isEmpty() || imageUri == null || teacherName.isEmpty()) {
            Toast.makeText(requireContext(), "Barcha maydonlarni to'ldiring!", Toast.LENGTH_SHORT).show()
            return
        }

        val result = Results(
            id = 1, // Replace with actual student ID
            name = name,
            teacherName = teacherName,
            subject = subject,
            desc = desc,
            image = imageUri.toString()
        )

        database.addResult(result)
        Toast.makeText(requireContext(), "Natija saqlandi!", Toast.LENGTH_SHORT).show()
        requireActivity().onBackPressed()
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}

