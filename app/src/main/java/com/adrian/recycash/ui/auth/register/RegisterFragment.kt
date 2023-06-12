package com.adrian.recycash.ui.auth.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.adrian.recycash.R
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.databinding.FragmentRegisterBinding
import com.adrian.recycash.ui._factory.AuthViewModelFactory
import com.adrian.recycash.ui.auth.login.LoginFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var registerViewModel: RegisterViewModel
    private val factory: AuthViewModelFactory by lazy {
        AuthViewModelFactory.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        registerViewModel = viewModels<RegisterViewModel> { factory }.value
        val root: View = binding.root

        val backButton: ImageButton = binding.btnBack
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)

        val registerButton = binding.btnRegister
//        val edName = binding.edName
//        val edEmail = binding.edEmail
//        val edPhone = binding.edPhone
//        val edPassword = binding.edPassword
//
//        val textWatcher = object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            override fun afterTextChanged(s: Editable?) {
//                registerButton.isEnabled = !edEmail.text.isNullOrEmpty() &&
//                        edEmail.error.isNullOrEmpty() &&
//                        !edPassword.text.isNullOrEmpty() &&
//                        edPassword.error.isNullOrEmpty()
//            }
//        }
//        edEmail.addTextChangedListener(textWatcher)
//        edPassword.addTextChangedListener(textWatcher)

        registerButton.setOnClickListener {
            register()
        }

        registerViewModel.registerResult.observe(viewLifecycleOwner) { registerResult ->
            when (registerResult) {
                is Repository.RegisterResult.Success -> {
                    Snackbar.make(requireView(), "Account created successfully", Snackbar.LENGTH_SHORT).show()
                    viewLifecycleOwner.lifecycleScope.launch {
                        val loginFragment = LoginFragment()
                        val fragmentManager = parentFragmentManager
                        fragmentManager.beginTransaction().apply {
                            replace(R.id.frame_container, loginFragment, LoginFragment::class.java.simpleName)
                            addToBackStack(null)
                            commit()
                        }
                    }
                }
                is Repository.RegisterResult.Error -> {
                    view.let { it1 -> Snackbar.make(it1, registerResult.message, Snackbar.LENGTH_LONG).show() }
                    Log.e(TAG, "onResponse error: ${registerResult.message}")
                }
            }
        }
    }

    private fun register() {
        // Set up view model & observe
        val name = binding.edName.text.toString().trim()
        val email = binding.edEmail.text.toString().trim()
        val phoneNumber = binding.edPhone.text.toString().trim()
        val password = binding.edPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
            view?.let { Snackbar.make(it, getString(R.string.empty_fields), Snackbar.LENGTH_LONG).show() }
        } else {
            registerViewModel.register(name, email, phoneNumber, password)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "RegisterFragment"
    }
}