package com.adrian.recycash.data.di

import com.adrian.recycash.data.remote.retrofit.ArticlesApiConfig
import com.adrian.recycash.data.remote.retrofit.UserApiConfig

object Injection {
    fun provideRepository(): Repository {
        val articlesApiConfig = ArticlesApiConfig
        val userApiConfig = UserApiConfig
        return Repository.getInstance(articlesApiConfig, userApiConfig)
    }
}