package com.adrian.recycash.data.di

import com.adrian.recycash.data.remote.retrofit.ArticlesApiConfig

object Injection {
    fun provideRepository(): Repository {
        val articlesApiConfig = ArticlesApiConfig
        return Repository.getInstance(articlesApiConfig)
    }
}