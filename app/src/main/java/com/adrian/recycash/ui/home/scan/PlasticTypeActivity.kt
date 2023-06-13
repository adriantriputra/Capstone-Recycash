package com.adrian.recycash.ui.home.scan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.adrian.recycash.R
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.data.local.PlasticTypeData
import com.adrian.recycash.databinding.ActivityPlasticTypeBinding
import com.adrian.recycash.helper.LoginPreferences
import com.adrian.recycash.ui._factory.MainViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login_datastore")

@Suppress("DEPRECATION")
class PlasticTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlasticTypeBinding
    private lateinit var loginPreferences: LoginPreferences
    private lateinit var plasticTypeViewModel: PlasticTypeViewModel
    private val factory: MainViewModelFactory by lazy {
        MainViewModelFactory.getInstance(loginPreferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlasticTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginPreferences = LoginPreferences.getInstance(this.dataStore)

        // initialize viewModel
        plasticTypeViewModel = viewModels<PlasticTypeViewModel> { factory }.value

        binding.clearButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.cancellation_confirm))
                .setMessage(getString(R.string.cancellation_message))
                .setPositiveButton(getString(R.string.cancellation_yes)) { _, _ ->
                    onBackPressed()
                }
                .setNegativeButton(getString(R.string.cancellation_no)) { _, _ ->
                    // Do nothing
                }.show()
        }

        binding.tvPlasticType.setOnClickListener {
            val intent = Intent(this, PlasticTypeListActivity::class.java)
            startActivity(intent)
        }

        // Set up the spinner options
        val options = PlasticTypeData.options(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        plasticTypeViewModel.addPointsResult.observe(this) { addPointsResult ->
            when (addPointsResult) {
                is Repository.AddPointsResult.Success -> {
                    // Do nothing
                }
                is Repository.AddPointsResult.Error -> {
                    Log.d(TAG, "onResponse error: ${addPointsResult.message}")
                    Toast.makeText(this, getString(R.string.failed_fetch_points), Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }
        }

        plasticTypeViewModel.savePointsResult.observe(this) { savePointsResult ->
            when (savePointsResult) {
                is Repository.AddPointsResult.Success -> {
                    val intent = Intent(this@PlasticTypeActivity, ScanOkActivity::class.java)
                    Toast.makeText(this, getString(R.string.points_success), Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    finish()
                }
                is Repository.AddPointsResult.Error -> {
                    Log.d(TAG, "onResponse error: ${savePointsResult.message}")
                    Toast.makeText(this, getString(R.string.failed_fetch_points), Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }
            }
        }

        //observer for progress bar
        plasticTypeViewModel.isLoading.observe(this) {
            showProgressBar(it)
        }

        binding.btnContinue.setOnClickListener {
            plasticTypeViewModel.addPoints()

            // Delay the function call using a Handler
            Handler().postDelayed({
                plasticTypeViewModel.savePoints()
            }, DELAY_MILLIS)
        }
    }

    private fun showProgressBar(value: Boolean) {
        binding.progressBar.visibility = if (value) View.VISIBLE else View.GONE
    }

    companion object {
        private const val DELAY_MILLIS = 1500L
        private const val TAG = "PlasticTypeActivity"
    }

}