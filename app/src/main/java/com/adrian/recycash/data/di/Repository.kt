package com.adrian.recycash.data.di

import android.util.Log
import com.adrian.recycash.data.remote.response.AddPointsResponse
import com.adrian.recycash.data.remote.response.Articles
import com.adrian.recycash.data.remote.response.ArticlesResponse
import com.adrian.recycash.data.remote.response.HistoryResponse
import com.adrian.recycash.data.remote.response.LoginRequest
import com.adrian.recycash.data.remote.response.LoginResponse
import com.adrian.recycash.data.remote.response.PointsResponse
import com.adrian.recycash.data.remote.response.RegisterRequest
import com.adrian.recycash.data.remote.response.RegisterResponse
import com.adrian.recycash.data.remote.response.UserResponse
import com.adrian.recycash.data.remote.retrofit.ArticlesApiConfig
import com.adrian.recycash.data.remote.retrofit.UserApiConfig
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Repository private constructor(
    private val articlesApiConfig: ArticlesApiConfig,
    private val userApiConfig: UserApiConfig,
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

    suspend fun register(
        name: String,
        email: String,
        phoneNumber: String,
        password: String
    ): RegisterResult {
        val registerRequest = RegisterRequest(name, email, phoneNumber, password)
        val client = userApiConfig.getApiService.register(registerRequest)

        return suspendCoroutine { continuation ->
            client.enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val successMessage = responseBody.message
                        continuation.resume(RegisterResult.Success(successMessage))
                    } else {
                        try {
                            val errorResponse = Gson().fromJson(
                                response.errorBody()?.charStream(),
                                RegisterResponse::class.java
                            )
                            val errorMessage = errorResponse?.message ?: "Unknown error occurred"
                            Log.e(TAG, "onResponse error: $errorMessage")
                            continuation.resume(RegisterResult.Error(errorMessage))
                        } catch (e: Exception) {
                            val errorMessage = "Unknown error occurred"
                            Log.e(TAG, "onFailure: ${response.message()}")
                            continuation.resume(RegisterResult.Error(errorMessage))
                        }
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    val errorMessage = t.message.toString()
                    Log.e(TAG, "onFailure: $errorMessage")
                    continuation.resume(RegisterResult.Error(errorMessage))
                }
            })
        }
    }

    suspend fun login(email: String, password: String): LoginResult {
        val loginRequest = LoginRequest(email, password)
        val client = userApiConfig.getApiService.login(loginRequest)

        return suspendCoroutine { continuation ->
            client.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val token = responseBody.token
                        token?.let { LoginResult.Success(it) }?.let { continuation.resume(it) }
                    } else {
                        try {
                            val errorResponse = Gson().fromJson(
                                response.errorBody()?.charStream(),
                                LoginResponse::class.java
                            )
                            val errorMessage = errorResponse?.message ?: "Unknown error occurred"
                            Log.e(TAG, "onResponse error: $errorMessage")
                            continuation.resume(LoginResult.Error(errorMessage))
                        } catch (e: Exception) {
                            val errorMessage = "Unknown error occurred"
                            Log.e(TAG, "onFailure: ${response.message()}")
                            continuation.resume(LoginResult.Error(errorMessage))
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    val errorMessage = t.message.toString()
                    Log.e(TAG, "onFailure: $errorMessage")
                    continuation.resume(LoginResult.Error(errorMessage))
                }
            })
        }
    }

    suspend fun getUser(token: String): UserResult {
        val client = userApiConfig.getApiService.getUser(token)

        return suspendCoroutine { continuation ->
            client.enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val result = response.body()
                        result?.let { UserResult.Success(it) }?.let { continuation.resume(it) }
                    } else {
                        val errorMessage = "Unknown error occurred"
                        Log.e(TAG, "onFailure: ${response.message()}")
                        continuation.resume(UserResult.Error(errorMessage))
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    val errorMessage = t.message.toString()
                    Log.e(TAG, "onFailure: $errorMessage")
                    continuation.resume(UserResult.Error(errorMessage))
                }
            })
        }
    }

    suspend fun getTotalPoints(token: String): PointsResult {
        val client = userApiConfig.getApiService.getTotalPoints(token)

        return suspendCoroutine { continuation ->
            client.enqueue(object : Callback<PointsResponse> {
                override fun onResponse(
                    call: Call<PointsResponse>,
                    response: Response<PointsResponse>
                ) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val result = response.body()
                        result?.let { PointsResult.Success(it) }?.let { continuation.resume(it) }
                    } else {
                        val errorMessage = "Unknown error occurred"
                        Log.e(TAG, "onFailure: ${response.message()}")
                        continuation.resume(PointsResult.Error(errorMessage))
                    }
                }

                override fun onFailure(call: Call<PointsResponse>, t: Throwable) {
                    val errorMessage = t.message.toString()
                    Log.e(TAG, "onFailure: $errorMessage")
                    continuation.resume(PointsResult.Error(errorMessage))
                }
            })
        }
    }

    suspend fun addPoints(token: String, type: Int): AddPointsResult {
        val client = userApiConfig.getApiService.addPoints(token, type)
        return suspendCoroutine { continuation ->
            client.enqueue(object : Callback<AddPointsResponse> {
                override fun onResponse(
                    call: Call<AddPointsResponse>,
                    response: Response<AddPointsResponse>
                ) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val result = response.body()
                        result?.let { AddPointsResult.Success(it) }?.let { continuation.resume(it) }
                    } else {
                        val errorMessage = "Unknown error occurred"
                        Log.e(TAG, "onFailure: ${response.message()}")
                        continuation.resume(AddPointsResult.Error(errorMessage))
                    }
                }

                override fun onFailure(call: Call<AddPointsResponse>, t: Throwable) {
                    val errorMessage = t.message.toString()
                    Log.e(TAG, "onFailure: $errorMessage")
                    continuation.resume(AddPointsResult.Error(errorMessage))
                }
            })
        }
    }

    suspend fun savePoints(token: String): AddPointsResult {
        val client = userApiConfig.getApiService.savePoints(token)

        return suspendCoroutine { continuation ->
            client.enqueue(object : Callback<AddPointsResponse> {
                override fun onResponse(
                    call: Call<AddPointsResponse>,
                    response: Response<AddPointsResponse>
                ) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val result = response.body()
                        result?.let { AddPointsResult.Success(it) }?.let { continuation.resume(it) }
                    } else {
                        val errorMessage = "Unknown error occurred"
                        Log.e(TAG, "onFailure: ${response.message()}")
                        continuation.resume(AddPointsResult.Error(errorMessage))
                    }
                }

                override fun onFailure(call: Call<AddPointsResponse>, t: Throwable) {
                    val errorMessage = t.message.toString()
                    Log.e(TAG, "onFailure: $errorMessage")
                    continuation.resume(AddPointsResult.Error(errorMessage))
                }
            })
        }
    }

    suspend fun getPointHistory(token: String): HistoryResult {
        val client = userApiConfig.getApiService.getPointHistory(token)

        return suspendCoroutine { continuation ->
            client.enqueue(object : Callback<List<HistoryResponse>> {
                override fun onResponse(
                    call: Call<List<HistoryResponse>>,
                    response: Response<List<HistoryResponse>>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            continuation.resume(HistoryResult.Success(responseBody))
                        } else {
                            val errorMessage = "Unknown error occurred"
                            Log.e(TAG, "onResponse: Response body is null")
                            continuation.resume(HistoryResult.Error(errorMessage))
                        }
                    } else {
                        val errorMessage = "Failed to fetch history: ${response.code()}"
                        Log.e(TAG, "onResponse: $errorMessage")
                        continuation.resume(HistoryResult.Error(errorMessage))
                    }
                }

                override fun onFailure(call: Call<List<HistoryResponse>>, t: Throwable) {
                    val errorMessage = "Failed to fetch history: ${t.message}"
                    Log.e(TAG, "onFailure: $errorMessage")
                    continuation.resume(HistoryResult.Error(errorMessage))
                }
            })
        }
    }


    sealed class ArticlesResult {
        data class Success(val articles: ArrayList<Articles>) : ArticlesResult()
        data class Error(val message: String) : ArticlesResult()
    }

    sealed class RegisterResult {
        data class Success(val message: String?) : RegisterResult()
        data class Error(val message: String) : RegisterResult()
    }

    sealed class LoginResult {
        data class Success(val token: String) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    sealed class UserResult {
        data class Success(val user: UserResponse) : UserResult()
        data class Error(val message: String) : UserResult()
    }

    sealed class PointsResult {
        data class Success(val points: PointsResponse) : PointsResult()
        data class Error(val message: String) : PointsResult()
    }

    sealed class AddPointsResult {
        data class Success(val response: AddPointsResponse) : AddPointsResult()
        data class Error(val message: String) : AddPointsResult()
    }

    sealed class HistoryResult {
        data class Success(val history: List<HistoryResponse?>) : HistoryResult()
        data class Error(val message: String) : HistoryResult()
    }

    companion object {
        private const val TAG = "Repository"

        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            articlesApiConfig: ArticlesApiConfig,
            userApiConfig: UserApiConfig
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(articlesApiConfig, userApiConfig)
            }.also { instance = it }
    }
}