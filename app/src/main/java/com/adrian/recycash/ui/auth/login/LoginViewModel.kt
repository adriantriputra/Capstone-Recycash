package com.adrian.recycash.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrian.recycash.data.di.Repository
import kotlinx.coroutines.launch

class LoginViewModel (private val repository: Repository) : ViewModel() {
    private val _loginResult = MutableLiveData<Repository.LoginResult>()
    val loginResult: LiveData<Repository.LoginResult> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String){
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.login(email, password)
            _loginResult.postValue(result)
            _isLoading.postValue(false)
        }
    }
}