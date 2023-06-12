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

class PlasticTypeViewModel(
    private val repository: Repository,
    private val preferences: LoginPreferences
) : ViewModel() {

    private val _savePointsResult = MutableLiveData<Repository.AddPointsResult>()
    val savePointsResult: LiveData<Repository.AddPointsResult> = _savePointsResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun savePoints(){
        _isLoading.postValue(true)
        viewModelScope.launch {
            val token = preferences.tokenFlow
                .filterNotNull()
                .first()
            val result = repository.savePoints(token)
            _savePointsResult.postValue(result)
            _isLoading.postValue(false)
        }
    }

}