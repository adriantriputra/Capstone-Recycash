package com.adrian.recycash.data.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("message") var message: String? = null,
    @SerializedName("user") var user: User? = User()
)

data class User(
    @SerializedName("id_user") var idUser: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("phone_number") var phoneNumber: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("total_point") var totalPoint: String? = null
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone_number: String,
    val password: String,
)