package com.adrian.recycash.data.remote.response

import com.google.gson.annotations.SerializedName

data class PointsResponse(
    @SerializedName("total_point") var totalPoint: Int? = null
)

data class AddPointsResponse(
    @SerializedName("error") var error: String? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("statusCode") var statusCode: Int? = null
)