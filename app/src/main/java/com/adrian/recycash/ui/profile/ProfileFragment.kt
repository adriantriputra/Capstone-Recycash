package com.adrian.recycash.ui.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.adrian.recycash.R
import com.adrian.recycash.databinding.FragmentProfileBinding
import com.adrian.recycash.helper.loadImage
import com.adrian.recycash.ui.auth.AuthActivity
import com.adrian.recycash.ui.profile.about.AboutActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var auth: FirebaseAuth

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.cvChangePassword.setOnClickListener {  }
        binding.cvChangeLanguage.setOnClickListener {
            val settingsIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(settingsIntent)
        }
        binding.cvAbout.setOnClickListener {
            val aboutIntent = Intent(requireContext(), AboutActivity::class.java)
            startActivity(aboutIntent)
        }
        binding.btnLogout.setOnClickListener {
            logOut()
        }

        setUpProfileData()
    }

    private fun setUpProfileData(){
        val firebaseUser = auth.currentUser
        val notFound = getString(R.string.msg_phone_number_notfound)
        with (binding) {
            imgProfile.loadImage(firebaseUser?.photoUrl.toString())
            tvProfileName.text = firebaseUser?.displayName
            tvProfileEmail.text = firebaseUser?.email
            if (firebaseUser?.phoneNumber.isNullOrEmpty()) {
                tvPhoneNumber.text = notFound
            } else {
                tvPhoneNumber.text = firebaseUser?.phoneNumber
            }
            Log.d("ProfileFragment", "response: ${firebaseUser?.phoneNumber.toString()}")
        }
    }

    private fun logOut(){
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.logout_confirmation))
            .setPositiveButton(getString(R.string.logout_yes)) { _, _ ->
                // If user clicked yes, clear token and session then intent to auth activity
                val logoutIntent = Intent(requireContext(), AuthActivity::class.java)
                auth.signOut()
                startActivity(logoutIntent)
                requireActivity().finish()
            }
            .setNegativeButton(getString(R.string.logout_no)) { _, _ ->
                // Do nothing
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}