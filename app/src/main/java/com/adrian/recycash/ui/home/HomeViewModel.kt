package com.adrian.recycash.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.adrian.recycash.data.di.Repository
import com.adrian.recycash.helper.LoginPreferences
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: Repository,
    private val preferences: LoginPreferences
) : ViewModel() {

    private val _articlesResult = MutableLiveData<Repository.ArticlesResult>()
    val articlesResult: LiveData<Repository.ArticlesResult> = _articlesResult

    private val _userResult = MutableLiveData<Repository.UserResult>()
    val userResult: LiveData<Repository.UserResult> = _userResult

    private val _pointsResult = MutableLiveData<Repository.PointsResult>()
    val pointsResult: LiveData<Repository.PointsResult> = _pointsResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun getAllArticles() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            val result = repository.getAllArticles()
            _articlesResult.postValue(result)
            _isLoading.postValue(false)
        }
    }

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

    fun getTotalPoints() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            val token: String = preferences.tokenFlow
                .filterNotNull()
                .first()
            val result = repository.getTotalPoints(token)
            _pointsResult.postValue(result)
            _isLoading.postValue(false)
        }
    }

    fun getLoginState(): LiveData<Boolean> {
        return preferences.isLoggedInFlow.asLiveData()
    }
}