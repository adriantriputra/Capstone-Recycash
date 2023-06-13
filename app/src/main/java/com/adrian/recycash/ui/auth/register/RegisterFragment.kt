package com.adrian.recycash.ui.auth.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.adrian.recycash.R
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.databinding.FragmentRegisterBinding
import com.adrian.recycash.helper.onFocusLost
import com.adrian.recycash.helper.trimmedText
import com.adrian.recycash.ui._factory.AuthViewModelFactory
import com.adrian.recycash.ui.auth.login.LoginFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var registerViewModel: RegisterViewModel
    private val factory: AuthViewModelFactory by lazy {
        AuthViewModelFactory.getInstance()
    }

    private lateinit var emailTextInputLayout: TextInputLayout
    private lateinit var passwordTextInputLayout: TextInputLayout
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: MaterialButton

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

        emailEditText = binding.edEmail
        passwordEditText = binding.edPassword
        registerButton = binding.btnRegister
        emailTextInputLayout = binding.emailInputLayout
        passwordTextInputLayout = binding.passwordInputLayout

        registerButton.isEnabled = false

        emailEditText.onFocusLost(::validateEmail)
        passwordEditText.onFocusLost(::validatePassword)

        emailEditText.addTextChangedListener(registerTextWatcher)
        passwordEditText.addTextChangedListener(registerTextWatcher)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)

        val registerButton = binding.btnRegister

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

    private val registerTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            // Not needed
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not needed
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateLoginButtonState()
        }
    }

    private fun validateEmail() {
        val email = emailEditText.trimmedText
        if (isValidEmail(email)) {
            emailTextInputLayout.error = null
        } else {
            emailTextInputLayout.error = getString(R.string.invalid_email)
        }
        updateLoginButtonState()
    }

    private fun validatePassword() {
        val password = passwordEditText.trimmedText
        if (isValidPassword(password)) {
            passwordTextInputLayout.error = null
        } else {
            passwordTextInputLayout.error = getString(R.string.invalid_password)
        }
        updateLoginButtonState()
    }

    private fun updateLoginButtonState() {
        val isEmailValid = isValidEmail(emailEditText.trimmedText)
        val isPasswordValid = isValidPassword(passwordEditText.trimmedText)
        registerButton.isEnabled = isEmailValid && isPasswordValid
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length in 8..16
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "RegisterFragment"
    }
}