package com.example.siliconacademy.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.databinding.FragmentAddResultBinding
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.models.Results
import java.io.File
import java.io.FileOutputStream

class AddResultFragment : Fragment() {

    private lateinit var binding: FragmentAddResultBinding
    private lateinit var database: CodialDatabase
    private var imageUri: Uri? = null

    private val testList = arrayOf("DTM", "IELTS", "CEFR")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddResultBinding.inflate(inflater, container, false)
        database = CodialDatabase.getInstance(requireContext())

        binding.type.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            testList
        )

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
            val selectedUri = data?.data
            selectedUri?.let {
                val compressedUri = compressAndSaveImageToCache(it)
                imageUri = compressedUri
                binding.image.setImageURI(compressedUri)
            }
        }
    }

    private fun compressAndSaveImageToCache(uri: Uri): Uri? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            // Scale to max width 1080px
            val targetWidth = 1080
            val targetHeight = (originalBitmap.height * targetWidth) / originalBitmap.width

            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

            val file = File(requireContext().cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)

            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            outputStream.flush()
            outputStream.close()

            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveResult() {
        val name = binding.name.text.toString().trim()
        val subject = binding.subject.text.toString().trim()
        val age = binding.age.text.toString().trim()
        val teacherName = binding.teacherName.text.toString().trim()
        val testType = binding.type.selectedItem?.toString() ?: ""

        if (name.isEmpty() || subject.isEmpty() || age.isEmpty() || teacherName.isEmpty() || imageUri == null || testType.isEmpty()) {
            Toast.makeText(requireContext(), "Barcha maydonlarni to'ldiring!", Toast.LENGTH_SHORT).show()
            return
        }

        val position = when (testType) {
            "DTM" -> 0
            "IELTS" -> 1
            "CEFR" -> 2
            else -> -1
        }

        if (position == -1) {
            Toast.makeText(requireContext(), "Test turi noto‘g‘ri!", Toast.LENGTH_SHORT).show()
            return
        }

        val result = Results(
            resultPosition = position,
            name = name,
            age = age,
            testType = testType,
            teacherName = teacherName,
            subject = subject,
            imageUri = imageUri.toString()
        )

        database.addResult(result)

        Toast.makeText(requireContext(), "Natija saqlandi!", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}
