package com.kseniabl.userstasks.models

import com.google.gson.annotations.SerializedName

data class UserLoginModel(
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("password")
    val password: String
)
