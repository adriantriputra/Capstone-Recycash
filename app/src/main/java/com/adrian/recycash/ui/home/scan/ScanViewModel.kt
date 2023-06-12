package com.adrian.recycash.ui.home.scan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.helper.LoginPreferences
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ScanViewModel(
    private val repository: Repository,
    private val preferences: LoginPreferences
) : ViewModel() {

    private val _addPointsResult = MutableLiveData<Repository.AddPointsResult>()
    val addPointsResult: LiveData<Repository.AddPointsResult> = _addPointsResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun addPoints(){
        _isLoading.postValue(true)
        viewModelScope.launch {
            val token = preferences.tokenFlow
                .filterNotNull()
                .first()
            val result = repository.addPoints(token)
            _addPointsResult.postValue(result)
            _isLoading.postValue(false)
        }
    }
}