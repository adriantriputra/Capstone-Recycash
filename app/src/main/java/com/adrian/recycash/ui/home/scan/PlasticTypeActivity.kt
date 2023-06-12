package com.adrian.recycash.ui.home.scan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.adrian.recycash.databinding.ActivityPlasticTypeBinding

class PlasticTypeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlasticTypeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlasticTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clearButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Do you want to cancel?")
                .setMessage("You won't get any points if you leave at this phase.")
                .setPositiveButton("Yes") { _, _ ->
                    @Suppress("DEPRECATION")
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

        binding.btnContinue.setOnClickListener {
            val plasticType = binding.spinner.selectedItemPosition
            val intent = Intent(this@PlasticTypeActivity, ScanOkActivity::class.java)
            intent.putExtra(EXTRA_TYPE, plasticType)
            Log.d(TAG, "selected: $plasticType")
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val TAG = "PlasticTypeActivity"
        private const val EXTRA_TYPE = "PlasticType"
    }

}