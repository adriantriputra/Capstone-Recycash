package com.adrian.recycash.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adrian.recycash.data.di.Repository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: Repository,
) : ViewModel() {

    private val _articlesResult = MutableLiveData<Repository.ArticlesResult>()
    val articlesResult: LiveData<Repository.ArticlesResult> = _articlesResult

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
}