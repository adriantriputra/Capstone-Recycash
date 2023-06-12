package com.adrian.recycash.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.helper.LoginPreferences
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: Repository,
    private val preferences: LoginPreferences
) : ViewModel() {

    private val _historyResult = MutableLiveData<Repository.HistoryResult>()
    val historyResult: LiveData<Repository.HistoryResult> = _historyResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchHistory(){
        _isLoading.postValue(true)
        viewModelScope.launch {
            val token = preferences.tokenFlow
                .filterNotNull()
                .first()
            val result = repository.getPointHistory(token)
            _historyResult.postValue(result)
            _isLoading.postValue(false)
        }
    }
}