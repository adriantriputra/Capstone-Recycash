package com.adrian.recycash.data.remote.retrofit

import com.adrian.recycash.data.remote.response.RegisterRequest
import com.adrian.recycash.data.remote.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {

    @POST("register")
    fun register(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>

}