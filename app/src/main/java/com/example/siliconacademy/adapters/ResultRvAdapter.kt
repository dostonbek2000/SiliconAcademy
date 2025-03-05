package com.example.siliconacademy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.ResultItemBinding
import com.example.siliconacademy.models.Results

class ResultRvAdapter(private var itemList:ArrayList<Results>,private val onItemClick: OnItemClick): RecyclerView.Adapter<ResultRvAdapter.ResultVh>() {
    inner class ResultVh(private val binding: ResultItemBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(results: Results,position: Int) {
            binding.title.text = results.name
            binding.root.setOnClickListener{
                onItemClick.onItemClick(results, position)

            }
            binding.popClc.setOnClickListener { view->
                showPopupMenu(view,results, position)
            }

        }
    }
    private fun showPopupMenu(view: View, results: Results, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.menuInflater.inflate(R.menu.pop, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    onItemClick.onItemEditClick(results, position)
                    true
                }
                R.id.delete -> {
                    onItemClick.onItemDeleteClick(results, position)
                    true
                }


                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultVh {
        return ResultVh(ResultItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
       return itemList.size
    }

    override fun onBindViewHolder(holder: ResultVh, position: Int) {
        val result =itemList[position]
        holder.onBind(result,position)
    }
    interface OnItemClick{
        fun onItemClick(results: Results,position: Int)
        fun onItemEditClick(results: Results,position: Int)
        fun onItemDeleteClick(results: Results,position: Int)

    }
}