package com.adrian.recycash.data.remote.retrofit

import com.adrian.recycash.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ArticlesApiConfig {

    private const val mySecretKey = BuildConfig.API_TOKEN
    private const val BASE_URL = "https://newsapi.org/"

    val getApiService: ArticlesApiService by lazy {
        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestHeaders = req.newBuilder()
                .addHeader("x-api-key", mySecretKey)
                .build()
            chain.proceed(requestHeaders)
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        retrofit.create(ArticlesApiService::class.java)
    }

}