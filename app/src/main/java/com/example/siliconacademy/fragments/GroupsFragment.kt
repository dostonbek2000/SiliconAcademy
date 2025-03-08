package com.example.siliconacademy.fragments

import Group
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.adapters.GroupRvAdapter
import com.example.siliconacademy.db.CodialDatabase
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.EditTeacherBinding
import com.example.siliconacademy.databinding.FragmentGroupsBinding
import com.example.siliconacademy.utils.Object.courseId

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GroupsFragment : Fragment() {

    private val TAG = "GroupsFragment"
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentGroupsBinding
    private lateinit var codialDatabase: CodialDatabase
    private lateinit var groupsList: ArrayList<Group>
    private lateinit var groupList: ArrayList<Group>
    private lateinit var adapter: GroupRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        codialDatabase =
            CodialDatabase(requireContext())
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupsBinding.inflate(layoutInflater, container, false)

        val teachersList: ArrayList<String> = ArrayList()
        for (i in codialDatabase.getAllTeachersList().indices) {
            teachersList.add(codialDatabase.getAllTeachersList()[i].name!!)
        }

        groupsList = codialDatabase.getAllGroupsList()
        groupList = ArrayList()
        groupList.clear()
        if (param1 == "0")
            for (i in groupsList.indices) {
                if (groupsList[i].groupPosition == 0 && groupsList[i].courseId?.id == courseId) {
                    groupList.add(groupsList[i])
                }
            }
        else {
            for (i in groupsList.indices) {
                if (groupsList[i].groupPosition == 1 && groupsList[i].courseId?.id == courseId) {
                    groupList.add(groupsList[i])
                }
            }
        }

        adapter = GroupRvAdapter(object : GroupRvAdapter.OnItemClick {
            override fun onItemClick(group: Group, position: Int) {
                findNavController().navigate(
                    R.id.studentsFragment,
                    bundleOf(
                        "groupId" to group.id,
                        "groupTitle" to group.groupTitle,
                        "groupTime" to group.groupTime
                    )
                )
            }

            override fun onItemEditClick(group: Group, position: Int) {

                val alertDialog = AlertDialog.Builder(requireContext()).create()

                val editTeacher =
                    EditTeacherBinding.inflate(LayoutInflater.from(requireContext()), null, false)

                editTeacher.groupTitle.setText(group.groupTitle)

                editTeacher.teacherName.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    teachersList
                )
                val timesList =
                    arrayOf(
                        "08:00 -> 10:00",
                        "10:00 -> 12:00",
                        "14:00 -> 16:00",
                        "16:00 -> 18:00",
                        "18:00 -> 20:00"
                    )

                editTeacher.times.adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, timesList)

                alertDialog.setView(editTeacher.root)

                editTeacher.add.setOnClickListener {
                    val groupTitle: String = editTeacher.groupTitle.text.toString()
                    val groupTeacherName: String =
                        codialDatabase.getAllTeachersList()[editTeacher.teacherName.selectedItemPosition].name!!
                    val groupTime: String = timesList[editTeacher.times.selectedItemPosition]

                    group.groupTitle = groupTitle
                    group.groupTeacherName = groupTeacherName
                    group.groupTime = groupTime
                    codialDatabase.editGroup(group)
                    adapter.notifyItemChanged(groupList.size)
                    adapter.notifyItemRangeChanged(position, groupList.size)

                    alertDialog.dismiss()
                }

                editTeacher.close.setOnClickListener {
                    alertDialog.dismiss()
                }

                alertDialog.show()
            }

            override fun onItemDeleteClick(group: Group, position: Int) {
                val alertDialog = AlertDialog.Builder(requireContext())

                alertDialog.setTitle("Eslatma!")
                alertDialog.setMessage("Bu guruh o'chirilsa undagi barcha o'quvchilar o'chiriladi. Rozimisiz?")
                alertDialog.setPositiveButton("Ha") { _, _ ->
                    codialDatabase.deleteGroup(group)
                    groupList.remove(group)
                    adapter.notifyItemRemoved(position)
                    adapter.notifyItemRangeChanged(position, groupList.size)
                    alertDialog.create().dismiss()
                }
                alertDialog.setNegativeButton("Yo'q") { _, _ ->
                    alertDialog.create().dismiss()
                }

                alertDialog.show()
            }
        }, groupList)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GroupsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}