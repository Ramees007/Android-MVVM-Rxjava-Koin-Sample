package com.ramees.currency_convertor.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ramees.currency_convertor.data.ConvertedItem
import com.ramees.currency_convertor.databinding.CurrencyConvertedListItemBinding

class ConversionAdapter(private val convertedItems: List<ConvertedItem>) :
    RecyclerView.Adapter<ConversionAdapter.ConversionVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversionVH {
        val b = CurrencyConvertedListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConversionVH(b)

    }



    override fun onBindViewHolder(holder: ConversionVH, position: Int) {
        holder.bind(convertedItems[position])
    }

    override fun getItemCount() = convertedItems.size

    class ConversionVH(private val binding: CurrencyConvertedListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: ConvertedItem) {
            binding.currencyNameTV.text = item.currency.displayName
            binding.amtTV.text = "${item.currency.symbol} ${item.amount}"
            binding.currencySymbolTV.text = item.currency.currencyCode
        }

    }
}