package com.example.siliconacademy.fragments

import Group
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.siliconacademy.R
import com.example.siliconacademy.adapters.GroupRvAdapter
import com.example.siliconacademy.databinding.EditTeacherBinding
import com.example.siliconacademy.databinding.FragmentGroupsBinding
import com.example.siliconacademy.models.GroupViewModel
import com.example.siliconacademy.utils.Object.teacherId

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GroupsFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentGroupsBinding
    private val groupViewModel: GroupViewModel by viewModels()
    private val groupList = ArrayList<Group>()
    private lateinit var adapter: GroupRvAdapter
    private var isUiReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupsBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeGroups()
        groupViewModel.getGroups()
        isUiReady = true
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = GroupRvAdapter(object : GroupRvAdapter.OnItemClick {
            override fun onItemClick(group: Group, position: Int) {
                findNavController().navigate(
                    R.id.studentsFragment,
                    bundleOf(
                        "groupId" to group.id,
                        "groupTitle" to group.groupTitle,
                        "groupTime" to group.groupTime,
                        "subject" to group.groupSubject,
                        "fee" to group.fee.toString(),
                        "groupDay" to group.groupDay
                    )
                )
            }

            override fun onItemEditClick(group: Group, position: Int) {
                showEditDialog(group, position)
            }

            override fun onItemDeleteClick(group: Group, position: Int) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Eslatma!")
                    .setMessage("Bu guruh o'chirilsa, undagi barcha o'quvchilar o'chiriladi. Rozimisiz?")
                    .setPositiveButton("Ha") { _, _ ->
                        group.id?.let {
                            groupViewModel.deleteGroup(it)
                            groupList.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                    }
                    .setNegativeButton("Yo'q", null)
                    .show()
            }
        }, groupList)

        binding.recyclerView.adapter = adapter
    }

    private fun showEditDialog(group: Group, position: Int) {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val editBinding = EditTeacherBinding.inflate(layoutInflater)
        alertDialog.setView(editBinding.root)

        val timesList = arrayOf(
            "08:00 -> 10:00", "10:00 -> 12:00", "14:00 -> 16:00",
            "16:00 -> 18:00", "18:00 -> 20:00"
        )
        val daysList = arrayOf("Dushanba, Chorshanba, Juma", "Seshanba, Payshanba, Shanba")

        val timeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, timesList)
        val dayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, daysList)

        editBinding.times.adapter = timeAdapter
        editBinding.days.adapter = dayAdapter

        editBinding.groupTitle.setText(group.groupTitle)
        editBinding.groupSubject.setText(group.groupSubject)
        editBinding.groupFee.setText(group.fee.toString())

        val timeIndex = timesList.indexOfFirst { it == group.groupTime }
        if (timeIndex >= 0) editBinding.times.setSelection(timeIndex)

        val dayIndex = daysList.indexOfFirst { it == group.groupDay }
        if (dayIndex >= 0) editBinding.days.setSelection(dayIndex)

        editBinding.save.setOnClickListener {
            val updatedGroup = group.courseId?.let { course ->
                val fee = editBinding.groupFee.text.toString()
                Group(
                    id = group.id,
                    groupTitle = editBinding.groupTitle.text.toString(),
                    groupSubject = editBinding.groupSubject.text.toString(),
                    groupTime = timesList[editBinding.times.selectedItemPosition],
                    groupDay = daysList[editBinding.days.selectedItemPosition],
                    groupPosition = group.groupPosition,
                    fee = fee.toDouble(),
                    courseId = course
                )
            }

            updatedGroup?.let {
                groupViewModel.updateGroup(it)
                groupList[position] = it
                adapter.notifyItemChanged(position)
                Toast.makeText(requireContext(), "Yangilandi", Toast.LENGTH_SHORT).show()
            }

            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun observeGroups() {
        groupViewModel.isLoading.observe(viewLifecycleOwner) {
            if (isUiReady) {
                binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        groupViewModel.message.observe(viewLifecycleOwner) {
            it?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }

        groupViewModel.groups.observe(viewLifecycleOwner, Observer { allGroups ->
            if (allGroups != null) {
                groupList.clear()

                val positionFilter = param1?.toIntOrNull() ?: 0
                val filtered = allGroups.filter { group ->
                    val matchesPosition = group.groupPosition == positionFilter
                    val matchesTeacher = group.courseId?.id == teacherId
                    matchesPosition && matchesTeacher
                }

                groupList.addAll(filtered.reversed()) // Show latest groups first
                adapter.notifyDataSetChanged()

                if (groupList.isEmpty()) {
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.recyclerView.visibility = View.VISIBLE
                }
            } else {
                Log.e("GroupsFragment", "Groups list is null")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        groupViewModel.getGroups()
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
