package com.kseniabl.userstasks.models

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error_message")
    val error: String
)
