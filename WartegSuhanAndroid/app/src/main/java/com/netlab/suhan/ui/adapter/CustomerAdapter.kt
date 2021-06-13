package com.netlab.suhan.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.netlab.suhan.data.Customer
import com.netlab.suhan.databinding.ItemCustomerBinding
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class CustomerAdapter : RecyclerView.Adapter<CustomerAdapter.ViewHolder>() {
    var onDataSelected: ((Customer) -> Unit)? = null
    private var list = ArrayList<Customer>()

    fun setList(list: ArrayList<Customer>) {
        this.list = list
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemCustomerBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(customer: Customer) {
            with(binding) {
                tvId.text = "#${customer.id}"

                val numberFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                tvTotalExpense.text = "Jumlah Pengeluaran: ${numberFormat.format(customer.totalExpense)}"
                tvBalance.text = "Saldo: ${numberFormat.format(customer.balance)}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            onDataSelected?.invoke(list[position])
        }
    }

    override fun getItemCount(): Int = list.size
}