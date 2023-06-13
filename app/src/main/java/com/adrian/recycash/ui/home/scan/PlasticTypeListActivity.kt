package com.adrian.recycash.ui.home.scan

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrian.recycash.data.local.PlasticTypeData
import com.adrian.recycash.databinding.ActivityPlasticTypeListBinding
import com.adrian.recycash.ui._adapter.PlasticTypeAdapter

class PlasticTypeListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlasticTypeListBinding

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlasticTypeListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton: ImageButton = binding.btnBack
        backButton.setOnClickListener {
            onBackPressed()
        }

        val plasticTypes = PlasticTypeData.getPlasticTypes(this)

        val layoutManager = LinearLayoutManager(this)
        binding.rvPlasticTypes.layoutManager = layoutManager
        binding.rvPlasticTypes.setHasFixedSize(true)

        val adapter = PlasticTypeAdapter(plasticTypes)
        binding.rvPlasticTypes.adapter = adapter
    }
}