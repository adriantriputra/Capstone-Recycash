package com.adrian.recycash.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("phone_number") var phoneNumber: String? = null
)