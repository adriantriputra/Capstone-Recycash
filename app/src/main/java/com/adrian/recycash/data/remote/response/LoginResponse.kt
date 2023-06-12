package com.adrian.recycash.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("message") var message: String? = null,
    @SerializedName("token") var token: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)