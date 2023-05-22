package com.adrian.recycash.ui.exchange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExchangeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Exchange Point Fragment"
    }
    val text: LiveData<String> = _text
}