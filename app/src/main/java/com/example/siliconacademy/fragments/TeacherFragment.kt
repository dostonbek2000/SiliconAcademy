package com.example.siliconacademy.fragments

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.siliconacademy.R
import com.example.siliconacademy.adapters.TeacherRvAdapter
import com.example.siliconacademy.databinding.FragmentAddTeacherBinding
import com.example.siliconacademy.databinding.FragmentTeacherBinding
import com.example.siliconacademy.databinding.TeacherAddBinding
import com.example.siliconacademy.models.Teacher
import com.example.siliconacademy.models.TeacherViewModel
import com.example.siliconacademy.utils.Object.teacherId

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TeacherFragment : Fragment() {

    private lateinit var binding: FragmentTeacherBinding
    private lateinit var adapter: TeacherRvAdapter
    private val teacherList = ArrayList<Teacher>()
    private var query: Int? = null
    private var imagePickCallback: ((Uri) -> Unit)? = null
    private var filePickCallback: ((Uri) -> Unit)? = null
    private val viewModel: TeacherViewModel by viewModels()
    private var param1: String? = null
    private var param2: String? = null
    private var isUiReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        query = arguments?.getInt("query")
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        adapter = TeacherRvAdapter(object : TeacherRvAdapter.OnItemClick {
            override fun onItemClick(teacher: Teacher, position: Int) {
                teacherId = teacher.id
                when (query) {
                    1 -> {
                        val bundle = Bundle().apply {
                            putSerializable("course", teacher)
                        }
                        findNavController().navigate(R.id.teacherInfoFragment, bundle)
                    }
                    2 -> findNavController().navigate(
                        R.id.groupHomeFragment,
                        bundleOf("course" to teacher)
                    )
                    3 -> findNavController().navigate(
                        R.id.teacherFragment,
                        bundleOf("course" to teacher)
                    )
                }
            }

            override fun onItemDeleteClick(teacher: Teacher, position: Int) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Eslatma!")
                    .setMessage("Rostan ham o'chirmoqchimisiz?")
                    .setPositiveButton("Ha") { _, _ ->
                        teacher.id?.let { viewModel.deleteTeacher(it) }
                        teacherList.removeAt(position)
                        adapter.notifyItemRemoved(position)
                    }
                    .setNegativeButton("Yo‘q", null)
                    .show()
            }

            override fun onItemEditClick(teacher: Teacher, position: Int) {
                findNavController().navigate(R.id.editTeacherFragment, bundleOf("teacher" to teacher))
            }
        }, teacherList)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTeacherBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.teachers.observe(viewLifecycleOwner) {
            teacherList.clear()
            teacherList.addAll(it.reversed()) // Show latest teachers at top
            adapter.notifyDataSetChanged()
            binding.emptyText.visibility = if (teacherList.isEmpty()) View.VISIBLE else View.GONE
            binding.progressBar.visibility = View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (isUiReady) {
                binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        viewModel.message.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        viewModel.getTeachers()

        if (query == 1) {
            binding.toolBar.inflateMenu(R.menu.add)
            binding.toolBar.setOnMenuItemClickListener {
                findNavController().navigate(R.id.addTeacherFragment)
                true
            }
        }

        isUiReady = true
        return binding.root
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
            TeacherFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun isUriValid(uri: Uri): Boolean {
        return try {
            val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
            val isValid = cursor != null
            cursor?.close()
            isValid
        } catch (e: Exception) {
            Log.e("TeacherFragment", "Error validating URI: ${e.message}")
            false
        }
    }
}