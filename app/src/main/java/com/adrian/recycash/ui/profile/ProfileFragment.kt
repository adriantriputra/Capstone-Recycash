package com.adrian.recycash.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.adrian.recycash.R
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.data.remote.response.UserResponse
import com.adrian.recycash.databinding.FragmentProfileBinding
import com.adrian.recycash.helper.LoginPreferences
import com.adrian.recycash.helper.loadImage
import com.adrian.recycash.ui._factory.MainViewModelFactory
import com.adrian.recycash.ui.auth.AuthActivity
import com.adrian.recycash.ui.profile.about.AboutActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login_datastore")

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    private lateinit var loginPreferences: LoginPreferences
    private lateinit var profileViewModel: ProfileViewModel
    private val factory: MainViewModelFactory by lazy {
        MainViewModelFactory.getInstance(loginPreferences)
    }

    private var isGetUserCalled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginPreferences = LoginPreferences.getInstance(requireContext().dataStore)
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

        // initialize view model
        profileViewModel = viewModels<ProfileViewModel> { factory }.value

        //observer for progress bar
        profileViewModel.isLoading.observe(viewLifecycleOwner) {
            showProgressBar(it)
        }

        profileViewModel.userResult.observe(viewLifecycleOwner) { userResult ->
            when (userResult) {
                is Repository.UserResult.Success -> {
                    updateUI(userResult.response)
                    isGetUserCalled = true
                }

                is Repository.UserResult.Error -> {
                    Snackbar.make(binding.root, "Failed to get user", Snackbar.LENGTH_SHORT).show()
                    Log.d(TAG, "onRespose error: ${userResult.message}")
                }
            }
        }

        if (!isGetUserCalled){
            setUpProfileData()
        }
    }

    private fun updateUI(response: UserResponse) {
        val notFound = getString(R.string.msg_phone_number_notfound)
        with(binding) {
            tvProfileName.text = response.name
            tvProfileEmail.text = response.email
            if (response.phoneNumber.isNullOrEmpty()) {
                tvPhoneNumber.text = notFound
            } else {
                tvPhoneNumber.text = response.phoneNumber
            }
        }
    }

    private fun setUpProfileData(){
        val firebaseUser = auth.currentUser
        val notFound = getString(R.string.msg_phone_number_notfound)

        if (firebaseUser != null) {
            with (binding) {
                if (firebaseUser.photoUrl.toString().isNotEmpty()){
                    imgProfile.loadImage(firebaseUser.photoUrl.toString())
                }
                tvProfileName.text = firebaseUser.displayName
                tvProfileEmail.text = firebaseUser.email
                if (firebaseUser.phoneNumber.isNullOrEmpty()) {
                    tvPhoneNumber.text = notFound
                } else {
                    tvPhoneNumber.text = firebaseUser.phoneNumber
                }
                Log.d("ProfileFragment", "response: ${firebaseUser.phoneNumber.toString()}")
            }
        } else {
            profileViewModel.getUser()
        }
    }

    private fun logOut(){
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.logout_confirmation))
            .setPositiveButton(getString(R.string.logout_yes)) { _, _ ->
                // If user clicked yes, clear token and session then intent to auth activity
                val logoutIntent = Intent(requireContext(), AuthActivity::class.java)
                auth.signOut()
                profileViewModel.clearToken()
                profileViewModel.isLoggedOut()
                startActivity(logoutIntent)
                requireActivity().finish()
            }
            .setNegativeButton(getString(R.string.logout_no)) { _, _ ->
                // Do nothing
            }.show()
    }

    private fun showProgressBar(value: Boolean) {
        binding.progressBar.visibility = if (value) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}