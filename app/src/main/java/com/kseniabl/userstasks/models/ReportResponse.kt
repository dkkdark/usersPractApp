package com.kseniabl.userstasks.models

import com.google.gson.annotations.SerializedName

data class ReportResponse (
    @SerializedName("first_select")
    val firstSelect: Int,
    @SerializedName("fourth_select")
    val fourthSelect: Int,
    @SerializedName("main_select")
    val mainSelect: Int,
    @SerializedName("second_select")
    val secondSelect: Int,
    @SerializedName("tird_select")
    val thirdSelect: Int
)