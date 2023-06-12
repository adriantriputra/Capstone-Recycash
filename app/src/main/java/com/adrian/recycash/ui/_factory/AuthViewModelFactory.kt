package com.adrian.recycash.ui._factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adrian.recycash.data.di.Injection
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.ui.auth.login.LoginViewModel
import com.adrian.recycash.ui.auth.register.RegisterViewModel

class AuthViewModelFactory private constructor(
    private val repository: Repository,
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        }
        if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(repository) as T
        }
        throw java.lang.IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: AuthViewModelFactory? = null
        fun getInstance(): AuthViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: AuthViewModelFactory(
                    Injection.provideRepository(),
                )
            }.also { instance = it }
    }
}