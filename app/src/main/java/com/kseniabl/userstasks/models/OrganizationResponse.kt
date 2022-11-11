package com.kseniabl.userstasks.models

import com.google.gson.annotations.SerializedName

data class OrganizationResponse (
    @SerializedName("city")
    val city: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("is_current")
    val isCurrent: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("organization_id")
    val id: Int,
    @SerializedName("phone_number")
    val phoneNum: String,
    @SerializedName("post_address")
    val postAddress: String
)