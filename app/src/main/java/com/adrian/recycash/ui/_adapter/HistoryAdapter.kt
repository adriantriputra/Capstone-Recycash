package com.adrian.recycash.ui._adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adrian.recycash.R
import com.adrian.recycash.data.remote.response.HistoryResponse
import com.adrian.recycash.databinding.ItemHistoryBinding

class HistoryAdapter(
    private val history: List<HistoryResponse?>
) : RecyclerView.Adapter<HistoryAdapter.ListViewHolder>() {
    class ListViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryResponse) = binding.apply {
            tvPointGet.text = history.pointAmount.toString()
            if (history.poinType == "getpoint"){
                tvPointType.setText(R.string.points_added)
            } else {
                tvPointType.setText(R.string.points_exchanged)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = history.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        history[position]?.let { holder.bind(it) }
    }

}