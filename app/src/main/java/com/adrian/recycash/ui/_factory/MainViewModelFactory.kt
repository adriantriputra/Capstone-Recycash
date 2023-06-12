package com.adrian.recycash.ui._factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adrian.recycash.data.di.Injection
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.helper.LoginPreferences
import com.adrian.recycash.ui.home.HomeViewModel
import com.adrian.recycash.ui.home.scan.PlasticTypeViewModel
import com.adrian.recycash.ui.home.scan.ScanViewModel
import com.adrian.recycash.ui.profile.ProfileViewModel

class MainViewModelFactory private constructor(
    private val repository: Repository,
    private val preferences: LoginPreferences
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository, preferences) as T
        }
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(repository, preferences) as T
        }
        if (modelClass.isAssignableFrom(ScanViewModel::class.java)) {
            return ScanViewModel(repository, preferences) as T
        }
        if (modelClass.isAssignableFrom(PlasticTypeViewModel::class.java)) {
            return PlasticTypeViewModel(repository, preferences) as T
        }
        throw java.lang.IllegalArgumentException("Unknown ViewModel Class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: MainViewModelFactory? = null
        fun getInstance(preferences: LoginPreferences): MainViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: MainViewModelFactory(
                    Injection.provideRepository(),
                    preferences
                )
            }.also { instance = it }
    }
}