package com.adrian.recycash.ui.home.scan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adrian.recycash.databinding.ActivityScanOkBinding

@Suppress("DEPRECATION")
class ScanOkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanOkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanOkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnHome = binding.btnHome
        val btnScan = binding.btnScan

        btnHome.setOnClickListener {
            onBackPressed()
        }
        btnScan.setOnClickListener {
            val scanIntent = Intent(this@ScanOkActivity, ScanActivity::class.java)
            startActivity(scanIntent)
            finish()
        }
    }
}