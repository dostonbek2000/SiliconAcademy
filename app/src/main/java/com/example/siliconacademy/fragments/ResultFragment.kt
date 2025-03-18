package com.example.siliconacademy.fragments
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ResultFragment : Fragment() {

    private lateinit var binding: FragmentResultBinding
    private lateinit var codialDatabase: CodialDatabase
    private lateinit var adapter: ResultRvAdapter
    private lateinit var resultList: ArrayList<Results>
    private lateinit var resultsList: ArrayList<Results>
    private var imageUri: Uri? = null
    private var fileUri: Uri? = null
    private var param1:String?=null
    private var param2:String?=null
    private var imagePickCallback: ((Uri) -> Unit)? = null
    private var filePickCallback: ((Uri) -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding = FragmentResultBinding.inflate(inflater, container, false)
        resultsList=codialDatabase.getAllResultsList()
        resultList=ArrayList()
        resultList.clear()
        when (param1) {
            "0" -> for (i in resultsList.indices) {
                if (resultsList[i].resultPosition == 0) {
                    resultList.add(resultsList[i])
                }
            }
            "1" -> {
                for (i in resultsList.indices) {
                    if (resultsList[i].resultPosition == 1) {
                        resultList.add(resultsList[i])
                    }
                }
            }
            else -> for (i in resultsList.indices){
                if (resultsList[i].resultPosition==2){
                    resultList.add(resultsList[i])
                }
            }
        }

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

                dialogBinding.name.setText(results.name)
                dialogBinding.subject.setText(results.subject)
                dialogBinding.age.setText(results.age)
                dialogBinding.teacherName.setText(results.teacherName)

                val testTypes = arrayOf("DTM", "IELTS", "CEFR")
                val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, testTypes)
                dialogBinding.type.adapter = spinnerAdapter
                results.resultPosition?.let { dialogBinding.type.setSelection(it) }

                Glide.with(requireContext()).load(Uri.parse(results.imageUri)).into(dialogBinding.image)
                dialogBinding.fileName.text = getFileNameFromUri(results.fileUri)

                var updatedImageUri: Uri? = Uri.parse(results.imageUri)
                var updatedFileUri: Uri? = Uri.parse(results.fileUri)

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

                    results.name = name
                    results.subject = subject
                    results.age = age
                    results.teacherName = teacherName
                    results.testType = testType
                    results.resultPosition = newPosition
                    results.imageUri = updatedImageUri.toString()
                    results.fileUri = updatedFileUri?.toString() ?: ""

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

    private fun pickImageFromGallery(callback: (Uri) -> Unit) {
        imagePickCallback = callback
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun pickFileFromStorage(callback: (Uri) -> Unit) {
        filePickCallback = callback
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT) // ✅ correct way
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
                FILE_PICK_CODE -> {
                    fileUri = data.data
                    fileUri?.let {
                        try {
                            // ✅ take permission PERMANENTLY
                            requireContext().contentResolver.takePersistableUriPermission(
                                it,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                            )
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                        }
                        filePickCallback?.invoke(it)
                    }
                }
            }
        }
    }




    private fun getFileNameFromUri(uriString: String?): String {
        return if (!uriString.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(uriString)
                getFileName(uri)
            } catch (e: Exception) {
                uriString.substringAfterLast("/")
            }
        } else "📎 Fayl mavjud emas"
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

    private fun compressAndSaveImageToCache(uri: Uri): Uri? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
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


companion object {
        private const val IMAGE_PICK_CODE = 1000
    private const val FILE_PICK_CODE = 1002
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