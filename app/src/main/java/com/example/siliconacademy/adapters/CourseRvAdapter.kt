package com.example.siliconacademy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.CourseItemBinding
import com.example.siliconacademy.models.Course
import com.example.siliconacademy.models.Teacher

class CourseRvAdapter(var onItemClick: OnItemClick, var itemList: ArrayList<Course>) :
    RecyclerView.Adapter<CourseRvAdapter.CourseVh>() {

    inner class CourseVh(private val binding: CourseItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(course: Course, position: Int) {
            binding.root.setOnClickListener {
                onItemClick.onItemClick(course, position)
            }
            binding.menu.setOnClickListener { view->
                showPopupMenu(view,course, position)
            }
            binding.title.text = course.title
        }
        private fun showPopupMenu(view: View, course: Course, position: Int) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.result, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit -> {
                        onItemClick.onItemEditClick(course, position)
                        true
                    }
                    R.id.delete -> {
                        onItemClick.onItemDeleteClick(course, position)
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseVh {
        return CourseVh(
            CourseItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder:CourseVh, position: Int) {
        holder.onBind(itemList[position], position)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    interface OnItemClick {
        fun onItemClick(course: Course, position: Int)
        fun onItemDeleteClick(course: Course, position: Int)
        fun onItemEditClick(course: Course, position: Int)
    }
}