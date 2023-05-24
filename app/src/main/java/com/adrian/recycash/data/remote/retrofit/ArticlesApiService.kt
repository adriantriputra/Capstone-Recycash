package com.adrian.recycash.data.remote.retrofit

import com.adrian.recycash.data.remote.response.ArticlesResponse
import retrofit2.Call
import retrofit2.http.GET

interface ArticlesApiService {
    @GET("v2/everything?q=+\"plastic bottle\"&pageSize=10")
    fun getArticles(): Call<ArticlesResponse>
}