package com.example.siliconacademy.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.FragmentAttendanceHomeBinding
import com.example.siliconacademy.models.AttendanceViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AttendanceHomeFragment : Fragment() {
    private lateinit var binding: FragmentAttendanceHomeBinding
    private var groupId: Int? = null
    private var groupTitle: String? = null
    private val viewModel: AttendanceViewModel by viewModels()
    private var isUiReady = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAttendanceHomeBinding.inflate(inflater, container, false)
        groupId = arguments?.getInt("groupId")

        val adapter = GroupAdapter { attendanceGroupId ->
            findNavController().navigate(R.id.attendanceInfoFragment, bundleOf("attendanceGroupId" to attendanceGroupId))
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.attendanceGroups.observe(viewLifecycleOwner) {
            adapter.submitList(it.reversed()) // Show latest attendance first
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (isUiReady) {
                binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        viewModel.message.observe(viewLifecycleOwner) {
            it?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            it?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }

        groupId?.let {
            viewModel.getAttendanceGroupsByGroupId(it)
        }

        binding.toolbar.inflateMenu(R.menu.add)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.add) {
                groupId?.let {
                    findNavController().navigate(R.id.addAttendanceFragment, bundleOf("groupId" to it))
                }
                true
            } else false
        }

        isUiReady = true
        return binding.root
    }

    class GroupAdapter(val onItemClick: (Int) -> Unit) : RecyclerView.Adapter<GroupViewHolder>() {
        private val data = mutableListOf<Pair<Int, String>>()

        fun submitList(list: List<Pair<Int, String>>) {
            data.clear()
            data.addAll(list)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.attendance_item, parent, false)
            return GroupViewHolder(view)
        }

        override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
            val (id, date) = data[position]
            holder.date.text = formatDateToReadable(date)
            holder.itemView.setOnClickListener {
                onItemClick(id)
            }
        }

        private fun formatDateToReadable(dateString: String): String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = parser.parse(dateString)
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formatter.format(date!!)
            } catch (e: Exception) {
                dateString
            }
        }

        override fun getItemCount(): Int = data.size
    }

    class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.date)
    }
}