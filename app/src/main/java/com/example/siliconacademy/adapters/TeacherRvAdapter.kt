package com.example.siliconacademy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.TeacherItemBinding
import com.example.siliconacademy.models.Teacher

class TeacherRvAdapter(var onItemClick: OnItemClick, var itemList: ArrayList<Teacher>) :
    RecyclerView.Adapter<TeacherRvAdapter.TeacherVh>() {

    inner class TeacherVh(private val binding: TeacherItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(teacher: Teacher, position: Int) {
            binding.root.setOnClickListener {
                onItemClick.onItemClick(teacher, position)
            }
            binding.menu.setOnClickListener { view->
                showPopupMenu(view,teacher, position)
            }
            binding.courseTitle.text = teacher.title
        }
        private fun showPopupMenu(view: View, teacher: Teacher, position: Int) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.result, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit -> {
                        onItemClick.onItemEditClick(teacher, position)
                        true
                    }
                    R.id.delete -> {
                        onItemClick.onItemDeleteClick(teacher, position)
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherVh {
        return TeacherVh(
            TeacherItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TeacherVh, position: Int) {
        holder.onBind(itemList[position], position)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    interface OnItemClick {
        fun onItemClick(teacher: Teacher, position: Int)
        fun onItemDeleteClick(teacher: Teacher, position: Int)
        fun onItemEditClick(teacher: Teacher, position: Int)
    }
}