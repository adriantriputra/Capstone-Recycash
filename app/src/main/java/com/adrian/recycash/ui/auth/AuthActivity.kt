package com.adrian.recycash.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.adrian.recycash.MainActivity
import com.adrian.recycash.R
import com.adrian.recycash.databinding.ActivityAuthBinding
import com.adrian.recycash.ui.auth.onboarding.OnboardingFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth

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

        auth = Firebase.auth
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null){
            startActivity(Intent(this@AuthActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
}