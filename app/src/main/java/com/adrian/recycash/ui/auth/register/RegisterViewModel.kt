package com.adrian.recycash.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrian.recycash.data.di.Repository
import kotlinx.coroutines.launch

class RegisterViewModel (private val repository: Repository) : ViewModel() {

    private val _registerResult = MutableLiveData<Repository.RegisterResult>()
    val registerResult: LiveData<Repository.RegisterResult> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(name: String, email: String, phoneNumber: String, password: String){
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.register(name, email, phoneNumber, password)
            _registerResult.postValue(result)
            _isLoading.postValue(false)
        }
    }

}