package com.adrian.recycash.ui.auth.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.adrian.recycash.R
import com.adrian.recycash.databinding.FragmentOnboardingBinding
import com.adrian.recycash.ui.auth.login.LoginFragment
import com.adrian.recycash.ui.auth.register.RegisterFragment
import com.google.android.material.button.MaterialButton

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go to login fragment
        val loginButton: MaterialButton = binding.btnLogin
        loginButton.setOnClickListener {
            val loginFragment = LoginFragment()
            val fragmentManager: FragmentManager = parentFragmentManager

            fragmentManager.beginTransaction().apply {
                replace(R.id.frame_container, loginFragment, LoginFragment::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }

        // Go to register fragment
        val registerButton: MaterialButton = binding.btnRegister
        registerButton.setOnClickListener {
            val registerFragment = RegisterFragment()
            val fragmentManager: FragmentManager = parentFragmentManager

            fragmentManager.beginTransaction().apply {
                replace(R.id.frame_container, registerFragment, RegisterFragment::class.java.simpleName)
                addToBackStack(null)
                commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}