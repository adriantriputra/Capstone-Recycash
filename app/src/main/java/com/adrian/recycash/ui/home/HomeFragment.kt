package com.adrian.recycash.ui.home

import android.content.Intent
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
import com.adrian.recycash.helper.loadImage
import com.adrian.recycash.ui._adapter.ArticleAdapter
import com.adrian.recycash.ui._factory.MainViewModelFactory
import com.adrian.recycash.ui.home.scan.ScanActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

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

        val imgScan = binding.imgScan
        imgScan.isClickable
        imgScan.setOnClickListener {
            val scanIntent = Intent(requireContext(), ScanActivity::class.java)
            startActivity(scanIntent)
        }

        // set up rv
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvArticles.layoutManager = layoutManager
        binding.rvArticles.setHasFixedSize(true)

        // initialize home view model
        homeViewModel = viewModels<HomeViewModel> { factory }.value
        homeViewModel.getAllArticles()

        //observer for progress bar
        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            showProgressBar(it)
        }

        // observer for article
        homeViewModel.articlesResult.observe(viewLifecycleOwner) { articlesResult ->
            when (articlesResult) {
                is Repository.ArticlesResult.Success -> {
                    setArticleList(articlesResult.articles)
                }

                is Repository.ArticlesResult.Error -> {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_msg_articles),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    Log.e(TAG, "onResponse error: ${articlesResult.message}")
                }
            }
        }

        // firebase user
        getFirebaseUser()
    }

    private fun getFirebaseUser() {
        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        with(binding) {
            tvUserGreet.text = buildString {
                append("Hi, ")
                append(firebaseUser?.displayName)
                append("!")
            }
            imgProfile.loadImage(firebaseUser?.photoUrl.toString())
        }
    }

    private fun setArticleList(articles: ArrayList<Articles>) {
        val articleAdapter = ArticleAdapter(articles)
        binding.rvArticles.adapter = articleAdapter
    }

    private fun showProgressBar(value: Boolean) {
        binding.progressBar.visibility = if (value) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
