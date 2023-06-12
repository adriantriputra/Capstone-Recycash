package com.adrian.recycash.ui.history

import android.content.Context
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
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.data.remote.response.HistoryResponse
import com.adrian.recycash.databinding.FragmentHistoryBinding
import com.adrian.recycash.helper.LoginPreferences
import com.adrian.recycash.ui._adapter.HistoryAdapter
import com.adrian.recycash.ui._factory.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login_datastore")

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginPreferences: LoginPreferences
    private lateinit var historyViewModel: HistoryViewModel
    private val factory: MainViewModelFactory by lazy {
        MainViewModelFactory.getInstance(loginPreferences)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginPreferences = LoginPreferences.getInstance(requireContext().dataStore)
        historyViewModel = viewModels<HistoryViewModel> { factory }.value

        historyViewModel.historyResult.observe(viewLifecycleOwner) { historyResult ->
            when (historyResult) {
                is Repository.HistoryResult.Success -> {
                    if (historyResult.history.isEmpty()){
                        binding.emptyLayout.visibility = View.VISIBLE
                        binding.historyLayout.visibility = View.GONE
                    } else {
                        setHistory(historyResult.history)
                        binding.historyLayout.visibility = View.VISIBLE
                        binding.emptyLayout.visibility = View.GONE
                    }
                }
                is Repository.HistoryResult.Error -> {
                    Snackbar.make(binding.root, "Failed to fetch history", Snackbar.LENGTH_SHORT).show()
                    Log.d(TAG, "onResponse error: ${historyResult.message}")
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.historyLayout.visibility = View.GONE
                }
            }
        }

        // observer for loading
        historyViewModel.isLoading.observe(viewLifecycleOwner) {
            showProgressBar(it)
        }

        // set up rv
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.layoutManager = layoutManager
        binding.rvHistory.setHasFixedSize(true)

        historyViewModel.fetchHistory()
    }

    private fun showProgressBar(value: Boolean) {
        binding.progressBar.visibility = if (value) View.VISIBLE else View.GONE
    }

    private fun setHistory(response: List<HistoryResponse?>){
        Log.d(TAG, "response: $response")
        val historyAdapter = HistoryAdapter(response)
        Log.d(TAG, "itemCount: ${historyAdapter.itemCount}")
        binding.rvHistory.adapter = historyAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "HistoryFragment"
    }
}