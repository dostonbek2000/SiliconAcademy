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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.siliconacademy.R
import com.example.siliconacademy.adapters.ResultRvAdapter
import com.example.siliconacademy.databinding.FragmentAddResultBinding
import com.example.siliconacademy.databinding.FragmentResultBinding
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.models.Results
import java.io.File
import java.io.FileOutputStream

class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding
    private lateinit var codialDatabase: CodialDatabase
    private lateinit var adapter: ResultRvAdapter
    private lateinit var resultList: ArrayList<Results>
    private var imageUri: Uri? = null
    private var imagePickCallback: ((Uri) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(inflater, container, false)
        codialDatabase = CodialDatabase(requireContext())

        val allResults = codialDatabase.getAllResultsList()
        resultList = ArrayList(allResults)

        adapter = ResultRvAdapter(resultList, object : ResultRvAdapter.OnItemClick {
            override fun onItemClick(results: Results, position: Int) {
                val bundle = Bundle()
                bundle.putSerializable("result", results)
                findNavController().navigate(R.id.resultInfoFragment, bundle)
            }

            override fun onItemEditClick(results: Results, position: Int) {
                val alertDialog = AlertDialog.Builder(requireContext()).create()
                val dialogBinding = FragmentAddResultBinding.inflate(
                    LayoutInflater.from(requireContext()),
                    null,
                    false
                )
                alertDialog.setView(dialogBinding.root)

                dialogBinding.name.setText(results.name)
                dialogBinding.subject.setText(results.subject)
                dialogBinding.age.setText(results.age)
                dialogBinding.teacherName.setText(results.teacherName)

                val testTypes = arrayOf("DTM", "IELTS", "CEFR")
                val spinnerAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    testTypes
                )
                dialogBinding.type.adapter = spinnerAdapter
                results.resultPosition?.let { dialogBinding.type.setSelection(it) }

                // Show current image
                Glide.with(requireContext())
                    .load(Uri.parse(results.imageUri))
                    .into(dialogBinding.image)

                var updatedImageUri: Uri? = Uri.parse(results.imageUri)

                dialogBinding.imageButton.setOnClickListener {
                    imagePickCallback = { uri ->
                        updatedImageUri = uri
                        dialogBinding.image.setImageURI(uri)
                    }
                    pickImageFromGallery()
                }

                dialogBinding.save.setOnClickListener {
                    val name = dialogBinding.name.text.toString().trim()
                    val subject = dialogBinding.subject.text.toString().trim()
                    val age = dialogBinding.age.text.toString().trim()
                    val teacherName = dialogBinding.teacherName.text.toString().trim()
                    val testType = dialogBinding.type.selectedItem?.toString() ?: ""

                    if (name.isEmpty() || subject.isEmpty() || age.isEmpty() || teacherName.isEmpty() || testType.isEmpty()) {
                        Toast.makeText(
                            requireContext(),
                            "Barcha maydonlarni to'ldiring!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val newPosition = when (testType) {
                        "DTM" -> 0
                        "IELTS" -> 1
                        "CEFR" -> 2
                        else -> -1
                    }

                    if (newPosition == -1) {
                        Toast.makeText(requireContext(), "Test turi noto‘g‘ri!", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }

                    results.name = name
                    results.subject = subject
                    results.age = age
                    results.teacherName = teacherName
                    results.testType = testType
                    results.resultPosition = newPosition
                    results.imageUri = updatedImageUri.toString()

                    codialDatabase.editResult(results)
                    adapter.notifyItemChanged(position)
                    alertDialog.dismiss()
                }

                alertDialog.show()
            }

            override fun onItemDeleteClick(results: Results, position: Int) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Eslatma!")
                    .setMessage("Rostan ham o'chirmoqchimisiz?")
                    .setPositiveButton("Ha") { _, _ ->
                        codialDatabase.deleteResult(results)
                        resultList.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        adapter.notifyItemRangeChanged(position, resultList.size)
                    }
                    .setNegativeButton("Yo'q", null)
                    .show()
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

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
                imagePickCallback?.invoke(compressedUri!!)
            }
        }
    }

    private fun compressAndSaveImageToCache(uri: Uri): Uri? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)

            val targetWidth = 1080
            val targetHeight = (originalBitmap.height * targetWidth) / originalBitmap.width

            val scaledBitmap =
                Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

            val file =
                File(requireContext().cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
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

    companion object {
        private const val IMAGE_PICK_CODE = 1000

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
