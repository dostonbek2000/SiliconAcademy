package com.example.siliconacademy.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.StudentItemBinding
import com.example.siliconacademy.models.Student

class StudentRvAdapter(var onItemClick: OnItemClick, var itemList: ArrayList<Student>) :
    RecyclerView.Adapter<StudentRvAdapter.StudentVh>() {

    inner class StudentVh(private val binding: StudentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(student: Student, position: Int) {
            binding.root.setOnClickListener {
                onItemClick.onItemClick(student, position)
            }
            binding.studentFullName.text = "${student.name} ${student.surname}"

            // Handle popup menu on moreOptions click
            binding.moreOptions.setOnClickListener { view ->
                showPopupMenu(view, student, position)
            }
        }

        private fun showPopupMenu(view: View, student: Student, position: Int) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.pop, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit -> {
                        onItemClick.onItemEditClick(student, position)
                        true
                    }
                    R.id.delete -> {
                        onItemClick.onItemDeleteClick(student, position)
                        true
                    }
                    R.id.pay -> {
                        onItemClick.onItemPayClick(student,position)
                        true
                    }
                    R.id.attendance -> {
                        onItemClick.onItemAttendance(student,position)
                        true
                    }


                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentVh {
        return StudentVh(
            StudentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StudentVh, position: Int) {
        holder.onBind(itemList[position], position)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    interface OnItemClick {
        fun onItemClick(student: Student, position: Int)
        fun onItemEditClick(student: Student, position: Int)
        fun onItemDeleteClick(student: Student, position: Int)
        fun onItemPayClick(student: Student,position: Int)
        fun onItemAttendance(student: Student,position: Int)
    }
}
