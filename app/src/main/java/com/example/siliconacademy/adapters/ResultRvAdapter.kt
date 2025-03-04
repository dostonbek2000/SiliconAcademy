package com.example.siliconacademy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.siliconacademy.databinding.ResultItemBinding
import com.example.siliconacademy.models.Results

class ResultRvAdapter(private var itemList:ArrayList<Results>): RecyclerView.Adapter<ResultRvAdapter.ResultVh>() {
    inner class ResultVh(private val binding: ResultItemBinding):RecyclerView.ViewHolder(binding.root){
        fun onBind(results: Results,position: Int){
            binding.title.text=results.name
        }
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
}