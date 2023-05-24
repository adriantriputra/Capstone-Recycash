package com.adrian.recycash.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrian.recycash.R
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.data.remote.response.Articles
import com.adrian.recycash.databinding.FragmentHomeBinding
import com.adrian.recycash.ui._adapter.ArticleAdapter
import com.adrian.recycash.ui._factory.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private val factory: MainViewModelFactory by lazy {
        MainViewModelFactory.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set up rv
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvArticles.layoutManager = layoutManager
        binding.rvArticles.setHasFixedSize(true)

        // initialize home view model
        homeViewModel = viewModels<HomeViewModel> { factory }.value
        homeViewModel.getAllArticles()

        // observe
        homeViewModel.articlesResult.observe(viewLifecycleOwner){ articlesResult ->
            when (articlesResult) {
                is Repository.ArticlesResult.Success -> {
                    setArticleList(articlesResult.articles)
                }
                is Repository.ArticlesResult.Error -> {
                    Snackbar.make(binding.root, getString(R.string.error_msg_articles), Snackbar.LENGTH_SHORT).show()
                    Log.e(TAG, "onResponse error: ${articlesResult.message}")
                }
            }
        }
    }

    private fun setArticleList(articles: ArrayList<Articles>) {
        val articleAdapter = ArticleAdapter(articles)
        binding.rvArticles.adapter = articleAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        private const val TAG = "HomeFragment"
    }
}
