package com.adrian.recycash.ui._adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adrian.recycash.data.remote.response.Articles
import com.adrian.recycash.databinding.ItemArticlesBinding
import com.bumptech.glide.Glide

class ArticleAdapter(
    private val articles: ArrayList<Articles>
) : RecyclerView.Adapter<ArticleAdapter.ListViewHolder>() {
    class ListViewHolder(private val binding: ItemArticlesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(articles: Articles) = binding.apply {
            Glide.with(itemView.context)
                .load(articles.urlToImage)
                .into(binding.imgItem)
            tvArticleTitle.text = articles.title
            tvArticleDesc.text = articles.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemArticlesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(articles[position])
    }
}