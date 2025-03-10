package com.example.siliconacademy.fragments

import Group
import Teacher
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.models.Course
import com.example.siliconacademy.databinding.FragmentAddGroupBinding

class AddGroupFragment : Fragment() {

    private lateinit var binding: FragmentAddGroupBinding

    private lateinit var codialDatabase: CodialDatabase
    private lateinit var teachersList: ArrayList<Teacher>

    private lateinit var course: Course

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        codialDatabase = CodialDatabase(requireContext())
        teachersList = codialDatabase.getAllTeachersList()
        course = arguments?.getSerializable("course") as Course
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddGroupBinding.inflate(layoutInflater, container, false)

        val teacherList = ArrayList<String>()
        for (i in teachersList.indices) {
            teacherList.add(teachersList[i].name!!)
        }

        val timesList =
            arrayOf(
                "08:00 -> 10:00",
                "10:00 -> 12:00",
                "14:00 -> 16:00",
                "16:00 -> 18:00",
                "18:00 -> 20:00"
            )

        val daysList = arrayOf("Dushanba, Chorshanba, Juma", "Seshanba, Payshanba, Shanba")
        binding.days.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, daysList)

        binding.times.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, timesList)

        binding.save.setOnClickListener {
            val groupTitle: String = binding.groupTitle.text.toString()
             val groupTime: String = timesList[binding.times.selectedItemPosition]
            val groupDay: String = daysList[binding.days.selectedItemPosition]

            codialDatabase.addGroup(
                Group(
                    1,
                    groupTitle,

                    groupTime,
                    groupDay,

                    course
                )
            )
            Toast.makeText(requireContext(), "Saqlandi", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        return binding.root
    }
}