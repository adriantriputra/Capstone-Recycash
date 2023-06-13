package com.adrian.recycash.ui.auth.login

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.adrian.recycash.MainActivity
import com.adrian.recycash.R
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.databinding.FragmentLoginBinding
import com.adrian.recycash.helper.LoginPreferences
import com.adrian.recycash.helper.onFocusLost
import com.adrian.recycash.helper.trimmedText
import com.adrian.recycash.ui._factory.AuthViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login_datastore")

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var loginViewModel: LoginViewModel
    private val factory: AuthViewModelFactory by lazy {
        AuthViewModelFactory.getInstance()
    }

    private var dataStoreListener: DataStoreListener? = null

    private lateinit var emailTextInputLayout: TextInputLayout
    private lateinit var passwordTextInputLayout: TextInputLayout
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        loginViewModel = viewModels<LoginViewModel> { factory }.value
        val root: View = binding.root

        val backButton: ImageButton = binding.btnBack
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        emailTextInputLayout = binding.emailInputLayout
        passwordTextInputLayout = binding.passwordInputLayout
        emailEditText = binding.edEmail
        passwordEditText = binding.edPassword
        loginButton = binding.btnLogin

        loginButton.isEnabled = false

        emailEditText.onFocusLost(::validateEmail)
        passwordEditText.onFocusLost(::validatePassword)

        emailEditText.addTextChangedListener(loginTextWatcher)
        passwordEditText.addTextChangedListener(loginTextWatcher)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.btnLogin.setOnClickListener {
            login()
        }

        loginViewModel.loginResult.observe(viewLifecycleOwner){ loginResult ->
            when (loginResult){
                is Repository.LoginResult.Success -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val pref = LoginPreferences.getInstance(requireContext().dataStore)
                        pref.saveToken(loginResult.token)
                        Log.d(TAG, "Bearer: ${loginResult.token}")
                        pref.saveIsLoggedIn(true)

                        dataStoreListener?.onDataStoreReceived(requireContext().dataStore)

                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
                is Repository.LoginResult.Error -> {
                    binding.root.let { it1 ->
                        Snackbar.make(it1, loginResult.message, Snackbar.LENGTH_LONG).show()
                    }
                    Log.e(TAG, "onResponse error: ${loginResult.message}")
                }
            }
        }

        binding.btnGoogleSignIn.setOnClickListener{
            googleSignIn()
        }
    }

    private fun login(){
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()){
            binding.root.let { Snackbar.make(it, getString(R.string.empty_fields), Snackbar.LENGTH_LONG).show() }
        } else {
            loginViewModel.login(email, password)
        }
    }

    private fun googleSignIn(){
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null){
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private val loginTextWatcher = object : TextWatcher {
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
        loginButton.isEnabled = isEmailValid && isPasswordValid
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length in 8..16
    }

    interface DataStoreListener {
        fun onDataStoreReceived(dataStore: DataStore<Preferences>)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}
