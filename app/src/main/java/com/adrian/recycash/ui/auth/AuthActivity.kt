package com.adrian.recycash.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.adrian.recycash.R
import com.adrian.recycash.databinding.ActivityAuthBinding
import com.adrian.recycash.ui.auth.onboarding.OnboardingFragment

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentManager: FragmentManager = supportFragmentManager
        val onboardingFragment = OnboardingFragment()
        val fragment: Fragment? =
            fragmentManager.findFragmentByTag(OnboardingFragment::class.java.simpleName)

        if (fragment !is OnboardingFragment){
            Log.d("Recycash", "Fragment name: " + OnboardingFragment::class.java.simpleName)
            fragmentManager
                .beginTransaction()
                .add(R.id.frame_container, onboardingFragment, OnboardingFragment::class.java.simpleName)
                .commit()
        }
    }
}