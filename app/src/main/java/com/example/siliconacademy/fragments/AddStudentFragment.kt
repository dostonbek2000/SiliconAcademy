package com.example.siliconacademy.fragments

import Group
import Student
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.databinding.FragmentAddStudentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddStudentFragment : Fragment() {

    private lateinit var binding: FragmentAddStudentBinding
    private lateinit var codialDatabase: CodialDatabase
    private var groupId: Int? = null
    private lateinit var group: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codialDatabase = CodialDatabase(requireContext())
        groupId = arguments?.getInt("groupId")
        group = codialDatabase.getGroupById(groupId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddStudentBinding.inflate(layoutInflater, container, false)

        binding.save.setOnClickListener {
            val name: String = binding.name.text.toString()
            val surname: String = binding.surname.text.toString()
            val fatherName: String = binding.fatherName.text.toString()
val age:String=binding.age.text.toString()
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            codialDatabase.addStudent(Student(name=name, surname=surname, fatherName=fatherName,age=age , groupId = group, accountBalance = 0.0, date = currentDate))
            findNavController().popBackStack()
        }

        return binding.root
    }
}