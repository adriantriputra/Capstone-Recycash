package com.adrian.recycash.data.remote.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(
    @SerializedName("point_amount") var pointAmount: Int? = null,
    @SerializedName("poin_type") var poinType: String? = null
)
