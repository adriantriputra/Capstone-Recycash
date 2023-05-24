package com.adrian.recycash.data.di

import android.util.Log
import com.adrian.recycash.data.remote.response.Articles
import com.adrian.recycash.data.remote.response.ArticlesResponse
import com.adrian.recycash.data.remote.retrofit.ArticlesApiConfig
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Repository private constructor(
    private val articlesApiConfig: ArticlesApiConfig,
) {
    suspend fun getAllArticles(): ArticlesResult {
        val client = articlesApiConfig.getApiService.getArticles()
        return suspendCoroutine { continuation ->
            client.enqueue(object : Callback<ArticlesResponse> {
                override fun onResponse(
                    call: Call<ArticlesResponse>,
                    response: Response<ArticlesResponse>
                ) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val articles = responseBody.articles
                        continuation.resume(ArticlesResult.Success(articles))
                    } else {
                        try {
                            val errorResponse = Gson().fromJson(
                                response.errorBody()?.charStream(),
                                ArticlesResponse::class.java
                            )
                            val errorMessage = errorResponse?.status ?: "Unknown error occurred"
                            Log.e(TAG, "onResponse error: $errorMessage")
                            continuation.resume(ArticlesResult.Error(errorMessage))
                        } catch (e: Exception) {
                            val errorMessage = "Unknown error occurred"
                            Log.e(TAG, "onFailure: ${response.message()}")
                            continuation.resume(ArticlesResult.Error(errorMessage))
                        }
                    }
                }

                override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
                    val errorMessage = t.message.toString()
                    Log.e(TAG, "onFailure: $errorMessage")
                    continuation.resume(ArticlesResult.Error(errorMessage))
                }
            })
        }
    }

    sealed class ArticlesResult {
        data class Success(val articles: ArrayList<Articles>) : ArticlesResult()
        data class Error(val message: String) : ArticlesResult()
    }

    companion object {
        private const val TAG = "Repository"

        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            articlesApiConfig: ArticlesApiConfig,
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(articlesApiConfig)
            }.also { instance = it }
    }
}