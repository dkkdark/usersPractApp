package com.kseniabl.userstasks.models

import com.google.gson.annotations.SerializedName

data class ReportBody(
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("user_name")
    val username: String
)
