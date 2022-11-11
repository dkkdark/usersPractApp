package com.kseniabl.userstasks.models

import com.google.gson.annotations.SerializedName

data class AddUserModel(
    @SerializedName("master_password")
    val secretKey: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("role_name")
    val rolename: String,
    @SerializedName("user_name")
    val username: String
)