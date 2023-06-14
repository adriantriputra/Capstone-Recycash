package com.adrian.recycash.ui.home.scan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.adrian.recycash.R
import com.adrian.recycash.databinding.ActivityScanOkBinding

@Suppress("DEPRECATION")
class ScanOkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanOkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanOkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        when (intent.extras?.getInt(SELECTED_EXTRA)) {
            0 -> binding.tvGetPoint.text = getString(R.string.point_1)
            1 -> binding.tvGetPoint.text = getString(R.string.point_2)
            2 -> binding.tvGetPoint.text = getString(R.string.point_3)
            3 -> binding.tvGetPoint.text = getString(R.string.point_4)
            4 -> binding.tvGetPoint.text = getString(R.string.point_5)
            5 -> binding.tvGetPoint.text = getString(R.string.point_6)
            6 -> binding.tvGetPoint.text = getString(R.string.point_7)
        }

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

    @Deprecated("Deprecated in Java", ReplaceWith("finish()"))
    override fun onBackPressed() {
        finish()
    }

    companion object {
        private const val SELECTED_EXTRA = "SELECTED_EXTRA"
    }
}