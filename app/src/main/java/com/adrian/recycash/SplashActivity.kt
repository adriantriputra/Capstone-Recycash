package com.adrian.recycash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.adrian.recycash.helper.LoginPreferences
import com.adrian.recycash.ui._factory.MainViewModelFactory
import com.adrian.recycash.ui.auth.AuthActivity
import com.adrian.recycash.ui.home.HomeViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login_datastore")
    private lateinit var loginPreferences: LoginPreferences
    private lateinit var homeViewModel: HomeViewModel
    private val factory: MainViewModelFactory by lazy {
        MainViewModelFactory.getInstance(loginPreferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()
        loginPreferences = LoginPreferences.getInstance(this.dataStore)
        homeViewModel = viewModels<HomeViewModel> { factory }.value

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            homeViewModel.getLoginState()
        }, DELAY_MILLIS)

        homeViewModel.getLoginState().observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(mainIntent)
                finish()
            } else {
                val authIntent = Intent(this@SplashActivity, AuthActivity::class.java)
                startActivity(authIntent)
                finish()
            }
        }
    }

    companion object {
        private const val DELAY_MILLIS: Long = 2500
    }
}