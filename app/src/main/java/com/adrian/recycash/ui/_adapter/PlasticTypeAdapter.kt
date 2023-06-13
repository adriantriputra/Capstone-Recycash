package com.adrian.recycash.ui._adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.adrian.recycash.data.remote.response.PlasticType
import com.adrian.recycash.databinding.ItemPlasticTypesBinding

class PlasticTypeAdapter(private val plasticTypes: List<PlasticType>) :
    RecyclerView.Adapter<PlasticTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlasticTypesBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plasticType = plasticTypes[position]
        holder.bind(plasticType)
    }

    override fun getItemCount(): Int = plasticTypes.size

    inner class ViewHolder(private val binding: ItemPlasticTypesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plasticType: PlasticType) {
            binding.apply {
                val drawable = ContextCompat.getDrawable(itemView.context, plasticType.imageResInt)
                imgItem.setImageDrawable(drawable)

                tvPlasticTypeTitle.text = plasticType.title
                tvPlasticTypeDesc.text = plasticType.description
            }
        }
    }
}
