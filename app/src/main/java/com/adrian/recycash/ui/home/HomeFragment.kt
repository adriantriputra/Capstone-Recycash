package com.adrian.recycash.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrian.recycash.R
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.data.remote.response.Articles
import com.adrian.recycash.data.remote.response.PointsResponse
import com.adrian.recycash.data.remote.response.UserResponse
import com.adrian.recycash.databinding.FragmentHomeBinding
import com.adrian.recycash.helper.LoginPreferences
import com.adrian.recycash.helper.loadImage
import com.adrian.recycash.ui._adapter.ArticleAdapter
import com.adrian.recycash.ui._factory.MainViewModelFactory
import com.adrian.recycash.ui.home.scan.ScanActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login_datastore")

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var loginPreferences: LoginPreferences
    private lateinit var homeViewModel: HomeViewModel
    private val factory: MainViewModelFactory by lazy {
        MainViewModelFactory.getInstance(loginPreferences)
    }

    private var isGetUserCalled = false

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

        loginPreferences = LoginPreferences.getInstance(requireContext().dataStore)

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

        // observer for total points
        homeViewModel.pointsResult.observe(viewLifecycleOwner) { pointsResult ->
            when (pointsResult) {
                is Repository.PointsResult.Success -> {
                    if (pointsResult.points.totalPoint.toString() == "null"){
                        // Do nothing
                    }
                    else {
                        setPoints(pointsResult.points)
                    }
                }
                is Repository.PointsResult.Error -> {
                    Snackbar.make(binding.root, "Failed to fetch points", Snackbar.LENGTH_SHORT).show()
                    Log.d(TAG, "onResponse error: ${pointsResult.message}")
                }
            }
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

        // get user
        homeViewModel.userResult.observe(viewLifecycleOwner) { userResult ->
            when (userResult) {
                is Repository.UserResult.Success -> {
                    updateUI(userResult.user)
                    isGetUserCalled = true
                }

                is Repository.UserResult.Error -> {
                    Snackbar.make(binding.root, "Failed to get user", Snackbar.LENGTH_SHORT).show()
                    Log.d(TAG, "onResponse error: ${userResult.message}")
                }
            }
        }

        if (!isGetUserCalled){
            getUserInfo()
        }
    }

    private fun setPoints(points: PointsResponse) {
        with (binding) {
            tvPointsCount.text = points.totalPoint.toString()
        }
    }

    private fun updateUI(response: UserResponse) {
        with(binding) {
            tvUserGreet.text = buildString {
                append("Hi, ")
                append(response.name)
                append("!")
            }
        }
    }

    private fun getUserInfo() {
        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        if (firebaseUser != null) {
            with(binding) {
                tvUserGreet.text = buildString {
                    append("Hi, ")
                    append(firebaseUser.displayName)
                    append("!")
                }
                if (firebaseUser.photoUrl.toString().isNotEmpty()){
                    imgProfile.loadImage(firebaseUser.photoUrl.toString())
                }
            }
        } else {
            homeViewModel.getUser()
            homeViewModel.getTotalPoints()
        }
    }

    private fun setArticleList(articles: ArrayList<Articles>) {
        val articleAdapter = ArticleAdapter(articles)
        binding.rvArticles.adapter = articleAdapter
    }

    private fun showProgressBar(value: Boolean) {
        binding.progressBar.visibility = if (value) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()

        homeViewModel.getTotalPoints()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}
