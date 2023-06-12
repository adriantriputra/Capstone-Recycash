package com.adrian.recycash.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.helper.LoginPreferences
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: Repository,
    private val preferences: LoginPreferences
) : ViewModel() {

    private val _userResult = MutableLiveData<Repository.UserResult>()
    val userResult: LiveData<Repository.UserResult> = _userResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getUser() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            val token: String = preferences.tokenFlow
                .filterNotNull()
                .first()
            val result = repository.getUser(token)
            _userResult.postValue(result)
            _isLoading.postValue(false)
        }
    }

    fun clearToken() {
        viewModelScope.launch {
            preferences.clearToken()
        }
    }

    fun isLoggedOut() {
        viewModelScope.launch {
            preferences.saveIsLoggedIn(false)
        }
    }
}