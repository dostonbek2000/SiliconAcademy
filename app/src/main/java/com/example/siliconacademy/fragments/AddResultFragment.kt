package com.example.siliconacademy.fragments

import Teacher
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentAddResultBinding
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.models.Results
class AddResultFragment : Fragment() {

    private lateinit var codialDatabase: CodialDatabase
    private lateinit var binding: FragmentAddResultBinding
    private lateinit var database: CodialDatabase
    private var imageUri: Uri? = null
    private val testList = arrayOf("DTM", "IELTS", "CEFR")
    private lateinit var teachersList: ArrayList<Teacher>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codialDatabase = CodialDatabase(requireContext())
        teachersList = codialDatabase.getAllTeachersList()
    }

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
          val teacherList = ArrayList<String>()
        for (i in teachersList.indices) {
            teacherList.add(teachersList[i].name!!)
        binding.teacherName.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, teacherList)
        binding.type.adapter=
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,testList)


        }

        val name:String = binding.name.text.toString().trim()
        val subject:String = binding.subject.text.toString().trim()
        val age=binding.age.text.toString().trim()
           val test:String=testList[binding.type.selectedItemPosition]
        val teacherName:String = teacherList[binding.teacherName.selectedItemPosition]


        if (name.isEmpty() || subject.isEmpty() || age.isEmpty()|| subject.isEmpty() ||test.isEmpty()|| teacherName.isEmpty() || imageUri == null || teacherName.isEmpty()) {
            Toast.makeText(requireContext(), "Barcha maydonlarni to'ldiring!", Toast.LENGTH_SHORT).show()
            return
        }
var position: Int? =null
       when(test){
           "DTM"->{
               position=0
           }
           "IELTS"-> {
               position = 1
           }
           "CEFR"->{
               position=2
           }
       }
        val result = Results(

            resultPosition = position,
            name = name,
            age =age,
            testType = test,
            teacherName = teacherName,
            subject = subject,


        )

        database.addResult(result)
        Toast.makeText(requireContext(), "Natija saqlandi!", Toast.LENGTH_SHORT).show()
       findNavController().popBackStack()
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}

