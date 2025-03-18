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
import com.example.siliconacademy.utils.Object.teacherId

const val ARG_PARAM1 = "param1"
const val ARG_PARAM2 = "param2"

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



        groupsList = codialDatabase.getAllGroupsList()
        groupList = ArrayList()
        groupList.clear()
        if (param1 == "0")
            for (i in groupsList.indices) {
                if (groupsList[i].groupPosition == 0 && groupsList[i].courseId?.id == teacherId) {
                    groupList.add(groupsList[i])
                }
            }
        else {
            for (i in groupsList.indices) {
                if (groupsList[i].groupPosition == 1 && groupsList[i].courseId?.id == teacherId) {
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
                        "groupTime" to group.groupTime,
                        "subject" to group.groupSubject,
                        "fee" to group.fee,
                        "groupDay" to group.groupDay

                    )
                )
            }

            override fun onItemEditClick(group: Group, position: Int) {

                val alertDialog = AlertDialog.Builder(requireContext()).create()

                val editTeacher =
                    EditTeacherBinding.inflate(LayoutInflater.from(requireContext()), null, false)

                editTeacher.groupTitle.setText(group.groupTitle)


                val timesList =
                    arrayOf(
                        "08:00 -> 10:00",
                        "10:00 -> 12:00",
                        "14:00 -> 16:00",
                        "16:00 -> 18:00",
                        "18:00 -> 20:00"
                    )
                val daysList = arrayOf("Dushanba, Chorshanba, Juma", "Seshanba, Payshanba, Shanba")
                editTeacher.days.adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, daysList)
                val groupDay: String = daysList[editTeacher.days.selectedItemPosition]

                editTeacher.times.adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, timesList)

                alertDialog.setView(editTeacher.root)

                editTeacher.save.setOnClickListener {
                    val groupTitle: String = editTeacher.groupTitle.text.toString()
                       val groupTime: String = timesList[editTeacher.times.selectedItemPosition]
                    val subject:String=editTeacher.groupSubject.text.toString()
                    val fee:String=editTeacher.groupFee.text.toString()
                    group.groupTitle = groupTitle
                     group.groupTime = groupTime
                    group.groupDay=groupDay
                    group.groupSubject=subject
                    group.fee=fee
                    codialDatabase.editGroup(group)
                    adapter.notifyItemChanged(groupList.size)
                    adapter.notifyItemRangeChanged(position, groupList.size)

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
        }, groupList,codialDatabase)


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