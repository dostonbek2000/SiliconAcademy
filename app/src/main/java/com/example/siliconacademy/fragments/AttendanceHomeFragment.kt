package com.example.siliconacademy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentAttendanceHomeBinding
import com.example.siliconacademy.db.CodialDatabase

class AttendanceHomeFragment : Fragment() {
    private lateinit var binding: FragmentAttendanceHomeBinding
    private lateinit var codialDatabase: CodialDatabase
    private var groupId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAttendanceHomeBinding.inflate(inflater, container, false)
        codialDatabase = CodialDatabase(requireContext())

        groupId = arguments?.getInt("groupId")

        val attendanceGroups = codialDatabase.getAllAttendanceGroupsByGroupId(groupId!!)

        val adapter = object : RecyclerView.Adapter<GroupViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.attendance_item, parent, false)
                return GroupViewHolder(view)
            }
            override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
                val (id, date) = attendanceGroups[position]
                holder.date.text = date
                holder.itemView.setOnClickListener {
                    findNavController().navigate(R.id.attendanceInfoFragment, bundleOf("attendanceGroupId" to id))
                }
            }
            override fun getItemCount(): Int = attendanceGroups.size
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.toolbar.inflateMenu(R.menu.add)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.add -> {
                    groupId?.let {
                        findNavController().navigate(R.id.addAttendanceFragment, bundleOf("groupId" to it))
                    } ?: Toast.makeText(requireContext(), "Group ID is missing", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        return binding.root
    }

    class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.date)
    }
}