package com.adrian.recycash.ui.home.scan

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
                .setTitle("Do you want to cancel?")
                .setMessage("You won't get any points if you leave at this phase.")
                .setPositiveButton("Yes") { _, _ ->
                    onBackPressed()
                }
                .setNegativeButton("No") { _, _ ->
                    // Do nothing
                }.show()
        }

        binding.tvPlasticType.setOnClickListener {
            // Do something
        }

        // Set up the spinner options
        val options = listOf("Jenis 1: PET", "Jenis 2: HDPE", "Jenis 3: PC")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

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
            plasticTypeViewModel.savePoints()
        }
    }

    private fun showProgressBar(value: Boolean) {
        binding.progressBar.visibility = if (value) View.VISIBLE else View.GONE
    }

    companion object {
        private const val TAG = "PlasticTypeActivity"
    }

}