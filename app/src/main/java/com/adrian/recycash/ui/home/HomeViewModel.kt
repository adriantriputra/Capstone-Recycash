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

    fun getAllArticles() {
        viewModelScope.launch {
            val result = repository.getAllArticles()
            _articlesResult.postValue(result)
        }
    }
}