package com.adrian.recycash.data.remote.retrofit

import com.adrian.recycash.data.remote.response.AddPointsResponse
import com.adrian.recycash.data.remote.response.HistoryResponse
import com.adrian.recycash.data.remote.response.LoginRequest
import com.adrian.recycash.data.remote.response.LoginResponse
import com.adrian.recycash.data.remote.response.PointsResponse
import com.adrian.recycash.data.remote.response.RegisterRequest
import com.adrian.recycash.data.remote.response.RegisterResponse
import com.adrian.recycash.data.remote.response.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserApiService {
    @POST("register")
    fun register(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>

    @POST("login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @GET("user")
    fun getUser(
        @Header("Authorization") token: String
    ): Call<UserResponse>

    @GET("gettotalpoint")
    fun getTotalPoints(
        @Header("Authorization") token: String
    ): Call<PointsResponse>

    @POST("point_record/1")
    fun addPoints(
        @Header("Authorization") token: String
    ): Call<AddPointsResponse>

    @PUT("totalpoint")
    fun savePoints(
        @Header("Authorization") token: String
    ): Call<AddPointsResponse>

    @GET("listpointhistory")
    fun getPointHistory(
        @Header("Authorization") token: String
    ): Call<List<HistoryResponse>>
}