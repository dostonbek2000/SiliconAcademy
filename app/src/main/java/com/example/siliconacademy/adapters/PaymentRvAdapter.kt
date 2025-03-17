package com.example.siliconacademy.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.siliconacademy.R
import com.example.siliconacademy.databinding.PaymentItemBinding
import com.example.siliconacademy.models.Payment

class PaymentRvAdapter(var onItemClick: OnItemClick, var itemList: ArrayList<Payment>) :
    RecyclerView.Adapter<PaymentRvAdapter.PaymentVh>() {

    inner class PaymentVh(private val binding: PaymentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(payment: Payment, position: Int) {
            binding.root.setOnClickListener {
                onItemClick.onItemClick(payment, position)
            }
            binding.popClc.setOnClickListener { view->
                showPopupMenu(view, payment, position)
            }
            binding.fullName.text = payment.fullName
            binding.price.text=payment.amount
            binding.date.text = payment.date
        }
        private fun showPopupMenu(view: View, payment: Payment, position: Int) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.result, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit -> {
                        onItemClick.onItemEditClick(payment, position)
                        true
                    }
                    R.id.delete -> {
                        onItemClick.onItemDeleteClick(payment, position)
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentVh {
        return PaymentVh(
           PaymentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder:PaymentVh, position: Int) {
        holder.onBind(itemList[position], position)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    interface OnItemClick {
        fun onItemClick(payment: Payment, position: Int)
        fun onItemDeleteClick(payment: Payment, position: Int)
        fun onItemEditClick(payment: Payment, position: Int)
    }
}